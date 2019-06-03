package participant

import (
	"git.apache.org/thrift.git/lib/go/thrift"
	"fmt"
	
)

func maintest() {
	protocolFactory := thrift.NewTBinaryProtocolFactoryDefault()
	transportFactory := thrift.NewTTransportFactory()
	addr := "127.0.0.1:9090"

	transport, err := thrift.NewTServerSocket(addr)
	if err != nil {
		return
	}
	processoFactory := NewTThreadProcessorFactory()
	server := thrift.NewTSimpleServerFactory4(processoFactory, transport, transportFactory, protocolFactory)

	fmt.Println("Starting the simple server... on ", addr)
	err = server.Serve()
	if err != nil {
		return
	}
}
