package participant

import (
	"fmt"
	"testing"
	"time"
)

func TestNewRepository(t *testing.T) {
	repository := NewRepository()
	if repository.dbName == "" {
		t.Errorf("repository's dbname can't be nil")
	}
}

func TestStore(t *testing.T) {
	repository := NewRepository()
	key := []byte("testKey")
	value := "testValue"
	err := repository.Store(key, value)
	if err != nil {
		t.Errorf("repository store failed %s\n", err)
	}
	if err := repository.Store(nil, value); err == nil {
		t.Errorf("should check key before store")
	}
}
func TestGet(t *testing.T) {
	repository := NewRepository()
	key := []byte("testKey")
	data, err := repository.Get(key)
	if err != nil {
		t.Fatal("repository get failed", err)
	}
	fmt.Println(data)
	fact, ok := data.Data.(string)
	if !ok {
		t.Fatal("fact data is not type []byte")
	}
	if fact != "testValue" {
		t.Errorf("repository get failed, get a wrong value. fact: %s, expect:%s", fact, "testValue")
	}
}

func TestGetNilKey(t *testing.T) {
	repository := NewRepository()
	if _, err := repository.Get(nil); err == nil {
		t.Fatal("expect a error, but nothing was returned")
	}
}

func TestGetWithTimeout(t *testing.T) {
	repository1 := NewRepository()
	repository2 := NewRepository()
	key := []byte("testKey")
	data, err := repository1.Get(key)
	if err != nil {
		t.Fatal("repository get failed", err)
	}
	waitIndex, waitTime := data.LastIndex, time.Second*10
	repository1.GetWithTimeout(key, waitIndex, waitTime)
	repository2.Store(key, "testValueFromGetWithTimeout")
}
