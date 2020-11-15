package com.company;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static Pattern p = Pattern.compile("(===|!==|\\*\\*=|==|!=|\\+\\+|\\+=|-=|\\*=|\\/=|%=|>=|<=|&&|<<|>>|\\|\\||\\(|\\)|\\[|\\]|\\{|\\}|\\+|-|\\/|%|\\*—|=|>|<|\\?|!|&|~|\\^|,|;)");
    public static ArrayList<String> lexemes;
    static StringBuilder code = new StringBuilder();

    static class LexPattern {
        private final String name;
        private final String pattern;

        LexPattern(String name, String pattern) {
            this.name = name;
            this.pattern = pattern;
        }

        public String GetName() {
            return name;
        }

        public String GetPattern() {
            return pattern;
        }
    }

    public static void replaceAll(StringBuilder sb, String find, String replace) {
        Pattern p = Pattern.compile(find);
        Matcher matcher = p.matcher(sb);
        int startIndex = 0;
        while (matcher.find(startIndex)) {
            sb.replace(matcher.start(), matcher.end(), replace);
            startIndex = matcher.start() + replace.length();
        }
    }

    public final static LexPattern[] lexemesClass = {
            new LexPattern("RESERVED",
                    "var|abstract|arguments|await|boolean|break|byte|case|catch|char|class|const|continue|debugger|default|delete|do|double|else|enum|export|extends|false|final|finally|" +
                            "float|for|function|goto|if|implements|import|in|int|interface|let|long|new|null|package|private|protected|public|return|short|static|switch|this|throw|throws|true|" +
                            "try|typeof|void|while|async"),
            new LexPattern("STRING", "[\"|'|`][a-zA-Z1-9 ]+[\"|'|`|]"),
            new LexPattern("IDENTIFIER", "[a-zA-Z_$][a-zA-Z_0-9$]*"),
            new LexPattern("OPERATOR", "[#-+*/=(),.?!&`';:<>{}]{1}|[()-+?&:<>=]{2}|[=!*]{3}"),
            new LexPattern("HEXADECIMAL", "[-]?[0][xX][0-9a-fA-F]+"),
            new LexPattern("DECIMAL", "[-]?[0-9]"),
            new LexPattern("FLOAT", "[-]?[0-9]+[.][0-9]*")
    };

    public static void main(String[] args) {
        read("input.txt");
        removeComments(code);
        processDelimiters(code);
        System.out.println(lexemes);
        for (String lex : lexemes) {
            boolean isError = true;
            for (var lexemeClass : lexemesClass)
                if (Pattern.matches(lexemeClass.GetPattern(), lex)) {
                    isError = false;
                    System.out.println(lexemeClass.GetName() + " " + lex);
                    break;
                }
            if (isError)
                System.out.println("ERROR" + " " + lex);
        }
    }

    public static void read(String fileName) {
        try (FileReader fileReader = new FileReader(fileName)) {
            int c;
            while (-1 != (c = fileReader.read())) {
                code.append((char) c);
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void removeComments(StringBuilder code) {
        char previous = ' ';
        int startOneLineComment = -1, startManyLinesComment = -1;
        for (int i = 0; i < code.length(); i++) {
            if (startOneLineComment == -1 && code.charAt(i) == '/' && previous == '/') {
                startOneLineComment = i;
            }
            if (startManyLinesComment == -1 && code.charAt(i) == '*' && previous == '/') {
                startManyLinesComment = i;
            }
            if (startManyLinesComment != -1 && ((code.charAt(i) == '/' && previous == '*') || (i == code.length() - 1))) {
                code.delete(startManyLinesComment - 1, i + 1);
                i = startManyLinesComment - 2;
                startManyLinesComment = -1;
            }
            if (startOneLineComment != -1 && (code.charAt(i) == '\n')) {
                code.delete(startOneLineComment - 1, i);
                i = startOneLineComment - 1;
                startOneLineComment = -1;
            }
            previous = code.charAt(i);
        }
    }

    public static void processDelimiters(StringBuilder code) {
        Matcher m = p.matcher(code);
        int poss = 0;
        while (m.find(poss)) {
            String replacement = " ";
            code.insert(m.end(), replacement);
            code.insert(m.start(), replacement);
            poss = m.end() + replacement.length();
        }
        replaceAll(code, "[ \t\r\n]+", " ");
        code = new StringBuilder(code.toString().trim());
        lexemes = new ArrayList<>(Arrays.asList(code.toString().split(" ")));

        for (var lex : lexemes) {
            lex = lex.trim();
        }
    }
}
