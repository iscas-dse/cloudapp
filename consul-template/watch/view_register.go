package watch
import (
	"fmt"
	"github.com/hashicorp/consul/api"
	"log"
	dep "github.com/hashicorp/consul-template/dependency"
	"time"
	"net"
)

const(
	REGISTER_PREFIX = "listener/"
	REGISTER_PORT = "9001"
)

type DependencyRegister struct{
	config *RegisterConfig
	uuid string
}

type RegisterConfig struct {
	Addr string
	Port string
}

func NewDependencyRegister(registerConfig *RegisterConfig) *DependencyRegister{
	return &DependencyRegister{
		config: registerConfig,
		uuid: time.Now().String(),
	}
}

func (register *DependencyRegister) register(v *View) error{
	if v == nil {
		return fmt.Errorf("register method: view can't be nil")
	}
	if _, ok := v.Dependency.(*dep.StoreKey); !ok{
		return nil
	}
	clients := v.config.Clients
	consul, err := clients.Consul()
	if err != nil {
		return fmt.Errorf("register dependency: error getting client %s", err)
	}
	stores := consul.KV()
	key, err := register.genenateKey(v.Dependency)
	if err != nil{
		log.Printf("%s", err)
		return nil
	}
	value := register.config.value()
	p := &api.KVPair{Key: key, Flags: 0, Value: []byte(value)}
	if _, err = stores.Put(p, nil); err != nil {
		log.Printf("Invalid key not detected: %s", key)
	}
	return err
}

//	case *dep.CatalogNode:
//	case *dep.CatalogNodes:
//	case *dep.CatalogServices:
//	case *dep.Datacenters:
//	case *dep.File:
//	case *dep.HealthServices:
//	case *dep.StoreKeyPrefix:
func (register *DependencyRegister) genenateKey(d dep.Dependency)(string, error){
	if d == nil{
		return "", fmt.Errorf("dependency can't be nil")
	}
	result := REGISTER_PREFIX
	switch trueValue := d.(type){
	case *dep.StoreKey:
		result += trueValue.Path + "/" + register.uuid
		return result, nil
	}
	return "", fmt.Errorf("now can't gennenate key for %s", d.Display())
}

func (config *RegisterConfig) value() string{
	return config.Addr + ":" + config.Port
}

func (v *View) register() error{
	ip, err := externalIP()
	if err != nil {
		return err
	}
	registerConfig := &RegisterConfig{
		Addr: ip,
		Port:REGISTER_PORT,
	}
	register := NewDependencyRegister(registerConfig)
	return register.register(v)
}

func externalIP() (string, error) {
	ifaces, err := net.Interfaces()
	if err != nil {
		return "", err
	}
	for _, iface := range ifaces {
		if iface.Flags&net.FlagUp == 0 {
			continue // interface down
		}
		if iface.Flags&net.FlagLoopback != 0 {
			continue // loopback interface
		}
		addrs, err := iface.Addrs()
		if err != nil {
			return "", err
		}
		for _, addr := range addrs {
			var ip net.IP
			switch v := addr.(type) {
			case *net.IPNet:
				ip = v.IP
			case *net.IPAddr:
				ip = v.IP
			}
			if ip == nil || ip.IsLoopback() {
				continue
			}
			ip = ip.To4()
			if ip == nil {
				continue // not an ipv4 address
			}
			return ip.String(), nil
		}
	}
	return "", fmt.Errorf("are you connected to the network?")
}