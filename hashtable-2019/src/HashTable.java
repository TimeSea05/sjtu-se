class HashTableEntry {
    public int key;
    public int value;
    public boolean deleted;
    HashTableEntry(int key, int value) {
        this.key = key;
        this.value = value;
        this.deleted = false;
    }
}

public interface HashTable {
    public void Set(int key, int value);
    public String Get(int key);
    public void Delete(int key);
}
