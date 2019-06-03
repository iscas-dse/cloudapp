package dependency

import (
	"encoding/gob"
	"errors"
	"fmt"
	"log"
	"regexp"
	"strings"
)

func init() {
	gob.Register([]*KeyPair{})
}

// KeyPair is a simple Key-Value pair
type KeyPair struct {
	Path  string
	Key   string
	Value string

	// Lesser-used, but still valuable keys from api.KV
	CreateIndex uint64
	ModifyIndex uint64
	LockIndex   uint64
	Flags       uint64
	Session     string
}

// StoreKeyPrefix is the representation of a requested key dependency
// from inside a template.
type StoreKeyPrefix struct {
	rawKey     string
	Prefix     string
	DataCenter string
}

// Fetch queries the Consul API defined by the given client and returns a slice
// of KeyPair objects
func (d *StoreKeyPrefix) Fetch(clients *ClientSet, opts *QueryOptions) (interface{}, *ResponseMetadata, error) {
	if opts == nil {
		opts = &QueryOptions{}
	}

	consulOpts := opts.consulQueryOptions()
	if d.DataCenter != "" {
		consulOpts.Datacenter = d.DataCenter
	}

	log.Printf("[DEBUG] (%s) querying Consul with %+v", d.Display(), consulOpts)

	consul, err := clients.Consul()
	if err != nil {
		return nil, nil, fmt.Errorf("store key prefix: error getting client: %s", err)
	}

	store := consul.KV()
	prefixes, qm, err := store.List(d.Prefix, consulOpts)
	if err != nil {
		return nil, nil, fmt.Errorf("store key prefix: error fetching: %s", err)
	}

	log.Printf("[DEBUG] (%s) Consul returned %d key pairs", d.Display(), len(prefixes))

	keyPairs := make([]*KeyPair, 0, len(prefixes))
	for _, pair := range prefixes {
		key := strings.TrimPrefix(pair.Key, d.Prefix)
		key = strings.TrimLeft(key, "/")

		keyPairs = append(keyPairs, &KeyPair{
			Path:        pair.Key,
			Key:         key,
			Value:       string(pair.Value),
			CreateIndex: pair.CreateIndex,
			ModifyIndex: pair.ModifyIndex,
			LockIndex:   pair.LockIndex,
			Flags:       pair.Flags,
			Session:     pair.Session,
		})
	}

	rm := &ResponseMetadata{
		LastIndex:   qm.LastIndex,
		LastContact: qm.LastContact,
	}

	return keyPairs, rm, nil
}

func (d *StoreKeyPrefix) CanShare() bool {
	return true
}

func (d *StoreKeyPrefix) HashCode() string {
	return fmt.Sprintf("StoreKeyPrefix|%s", d.rawKey)
}

func (d *StoreKeyPrefix) Display() string {
	return fmt.Sprintf(`"storeKeyPrefix(%s)"`, d.rawKey)
}

// ParseStoreKeyPrefix parses a string of the format a(/b(/c...))
func ParseStoreKeyPrefix(s string) (*StoreKeyPrefix, error) {
	// a(/b(/c))(@datacenter)
	re := regexp.MustCompile(`\A` +
		`(?P<prefix>[[:word:]\.\:\-\/]+)?` +
		`(@(?P<datacenter>[[:word:]\.\-]+))?` +
		`\z`)
	names := re.SubexpNames()
	match := re.FindAllStringSubmatch(s, -1)

	if len(match) == 0 {
		return nil, errors.New("invalid key prefix dependency format")
	}

	r := match[0]

	m := map[string]string{}
	for i, n := range r {
		if names[i] != "" {
			m[names[i]] = n
		}
	}

	prefix, datacenter := m["prefix"], m["datacenter"]

	kpd := &StoreKeyPrefix{
		rawKey:     s,
		Prefix:     prefix,
		DataCenter: datacenter,
	}

	return kpd, nil
}
