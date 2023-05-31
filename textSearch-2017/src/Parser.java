import java.util.ArrayList;
import java.util.Stack;

class Token {
    public static final int TYPE_INVALID = -1;
    public static final int TYPE_LEFT_BRACKET = 0;
    public static final int TYPE_RIGHT_BRACKET = 1;
    public static final int TYPE_WORD = 2;
    public static final int TYPE_REL_OP_AND = 3;
    public static final int TYPE_REL_OP_OR = 4;
    public static final int TYPE_END_OF_STREAM = 5;
    public int type;
    public String word;

    public Token(int type, String word) {
        this.type = type;
        this.word = word;
    }
}

public class Parser {
    private String seq;
    private int cur;

    public Parser(String seq) {
        this.seq = seq;
        this.cur = 0;
    }

    private Token lex() {
        if (cur == seq.length()) {
            return new Token(Token.TYPE_END_OF_STREAM, "");
        }

        while (seq.charAt(cur) == ' ') {
            cur++;
        }

        switch (seq.charAt(cur)) {
            case '(' -> {
                cur++;
                return new Token(Token.TYPE_LEFT_BRACKET, "(");
            }
            case ')' -> {
                cur++;
                return new Token(Token.TYPE_RIGHT_BRACKET, ")");
            }
            case '&' -> {
                if (seq.charAt(cur + 1) != '&') {
                    return new Token(Token.TYPE_INVALID, "");
                }
                cur += 2;
                return new Token(Token.TYPE_REL_OP_AND, "&&");
            }
            case '|' -> {
                if (seq.charAt(cur + 1) != '|') {
                    return new Token(Token.TYPE_INVALID, "");
                }
                cur += 2;
                return new Token(Token.TYPE_REL_OP_OR, "||");
            }
            default -> {
                int start = cur;
                while (cur < seq.length() && (Character.isLetter(seq.charAt(cur)) || seq.charAt(cur) == '!')) {
                    cur++;
                }
                return new Token(Token.TYPE_WORD, seq.substring(start, cur));
            }
        }
    }

    public ArrayList<String> parse() {
        Stack<Token> ops = new Stack<>();
        ArrayList<Token> postfixTokens = new ArrayList<>();
        while (true) {
            Token t = lex();
            if (t.type == Token.TYPE_END_OF_STREAM) {
                while (!ops.empty()) {
                    postfixTokens.add(ops.pop());
                }
                break;
            }

            switch (t.type) {
                case Token.TYPE_REL_OP_AND, Token.TYPE_LEFT_BRACKET -> ops.push(t);
                case Token.TYPE_REL_OP_OR -> {
                    while (!ops.empty() && ops.lastElement().type != Token.TYPE_LEFT_BRACKET) {
                        postfixTokens.add(ops.pop());
                    }
                    ops.push(t);
                }
                case Token.TYPE_WORD -> postfixTokens.add(t);
                case Token.TYPE_RIGHT_BRACKET -> {
                    while (ops.lastElement().type != Token.TYPE_LEFT_BRACKET) {
                        postfixTokens.add(ops.pop());
                    }
                    ops.pop();
                }
            }
        }

        Stack<ArrayList<String>> calcStack = new Stack<>();
        for (Token t: postfixTokens) {
            switch (t.type) {
                case Token.TYPE_WORD -> {
                    ArrayList<String> arr = new ArrayList<>();
                    arr.add(t.word);
                    calcStack.push(arr);
                }
                case Token.TYPE_REL_OP_AND -> {
                    ArrayList<String> andArr1 = calcStack.pop();
                    ArrayList<String> andArr2 = calcStack.pop();
                    ArrayList<String> andArr = new ArrayList<>();
                    for (String str1 : andArr2) {
                        for (String str2 : andArr1) {
                            andArr.add(str1 + " " + str2);
                        }
                    }
                    calcStack.push(andArr);
                }
                case Token.TYPE_REL_OP_OR -> {
                    ArrayList<String> orArr1 = calcStack.pop();
                    ArrayList<String> orArr2 = calcStack.pop();
                    ArrayList<String> orArr = new ArrayList<>();
                    orArr.addAll(orArr2);
                    orArr.addAll(orArr1);
                    calcStack.push(orArr);
                }
            }
        }

        return calcStack.pop();
    }
}
