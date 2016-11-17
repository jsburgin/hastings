import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private String baseFile;
    private List lexemes;
    private Scanner scanner;
    private int line;

    public Lexer(String fileName) {
        this.baseFile = fileName;
        this.line = 1;

        try {
            this.scanner = new Scanner(new File(this.baseFile));
            this.lexemes = new List();
            this.beginAnalysis();

            this.lexemes.append(new Lexeme("EOF"));
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("File " + this.baseFile + " not found.");
            System.out.println("Exiting.");
            System.exit(1);
        }
    }

    public List getLexemes() {
        return this.lexemes;
    }

    private void beginAnalysis() {
        scanner.useDelimiter("");
        while(scanner.hasNext()) {
            this.processSingle(scanner.next());
        }
    }

    private void processSingle(String startingToken) {
        Lexeme singleLexeme = checkSingle(startingToken);

        if (singleLexeme == null) {
            processWord(startingToken);
            return;
        }

        if (singleLexeme.getType() == "ASSIGN") {
            if (this.lexemes.peekBack() != null) {
                Lexeme previous = (Lexeme) this.lexemes.peekBack();
                if (checkRepeats(previous)) return;
            }
        }

        if (singleLexeme.getType() != "COMMENT") {
            this.lexemes.append(singleLexeme);
        }
    }

    private Lexeme checkSingle(String value) {
        switch(value) {
            case "(":
                return new Lexeme("OPAREN", value, line);
            case ")":
                return new Lexeme("CPAREN", value, line);
            case "{":
                return new Lexeme("OBRACE", value, line);
            case "}":
                return new Lexeme("CBRACE", value, line);
            case ";":
                return new Lexeme("SEMIC", value, line);
            case ",":
                return new Lexeme("COMMA", value, line);
            case "+":
                return new Lexeme("PLUS", value, line);
            case "-":
                return new Lexeme("MINUS", value, line);
            case "*":
                return new Lexeme("MULT", value, line);
            case "/":
                return new Lexeme("DIV", value, line);
            case "=":
                return new Lexeme("ASSIGN", value, line);
            case "<":
                return new Lexeme("LESST", value, line);
            case ">":
                return new Lexeme("GRTT", value, line);
            case "^":
                return new Lexeme("EXP", value, line);
            case "%":
                return new Lexeme("MOD", value, line);
            case "[":
                return new Lexeme("OBRACK", value, line);
            case "]":
                return new Lexeme("CBRACK", value, line);
            case ".":
                return new Lexeme("DOT", value, line);
            case "#":
                while (scanner.hasNext()) {
                    String scanned = scanner.next();
                    if (scanned.equals("\n")) break;
                }
                return new Lexeme("COMMENT", value, line);
            case "\"":
                value = "";
                while (scanner.hasNext()) {
                    String scanned = scanner.next();
                    if (scanned.equals("\"")) break;
                    value += scanned;
                }
                return new Lexeme("STRING", value, line);
            case "\n":
                this.line++;
        }

        return null;
    }

    private boolean checkRepeats(Lexeme previous) {
        switch (previous.getType()) {
            case "ASSIGN":
                this.lexemes.removeBack();
                this.lexemes.append(new Lexeme("CEQUAL", "==", line));
                return true;
            case "GRTT":
                this.lexemes.removeBack();
                this.lexemes.append(new Lexeme("GRTEQ", ">=", line));
                return true;
            case "LESST":
                this.lexemes.removeBack();
                this.lexemes.append(new Lexeme("LESSEQ", "<=", line));
                return true;
        }

        return false;
    }

    private void processWord(String startingWord) {
        if (startingWord.compareTo(" ") == 0 || startingWord.compareTo("\n") == 0) {
            return;
        }

        while (scanner.hasNext()) {
            String newValue = scanner.next();


            if (checkSingle(newValue) != null) {
                Lexeme singleMatched = checkSingle(newValue);
                this.lexemes.append(checkWord(startingWord));

                if (singleMatched.getType() != "COMMENT") {
                    this.lexemes.append(checkSingle(newValue));
                }
                break;
            }

            if (newValue.compareTo(" ") == 0 || newValue.compareTo("\n") == 0) {
                this.lexemes.append(checkWord(startingWord));
                break;
            }

            startingWord += newValue;
        }
    }

    private Lexeme checkWord(String value) {
        switch (value) {
            case "var":
                return new Lexeme("VAR", value, line);
            case "func":
                return new Lexeme("FUNC", value, line);
            case "if":
                return new Lexeme("ICOND", value, line);
            case "else":
                return new Lexeme("ELSE", value, line);
            case "!=":
                return new Lexeme("CDEQUAL", value, line);
            case "for":
                return new Lexeme("FOR", value, line);
            case "return":
                return new Lexeme("RETURN", value, line);
            case "this":
                return new Lexeme("THIS", value, line);
            case "true":
                return new Lexeme("BOOLT", value, line);
            case "false":
                return new Lexeme("BOOLF", value, line);
            case "nil":
                return new Lexeme("NIL", value, line);
            case "set":
                return new Lexeme("SET", value, line);
        }

        return dynamicValue(value);
    }

    private Lexeme dynamicValue(String value) {
        String pattern = "([0-9])+";
        Pattern checkAgainst = Pattern.compile(pattern);
        Matcher m = checkAgainst.matcher(value);

        if (m.find()) {
            if (m.group(0).length() == value.length()) {
                return new Lexeme("INT", value, line);
            }
        }

        return new Lexeme("IDENT", value, line);
    }
}
