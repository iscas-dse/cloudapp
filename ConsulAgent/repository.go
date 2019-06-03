package participant

import (
	"container/list"
	"fmt"
	"github.com/boltdb/bolt"
	"github.com/jfrazelle/go/canonical/json"
	"log"
	"sync"
	"time"
)

type Repository struct {
	dbName     string
	bucketName []byte
	listeners  map[string]*list.List
}

//for singleton pattern
var instance *Repository
var once sync.Once

func NewRepository() *Repository {
	once.Do(func() {
		repoDB := "/opt/agent/bolt/mydb"
		instance = &Repository{dbName: repoDB, bucketName: []byte("keyvalues")}
		instance.listeners = make(map[string]*list.List)
	})
	return instance
}

type TableMeta struct {
	Data interface{} `json:"data"`
	// LastIndex. This can be used as a WaitIndex to perform
	// a blocking query
	LastIndex uint64 `json:"LastIndex"`
	// Time of last contact from the leader for the
	// server servicing the request
	LastContact time.Duration `json:"LastContract"`
}

type CheckpointFunc func() error
type CommitFunc func() error
type TriggerFunc func(meta TableMeta) error

//all listener just can listen only one event of this key, if a event has been  fired to listener,
// this listener will be deleted from this repo
func (repo *Repository) AddListener(key []byte, trigger TriggerFunc) *list.Element {
	log.Printf("[INFO] add listener for key(%s)", string(key))
	triggers := repo.listeners[string(key)]
	if triggers == nil {
		triggers = list.New()
		repo.listeners[string(key)] = triggers
	}
	return triggers.PushBack(trigger)
}

func (repo *Repository) deleteListener(key []byte, element *list.Element) {
	triggers := repo.listeners[string(key)]
	triggers.Remove(element)
	log.Printf("[DEBUG] remove trigger from listeners")
}

//fire a event will delete all listener of this key
func (repo *Repository) fire(key []byte, meta TableMeta) {
	log.Printf("[INFO] fire new data(%s) for key(%s)", meta, string(key))
	triggers := repo.listeners[string(key)]
	if triggers == nil {
		return
	}
	for trigger := triggers.Front(); trigger != nil; trigger = trigger.Next() {
		if hook, ok := trigger.Value.(TriggerFunc); ok {
			hook(meta)
		}
	}
	triggers.Init()
}

func (repo *Repository) Prepare(key []byte, value string) (CommitFunc, CheckpointFunc, error) {
	oldValue, err := repo.Get(key)
	if err != nil {
		return nil, nil, err
	}
	commitFunc := func() error {
		return repo.Store(key, value)
	}
	checkpointFunc := func() error {
		return repo.StoreWithMeta(key, oldValue)
	}
	return commitFunc, checkpointFunc, nil
}

func (repo *Repository) Store(key []byte, value interface{}) error {
	if key == nil {
		return fmt.Errorf("key can't be null")
	}
	db, err := bolt.Open(repo.dbName, 0600, nil)
	if err != nil {
		return fmt.Errorf("open db faild", err)
	}
	defer db.Close()

	var data TableMeta
	err = db.Update(
		func(tx *bolt.Tx) error {
			bucket, err := tx.CreateBucketIfNotExists([]byte(repo.bucketName))
			if err != nil {
				return fmt.Errorf("create bucket: %s", err)
			}
			index, _ := bucket.NextSequence()
			data = TableMeta{Data: value, LastIndex: index, LastContact: time.Second}
			persist, err := json.Marshal(data)
			if err != nil {
				return err
			}
			bucket.Put(key, persist)
			log.Printf("persisit kv(key:%s, value:%s)", key, data)
			return nil
		})
	if err != nil {
		return err
	} else {
		repo.fire(key, data)
		return err
	}
}

func (repo *Repository) StoreWithMeta(key []byte, meta TableMeta) error {
	if key == nil {
		return fmt.Errorf("key can't be null")
	}
	db, err := bolt.Open(repo.dbName, 0600, nil)
	if err != nil {
		return fmt.Errorf("open db faild", err)
	}
	defer db.Close()

	err = db.Update(
		func(tx *bolt.Tx) error {
			bucket, err := tx.CreateBucketIfNotExists([]byte(repo.bucketName))
			if err != nil {
				return fmt.Errorf("create bucket: %s", err)
			}
			persist, err := json.Marshal(meta)
			if err != nil {
				return err
			}
			bucket.Put(key, persist)
			log.Printf("persist kv(key:%s,value:%s)", key, meta)
			return nil
		})
	if err != nil {
		return err
	} else {
		repo.fire(key, meta)
		return err
	}
}

func (repo *Repository) Delete(key []byte) (err error) {
	if key == nil {
		return fmt.Errorf("key can't be nil")
	}

	db, err := bolt.Open(repo.dbName, 0600, nil)
	if err != nil {
		return fmt.Errorf("open db failed", err)
	}
	defer db.Close()

	err = db.Update(func(tx *bolt.Tx) error {
		bucket, err := tx.CreateBucketIfNotExists([]byte(repo.bucketName))
		if err != nil {
			return err
		}
		if err := bucket.Delete(key); err != nil {
			return fmt.Errorf("delete key failed %s", err)
		}
		log.Printf("delete key(%s) success", string(key))
		return nil
	})
	return err
}

func (repo *Repository) Get(key []byte) (data TableMeta, err error) {
	if key == nil {
		return data, fmt.Errorf("key can't be nil")
	}

	db, err := bolt.Open(repo.dbName, 0600, nil)
	if err != nil {
		fmt.Errorf("open db faild", err)
	}
	log.Printf("[DEBUG] defer db close")
	defer db.Close()
	log.Printf("[DEBUG] defer db closed")
	
	err = db.View(func(tx *bolt.Tx) error {
		log.Printf("[DEBUG] Begin View (%s)",repo.bucketName)
		bucket := tx.Bucket(repo.bucketName)
		log.Printf("[DEBUG] instance bucket")
		if bucket == nil {
			log.Printf("[DEBUG] bucket is nil")
			return fmt.Errorf("[ERROR] bucket(%s) not exist", repo.bucketName)
		}
		log.Printf("[DEBUG] Get Value of Key(%s)", key)
		v := bucket.Get(key)
		if err := json.Unmarshal(v, &data); err != nil {
			return err
		}
		log.Printf("[INFO] Get value(%s) of key(%s)", data, key)
		return nil
	})
	return
}

func (repo *Repository) GetWithTimeout(key []byte, waitIndex uint64, waitTime time.Duration) (data TableMeta, err error) {
	if key == nil {
		return data, fmt.Errorf("(repository) key can't be nil")
	}
	data, err = repo.Get(key)
	if err != nil || data.LastIndex > waitIndex {
		return data, err
	}
	triggerCh := make(chan TableMeta)
	trigger := func(meta TableMeta) error {
		log.Printf("[INFO] trigger has been called, meta data is :%s", meta)
		timer := time.NewTimer(waitTime)
		select {
		case triggerCh <- meta:
			timer.Stop()
			return nil
		case <-timer.C:
			return fmt.Errorf("time out to insert %s to triggerCh", meta)
		}
	}
	funcEle := repo.AddListener(key, trigger)

	timer := time.NewTimer(waitTime)
	select {
	case data := <-triggerCh:
		log.Printf("[DEBUG] get new data(%s) for key(%s)", data, string(key))
		timer.Stop()
		return data, err
	case <-timer.C:
		repo.deleteListener(key, funcEle)
		log.Printf("[DEBUG] there are no new data which index after %d for key(%s)", waitIndex, string(key))
		return data, err
	}
}
