package participant

import (
	"git.apache.org/thrift.git/lib/go/thrift"
	pro "participant/propagation"
)

type TThreadProcessorFactory struct {
}

func NewTThreadProcessorFactory() thrift.TProcessorFactory {
	return &TThreadProcessorFactory{}
}

func (factory *TThreadProcessorFactory) GetProcessor(trans thrift.TTransport) thrift.TProcessor {
	handler := NewPropagationServiceHandler()
	processor := pro.NewPropagationServiceProcessor(handler)
	return processor
}
