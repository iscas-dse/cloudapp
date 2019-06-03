package participant

import (
	"git.apache.org/thrift.git/lib/go/thrift"
	"log"
)

type PropagationServer struct {
	addr   string
	server *thrift.TSimpleServer
	ErrCh  chan error
}

type PropagationConfig struct {
	//server listen port
	Port string
	//server listen ip
	Addr string
}

func DefaultPropagationConfig() *PropagationConfig {
	addr := "0.0.0.0"
	port := "9001"
	config := &PropagationConfig{Port: port, Addr: addr}
	return config
}

func NewPropagationServer(config *PropagationConfig) (PropagationServer, error) {
	if config == nil {
		config = DefaultPropagationConfig()
	}
	var server PropagationServer
	protocolFactory := thrift.NewTBinaryProtocolFactoryDefault()
	transportFactory := thrift.NewTTransportFactory()
	addr := config.Addr + ":" + config.Port

	transport, err := thrift.NewTServerSocket(addr)
	if err != nil {
		return server, err
	}
	processoFactory := NewTThreadProcessorFactory()
	server = PropagationServer{}
	server.addr = addr
	server.server = thrift.NewTSimpleServerFactory4(processoFactory, transport, transportFactory, protocolFactory)
	server.ErrCh = make(chan error)
	return server, nil
}

func (server PropagationServer) Start() {
	log.Println("Starting the simple server... on ", server.addr)
	go server.server.Serve()
}

func (server PropagationServer) Stop() error {
	if err := server.server.Stop(); err != nil {
		return err
	}
	return nil
}
