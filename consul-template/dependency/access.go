package dependency
import (
	"participant"
	"log"
	"fmt"
)

type DataAccess interface {
	Find(*ClientSet, *QueryOptions) (interface{}, *ResponseMetadata, error)
}

func (storekey *StoreKey) Find(clients *ClientSet,
	opts *QueryOptions)(interface{}, *ResponseMetadata, error){
	repository := participant.NewRepository()
	key := []byte(storekey.Path)

	log.Printf("[Debug] query value of key(%s)", storekey.Path)

	var meta participant.TableMeta
	var err error
	meta, err = repository.GetWithTimeout(key, opts.WaitIndex, opts.WaitTime)

	if err != nil{
		log.Printf("(acess) repository get error: %s", err)
		return nil, nil, err
	}

	rm := &ResponseMetadata{
		LastIndex: meta.LastIndex,
		LastContact: meta.LastContact,
	}

	if meta.Data == nil {
		log.Printf("[WARN] (%s) repository returned no data", storekey.Path)
		data , rm , err := storekey.Fetch(clients, opts)
		if err != nil {
			return "", rm, err
		}
		if data == nil {
			return data, rm, fmt.Errorf("no value for key(%s)", string(key))
		}
		err = repository.Store(key, data)
		if err != nil {
			return "", rm, err
		}
		return storekey.Find(clients, opts)
	}

	log.Printf("[Debug] repository returned %s", meta.Data)

	var value string
	var ok bool
	if value, ok = meta.Data.(string); !ok {
		return "", rm, fmt.Errorf("repository returned %s is not string type", meta.Data)
	}
	return value, rm, nil
}