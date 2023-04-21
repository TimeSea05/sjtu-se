import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class CuckooHashTable implements HashTable{
    private HashTableEntry[] table1;
    private HashTableEntry[] table2;
    private int cap;
    public static final int INITIAL_CAP = 8;
    private int hashFunc1(int key) {
        return key % cap;
    }
    private int hashFunc2(int key) {
        return (key / cap) % cap;
    }
    public CuckooHashTable() {
        cap = INITIAL_CAP;
        table1 = new HashTableEntry[cap];
        table2 = new HashTableEntry[cap];
    }
    private void expandCap() {
        cap *= 2;
        rehash();
    }
    private void rehash() {
        HashTableEntry[] oldTable1 = table1;
        HashTableEntry[] oldTable2 = table2;

        table1 = new HashTableEntry[cap];
        table2 = new HashTableEntry[cap];

        for (HashTableEntry entry: oldTable1) {
            if (entry != null) {
                Set(entry.key, entry.value);
            }
        }
        for (HashTableEntry entry: oldTable2) {
            if (entry != null) {
                Set(entry.key, entry.value);
            }
        }
    }
    private static String elemPosition(int pos, int tableID) {
        return String.format("%d-%d", pos, tableID);
    }
    private boolean evict(int pos, int tableID, Set<String> s) {
        String elemPosStr = elemPosition(pos, tableID);
        if (s.contains(elemPosStr)) {
            return false;
        }
        s.add(elemPosStr);

        HashTableEntry[] table = (tableID == 1) ? table1 : table2;
        int key = table[pos].key;

        HashTableEntry[] nextTable = (tableID == 1) ? table2 : table1;
        int nextTableID = (tableID == 1) ? 2 : 1;
        Function<Integer, Integer> nextHashFunc = (tableID == 1) ? this::hashFunc2 : this::hashFunc1;

        int nextPos = nextHashFunc.apply(key);
        if (nextTable[nextPos] == null || evict(nextPos, nextTableID, s)) {
            nextTable[nextPos] = table[pos];
            table[pos] = null;
            return true;
        }

        return false;
    }

    public void Set(int key, int value) {
        int pos1 = hashFunc1(key), pos2 = hashFunc2(key);
        if (table1[pos1] != null && table1[pos1].key == key) {
            table1[pos1].value = value;
            return;
        }
        if (table2[pos2] != null && table2[pos2].key == key) {
            table2[pos2].value = value;
            return;
        }

        while (true) {
            int pos = hashFunc1(key);
            if (table1[pos] == null) {
                break;
            }

            Set<String> s = new HashSet<>();
            if (!evict(pos, 1, s)) {
                expandCap();
                continue;
            }
            break;
        }
        table1[hashFunc1(key)] = new HashTableEntry(key, value);
    }
    public String Get(int key) {
        int pos1 = hashFunc1(key), pos2 = hashFunc2(key);
        if (table1[pos1] != null && table1[pos1].key == key) {
            return Integer.toString(table1[pos1].value);
        }
        if (table2[pos2] != null && table2[pos2].key == key) {
            return Integer.toString(table2[pos2].value);
        }
        return "null";
    }
    public void Delete(int key) {
        int pos1 = hashFunc1(key), pos2 = hashFunc2(key);
        if (table1[pos1] != null && table1[pos1].key == key) {
            table1[pos1] = null;
            return;
        }
        if (table2[pos2] != null && table2[pos2].key == key) {
            table2[pos2] = null;
        }
    }

    public HashTableEntry[][] GetInnerTables() {
        HashTableEntry[][] innerTable = new HashTableEntry[2][];
        innerTable[0] = table1.clone();
        innerTable[1] = table2.clone();
        return innerTable;
    }
}
