package watch
import (
	"testing"
	"fmt"
	"github.com/hashicorp/consul-template/dependency"
	"github.com/hashicorp/consul/testutil"
	"io/ioutil"
	consulapi "github.com/hashicorp/consul/api"
)

func TestNewRegister(t *testing.T){
	config := &RegisterConfig{
		Addr: "localhost",
		Port: "9090",
	}
	register := NewDependencyRegister(config)
	fmt.Printf(register.uuid)
	if register.config.value() != "localhost:9090" {
		t.Errorf("expected localhost:9090, but the true value is %s", register.config.value())
	}
}

func TestRegisterNoView(t *testing.T){
	config := &RegisterConfig{
		Addr: "localhost",
		Port: "9090",
	}
	register := NewDependencyRegister(config)
	if err := register.register(nil); err == nil{
		t.Errorf("expect a error, but nothing was returned")
	}
}

func TestRegisterWithView(t *testing.T){
	config, dep := defaultWatcherConfig, &dependency.StoreKey{Path: "foo"}

	clients, consul := testConsulServer(t)
	defer consul.Stop()

	config.Clients = clients

	view, _ := NewView(config, dep)

	registerConfig := &RegisterConfig{
		Addr: "localhost",
		Port: "9090",
	}
	register := NewDependencyRegister(registerConfig)

	if err := register.register(view); err != nil{
		t.Errorf("%s", err)
	}
}

func TestGenenateNoDependency(t *testing.T){
	config := &RegisterConfig{
		Addr: "localhost",
		Port: "9090",
	}
	register := NewDependencyRegister(config)
	_, err := register.genenateKey(nil)
	if err == nil {
		t.Errorf("expect a error, but nothing was returned")
	}
}

func TestGenenateKeyDependency(t *testing.T) {
	d, err := dependency.ParseStoreKey("config/redis/maxconns")
	if err != nil {
		t.Errorf("parse store key failed")
	}
	config := &RegisterConfig{
		Addr: "localhost",
		Port: "9090",
	}
	register := NewDependencyRegister(config)
	key, err := register.genenateKey(d)
	if err != nil {
		t.Errorf("genenate key failed")
	}
	expected := "listener/config/redis/maxconns/" + register.uuid
	if key != expected {
		t.Errorf("expected is %s, but true value is %s", expected, key)
	}
}

func testConsulServer(t *testing.T) (*dependency.ClientSet, *testutil.TestServer) {
	t.Parallel()

	consul := testutil.NewTestServerConfig(t, func(c *testutil.TestServerConfig) {
		c.Stdout = ioutil.Discard
		c.Stderr = ioutil.Discard
	})

	config := consulapi.DefaultConfig()
	config.Address = consul.HTTPAddr
	client, err := consulapi.NewClient(config)
	if err != nil {
		consul.Stop()
		t.Fatalf("consul api client err: %s", err)
	}

	clients := dependency.NewClientSet()
	if err := clients.Add(client); err != nil {
		consul.Stop()
		t.Fatalf("clientset err: %s", err)
	}

	return clients, consul
}
