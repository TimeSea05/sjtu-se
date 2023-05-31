import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class Occurrence {
    public int line;
    public int col;

    public Occurrence(int line, int col) {
        this.line = line;
        this.col = col;
    }
}

class LineInfo {
    public String line;
    public int lineNum;
    public LineInfo(String line, int lineNum) {
        this.line = line;
        this.lineNum = lineNum;
    }
}

public class TextSearchSystem {
    private final HashMap<String, ArrayList<Occurrence>> wordCount = new HashMap<>();
    private ArrayList<LineInfo> linesInfo = new ArrayList<>();

    private static String processWord(String word) {
        word = word.toLowerCase();
        if (word.endsWith("es") || word.endsWith("ed")) {
            word = word.substring(0, word.length() - 2);
        } else if (word.endsWith("ing")) {
            word = word.substring(0, word.length() - 3);
        } else if (word.endsWith("s") && word.length() > 2) {
            word = word.substring(0, word.length() - 1);
        }

        return word;
    }
    public void readTextFile(String path) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            int lineNum = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                linesInfo.add(new LineInfo(line, lineNum));
                String[] words = line.split("[^A-Za-z]+");
                for (int colNum = 0; colNum < words.length; colNum++) {
                    String word = processWord(words[colNum]);
                    ArrayList<Occurrence> occurs = wordCount.get(word);

                    if (occurs != null) {
                        occurs.add(new Occurrence(lineNum, colNum));
                    } else {
                        occurs = new ArrayList<>();
                        occurs.add(new Occurrence(lineNum, colNum));
                        wordCount.put(word, occurs);
                    }
                }
                lineNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void outputWordCount(String path) {
        BufferedWriter writer = null;
        TreeMap<String, ArrayList<Occurrence>> sortedWordCount = new TreeMap<>(wordCount);
        try {
            writer = new BufferedWriter(new FileWriter(path));
            for (Map.Entry<String, ArrayList<Occurrence>> entry: sortedWordCount.entrySet()) {
                String word = entry.getKey();
                ArrayList<Occurrence> list = entry.getValue();
                writer.write(word + " {");
                for (int i = 0; i < list.size(); i++) {
                    if (i != 0) {
                        writer.write(",");
                    }
                    Occurrence occur = list.get(i);
                    writer.write("(" + occur.line + "," + occur.col + ")");
                }
                writer.write("}\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean match(String line, String phrase) {
        String[] wordsOfLine = line.split("[^A-Za-z]+");
        String[] wordsOfPhrase = phrase.split(" ");

        boolean allWordsWithExclaim = true;
        for (String word: wordsOfPhrase) {
            allWordsWithExclaim = allWordsWithExclaim && word.contains("!");
        }
        if (allWordsWithExclaim) {
            StringBuilder processedLine = new StringBuilder();
            StringBuilder processedPhrase = new StringBuilder();
            for (int i = 0; i < wordsOfLine.length; i++) {
                if (i != 0) {
                    processedLine.append(" ");
                }
                processedLine.append(processWord(wordsOfLine[i]));
            }
            for (int i = 0; i < wordsOfPhrase.length; i++) {
                if (i != 0) {
                    processedPhrase.append(" ");
                }
                processedPhrase.append(processWord(wordsOfPhrase[i].substring(1)));
            }

            return !processedLine.toString().contains(processedPhrase.toString());
        }

        for (int i = 0; i < wordsOfLine.length; i++) {
            int j = 0;
            boolean isMatch = true;
            for (; j < wordsOfPhrase.length; j++) {
                String word1 = processWord(wordsOfLine[i+j]);
                boolean excludeMode = wordsOfPhrase[j].contains("!");
                String word2 = processWord(excludeMode ? wordsOfPhrase[j].substring(1) : wordsOfPhrase[j]);

                if (excludeMode && word1.equals(word2) || !excludeMode && !word1.equals(word2)) {
                    isMatch = false;
                    break;
                }
            }

            if (isMatch) return true;
        }
        return false;
    }

    private ArrayList<LineInfo> filterLineInfo(String[] phrases) {
        ArrayList<LineInfo> filteredLinesInfo = new ArrayList<>();
        for (LineInfo info: linesInfo) {
            for (String phrase: phrases) {
                if (match(info.line, phrase)) {
                    filteredLinesInfo.add(info);
                    break;
                }
            }
        }

        return filteredLinesInfo;
    }

    public void fullTextSearch(String pattern) {
        Parser parser = new Parser(pattern);
        ArrayList<String> phrases = parser.parse();
        String[] phraseArr = phrases.toArray(new String[0]);

        for (String phrase: phraseArr) {
            int count = filterLineInfo(new String[]{phrase}).size();
            System.out.printf("%s: %d occurrences\n", phrase, count);
        }

        ArrayList<LineInfo> filteredLinesInfo = filterLineInfo(phraseArr);
        for (int i = 0; i < filteredLinesInfo.size(); i++) {
            LineInfo info = filteredLinesInfo.get(i);
            System.out.printf("%d.(%d)%s\n", i+1, info.lineNum, info.line);
        }
    }

    public String fullTextSearchWithStringResult(String pattern) {
        Parser parser = new Parser(pattern);
        ArrayList<String> phrases = parser.parse();
        String[] phraseArr = phrases.toArray(new String[0]);

        StringBuilder builder = new StringBuilder();
        for (String phrase: phraseArr) {
            int count = filterLineInfo(new String[]{phrase}).size();
            builder.append(String.format("%s: %d occurrences\n", phrase, count));
        }

        ArrayList<LineInfo> filteredLinesInfo = filterLineInfo(phraseArr);
        for (int i = 0; i < filteredLinesInfo.size(); i++) {
            LineInfo info = filteredLinesInfo.get(i);
            builder.append(String.format("%d.(%d)%s\n", i+1, info.lineNum, info.line));
        }

        return builder.toString();
    }
}
