import javax.imageio.IIOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class TestExecutor {
    private HashTable table;
    // `inFile` and `ansFile` are used to test whether our hash table is implemented correctly
    // `inFile` stores hashtable operations, `ansFile` store correct answers
    private String inFile;
    private String ansFile;
    // `lineNum` indicates the line number of `ansFile` where errors occur
    private int lineNum;

    final private int RANDOM_KEY_VALUE_NUM = 10000;
    final private int PERFORMANCE_TEST_KEY_VALUE_NUM = 1000;
    final private int TEST_OPERATION_NUM = 10000;
    private String insertDelayOutputFile;
    public TestExecutor(String in, String ans, HashTable table) {
        this.inFile = in;
        this.ansFile = ans;
        this.table = table;
    }
    public TestExecutor(String delayOutput, HashTable table) {
        this.insertDelayOutputFile = delayOutput;
        this.table = table;
    }
    public void CorrectnessTest() {
        try {
            File input = new File(inFile), answer = new File(ansFile);
            Scanner inputScanner = new Scanner(input), ansScanner = new Scanner(answer);

            while (inputScanner.hasNextLine()) {
                lineNum++;

                String line = inputScanner.nextLine();
                String[] parts = line.split(" ");
                String op = parts[0];
                int key = Integer.parseInt(parts[1]);
                int value = 0;
                if (op.equals("Set")) {
                    value = Integer.parseInt(parts[2]);
                }

                switch (op) {
                    case "Set":
                        table.Set(key, value);
                        break;
                    case "Get":
                        String real = table.Get(key);
                        String expect = ansScanner.nextLine();
                        if (!real.equals(expect)) {
                            throw new Exception("Result does not match answer; Line number: " + lineNum);
                        }
                        break;
                    case "Del":
                        table.Delete(key);
                        break;
                    default:
                        throw new Exception("Unknown Hashtable Operation!");
                }
            }
            System.out.println("PASS");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void PerformanceTest() throws IOException {
        FileWriter writer = new FileWriter(insertDelayOutputFile);
        // Initialization
        // Generate RANDOM_KEY_VALUE_NUM random key-values and insert them into the hashtable
        Random random = new Random();
        int[] keys = new int[RANDOM_KEY_VALUE_NUM];
        for (int i = 0; i < PERFORMANCE_TEST_KEY_VALUE_NUM; i++) {
            int key = random.nextInt(Integer.MAX_VALUE), value = random.nextInt(Integer.MAX_VALUE);
            keys[i] = key;
            long startTime = System.nanoTime();
            table.Set(key, value);
            long endTime = System.nanoTime();
            long delay = endTime - startTime; // us
            writer.write(Long.toString(delay) + "\n");
        }
        writer.close();

        for (int i = PERFORMANCE_TEST_KEY_VALUE_NUM; i < RANDOM_KEY_VALUE_NUM; i++) {
            int key = random.nextInt(Integer.MAX_VALUE), value = random.nextInt(Integer.MAX_VALUE);
            keys[i] = key;
            table.Set(key, value);
        }

        // Test the performance of Get operation
        long shortestGetDelay = Long.MAX_VALUE, longestGetDelay = 0;
        long totalGetDelay = 0;
        for (int i = 0; i < TEST_OPERATION_NUM; i++) {
            int key = keys[random.nextInt(RANDOM_KEY_VALUE_NUM)];
            long startTime = System.nanoTime();
            table.Get(key);
            long endTime = System.nanoTime();
            long delay = endTime - startTime;

            shortestGetDelay = Math.min(shortestGetDelay, delay);
            longestGetDelay = Math.max(longestGetDelay, delay);
            totalGetDelay += delay;
        }
        double averageGetDelay = (double)(totalGetDelay) / (double)(TEST_OPERATION_NUM);
        double totalGetDelaySeconds = (double)(totalGetDelay) / (double)(1000000000);
        double getThroughput = (double)(TEST_OPERATION_NUM) / totalGetDelaySeconds;

        System.out.printf("Shortest Get Delay: %d\n", shortestGetDelay);
        System.out.printf("Longest Get Delay: %d\n", longestGetDelay);
        System.out.printf("Average Get Delay: %f\n", averageGetDelay);
        System.out.printf("Get Throughput: %f\n", getThroughput);

        // Test the performance of Get with Set Operations
        long shortestGetSetDelay = Long.MAX_VALUE, longestGetSetDelay = 0;
        long totalGetSetDelay = 0;
        for (int i = 0; i < TEST_OPERATION_NUM / 2; i++) {
            // GET operation
            int getKey = keys[random.nextInt(RANDOM_KEY_VALUE_NUM)];
            long getStartTime = System.nanoTime();
            table.Get(getKey);
            long getEndTime = System.nanoTime();
            long getDelay = getEndTime - getStartTime;

            shortestGetSetDelay = Math.min(shortestGetSetDelay, getDelay);
            longestGetSetDelay = Math.max(longestGetSetDelay, getDelay);
            totalGetSetDelay += getDelay;

            // SET Operation
            int setKey = random.nextInt(Integer.MAX_VALUE), setValue = random.nextInt(Integer.MAX_VALUE);
            long setStartTime = System.nanoTime();
            table.Set(setKey, setValue);
            long setEndTime = System.nanoTime();
            long setDelay = setEndTime - setStartTime;

            shortestGetSetDelay = Math.min(shortestGetSetDelay, setDelay);
            longestGetSetDelay = Math.max(longestGetSetDelay, setDelay);
            totalGetSetDelay += setDelay;
        }
        double averageGetSetDelay = (double)(totalGetSetDelay) / (double)(TEST_OPERATION_NUM);
        double totalGetSetDelaySeconds = (double)(totalGetSetDelay) / (double)(1000000000);
        double getSetThroughput = (double)(TEST_OPERATION_NUM) / totalGetSetDelaySeconds;
        System.out.printf("Shortest Get-Set Delay: %d\n", shortestGetSetDelay);
        System.out.printf("Longest Get-Set Delay: %d\n", longestGetSetDelay);
        System.out.printf("Avarage Get-Set Delay: %f\n", averageGetSetDelay);
        System.out.printf("Get-Set Throughput: %f\n", getSetThroughput);
    }
}
