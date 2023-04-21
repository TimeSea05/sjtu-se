public class LinearHashTable implements HashTable {
    private HashTableEntry[] table;
    private int nElem;
    private int cap;
    final int INITIAL_CAP = 8;
    public LinearHashTable() {
        table = new HashTableEntry[INITIAL_CAP];
        nElem = 0;
        cap = INITIAL_CAP;
    }
    private int hashFunc(int key) {
        return key % cap;
    }
    private void expandCap() {
        cap *= 2;
        rehash();
    }
    private void rehash() {
        HashTableEntry[] oldTable = table;
        table = new HashTableEntry[cap];

        nElem = 0;
        for (HashTableEntry entry: oldTable) {
            if (entry != null && !entry.deleted) {
                Set(entry.key, entry.value);
            }
        }
    }
    public void Set(int key, int value) {
        if (nElem >= cap / 2) {
            expandCap();
        }

        int pos = hashFunc(key);
        int firstTombstone = -1;

        while (table[pos] != null) {
            if (table[pos].deleted && firstTombstone == -1) {
                firstTombstone = pos;
            }
            if (table[pos].key == key && !table[pos].deleted) {
                table[pos].value = value;
                return;
            }
            pos = (pos + 1) % cap;
        }
        if (firstTombstone != -1) {
            table[firstTombstone] = new HashTableEntry(key, value);
        } else {
            table[pos] = new HashTableEntry(key, value);
            nElem++;
        }
    }

    public String Get(int key) {
        int pos = hashFunc(key);
        while (table[pos] != null) {
            if (table[pos].key == key && !table[pos].deleted) {
                return Integer.toString(table[pos].value);
            }
            pos = (pos + 1) % cap;
        }
        return "null";
    }

    public void Delete(int key) {
        int pos = hashFunc(key);
        while (table[pos] != null) {
            if (table[pos].key == key) {
                table[pos].deleted = true;
            }
            pos = (pos + 1) % cap;
        }
    }
}
