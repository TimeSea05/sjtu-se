import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // Correctness of Linear HashTable
        // small test
//        LinearHashTable hashTable = new LinearHashTable();
//        TestExecutor executor = new TestExecutor("./test/small.in", "./test/small.ans", hashTable);
//        executor.CorrectnessTest();
        // large test
//        LinearHashTable hashTable = new LinearHashTable();
//        TestExecutor executor = new TestExecutor("./test/large.in", "./test/large.ans", hashTable);
//        executor.CorrectnessTest();

        // Correctness of Cuckoo HashTable
        // small test
//        CuckooHashTable hashTable = new CuckooHashTable();
//        TestExecutor executor = new TestExecutor("./test/small.in", "./test/small.ans", hashTable);
//        executor.CorrectnessTest();
        // large test
//        CuckooHashTable hashTable = new CuckooHashTable();
//        TestExecutor executor = new TestExecutor("./test/large.in", "./test/large.ans", hashTable);
//        executor.CorrectnessTest();

        // Performance of Linear Hashtable
//        LinearHashTable hashTable = new LinearHashTable();
//        TestExecutor executor = new TestExecutor("./test/linear_delay.txt", hashTable);
//        executor.PerformanceTest();

        // Performance of Cuckoo Hashtable
//        CuckooHashTable hashTable = new CuckooHashTable();
//        TestExecutor executor = new TestExecutor("./test/cuckoo_delay.txt", hashTable);
//        executor.PerformanceTest();

        // GUI App
        CuckooHashTable hashTable = new CuckooHashTable();
        hashTable.Set(1, 1);
        hashTable.Set(9, 1);
        SwingUtilities.invokeLater(() -> new CuckooGUIApp(hashTable));
    }
}