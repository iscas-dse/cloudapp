package participant

import (
	"fmt"
	"log"
	pro "participant/propagation"
	"time"
)

type PropagationServiceHandler struct {
	event          *pro.PropagationEvent
	db             *Repository
	timer          *time.Timer
	checkpointFunc CheckpointFunc
	commitFunc     CommitFunc
}

func NewPropagationServiceHandler() (handler *PropagationServiceHandler) {
	handler = &PropagationServiceHandler{timer: time.NewTimer(time.Second * 10)}
	return
}

func (handler *PropagationServiceHandler) Propagate(event *pro.PropagationEvent) (r *pro.PropagationResult_, err error) {
	handler.event = event
	handler.db = NewRepository()
	fmt.Printf("propagate method called event %s\n", event.EventKey)
	result := &pro.PropagationResult_{Code: pro.PropagationResultCode_EVENT_ACK,
		Info: "success"}
	fmt.Printf("Propagate success")
	return result, nil
}

func (handler *PropagationServiceHandler) Prepare() (r *pro.PropagationResult_, err error) {
	fmt.Printf("prepare method called event %s\n", handler.event)
	encodedKey := []byte(handler.event.EventKey)
	fmt.Printf("1")
	commitFunc, checkpointFunc, err := handler.db.Prepare(encodedKey, handler.event.EventValue)
	fmt.Printf("2")
	handler.commitFunc = commitFunc
	handler.checkpointFunc = checkpointFunc
	fmt.Printf("3")
	if err != nil {
		log.Printf("repository db prepare failed error %s", err)
		return &pro.PropagationResult_{Code: pro.PropagationResultCode_ROLLBACK,
			Info: err.Error()}, err
	}
	return &pro.PropagationResult_{Code: pro.PropagationResultCode_EVENT_ACK,
		Info: "success"}, nil
}

// Parameters:
//  - OnePhase
func (handler *PropagationServiceHandler) Commit(onePhase bool) (r *pro.PropagationResult_, err error) {
	fmt.Printf("commit method called event %s\n", handler.event)
	err = handler.commitFunc()
	if err != nil {
		return &pro.PropagationResult_{Code: pro.PropagationResultCode_HEUR_HAZARD,
			Info: "commit failed"}, err
	}
	return &pro.PropagationResult_{Code: pro.PropagationResultCode_EVENT_ACK,
		Info: "success"}, nil
}

func (handler *PropagationServiceHandler) Rollback() (r *pro.PropagationResult_, err error) {
	fmt.Printf("rollback method called event %s\n", handler.event)
	err = handler.checkpointFunc()
	if err != nil {
		return &pro.PropagationResult_{Code: pro.PropagationResultCode_HEUR_HAZARD,
			Info: "roll back failed"}, err
	}
	return &pro.PropagationResult_{Code: pro.PropagationResultCode_EVENT_ACK,
		Info: "success"}, nil
}
