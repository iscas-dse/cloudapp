package dependency
import (
	"testing"
	pro "participant"
)

func TestStoreKeyFind(t *testing.T){
	repository := pro.NewRepository()
	repository.Store([]byte("foo"), "bar")
	defer repository.Delete([]byte("foo"))

	dep := &StoreKey{rawKey: "foo", Path: "foo"}

	clients, consul := testConsulServer(t)
	defer consul.Stop()

	consul.SetKV("foo", []byte("bar"))

	results, _, err := dep.Find(clients, nil)

	if err != nil {
		t.Fatal(err)
	}

	_, ok := results.(string)
	if !ok {
		t.Fatal("could not convert result to string")
	}
}
