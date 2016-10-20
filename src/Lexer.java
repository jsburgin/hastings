import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private String baseFile;
    private List lexemes;
    private Scanner scanner;
    private boolean stringLock = false;

    public Lexer(String fileName) {
        this.baseFile = fileName;

        try {
            this.scanner = new Scanner(new File(this.baseFile));
            this.lexemes = new List();

            this.beginAnalysis();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("File " + this.baseFile + " not found.\nExiting.");
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

        if (singleLexeme != null) {

            if (singleLexeme.getType() == "ASSIGN") {
                if (this.lexemes.peekBack() != null) {
                    Lexeme previous = (Lexeme)this.lexemes.peekBack().getValue();

                    if (previous.getType() == "ASSIGN") {
                        this.lexemes.removeBack();
                        this.lexemes.append(new Lexeme("CEQUAL", "=="));
                        return;
                    }

                    if (previous.getType() == "GRTT") {
                        this.lexemes.removeBack();
                        this.lexemes.append(new Lexeme("GRTEQ", ">="));
                        return;
                    }

                    if (previous.getType() == "LESST") {
                        this.lexemes.removeBack();
                        this.lexemes.append(new Lexeme("LESSEQ", "<="));
                        return;
                    }
                }
            }

            if (singleLexeme.getType() != "COMMENT") {
                this.lexemes.append(singleLexeme);
            }

        } else {
            processWord(startingToken);
        }
    }

    private Lexeme checkSingle(String value) {

        if (!stringLock && value.equals("\"")) {
            this.stringLock = true;
            return null;
        }

        if (this.stringLock && value.equals("\"")) {
            this.stringLock = false;
            return null;
        }

        if (this.stringLock) {
            return null;
        }

        switch(value) {
            case "(":
                return new Lexeme("OPAREN", value);
            case ")":
                return new Lexeme("CPAREN", value);
            case "{":
                return new Lexeme("OBRACE", value);
            case "}":
                return new Lexeme("CBRACE", value);
            case ";":
                return new Lexeme("SEMIC", value);
            case ",":
                return new Lexeme("COMMA", value);
            case "+":
                return new Lexeme("PLUS", value);
            case "-":
                return new Lexeme("MINUS", value);
            case "*":
                return new Lexeme("MULT", value);
            case "/":
                return new Lexeme("DIV", value);
            case "=":
                return new Lexeme("ASSIGN", value);
            case "<":
                return new Lexeme("LESST", value);
            case ">":
                return new Lexeme("GRTT", value);
            case "^":
                return new Lexeme("EXP", value);
            case "%":
                return new Lexeme("MOD", value);
            case "#":
                while (scanner.hasNext()) {
                    String scanned = scanner.next();
                    if (scanned.equals("\n")) break;
                }
                return new Lexeme("COMMENT", value);
            default:
                return null;
        }
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

            if (newValue.compareTo(" ") == 0 && !stringLock
                    || newValue.compareTo("\n") == 0 && !stringLock) {
                this.lexemes.append(checkWord(startingWord));
                break;
            }

            startingWord += newValue;

        }
    }

    private Lexeme checkWord(String value) {
        switch (value) {
            case "var":
                return new Lexeme("VAR", value);
            case "func":
                return new Lexeme("FUNCTION", value);
            case "if":
                return new Lexeme("ICOND", value);
            case "!=":
                return new Lexeme("CDEQUAL", value);
            case "while":
                return new Lexeme("WHILE", value);
            case "for":
                return new Lexeme("FOR", value);
            case "return":
                return new Lexeme("RETURN", value);
            case "this":
                return new Lexeme("ENV", value);
        }

        return dynamicValue(value);
    }

    private Lexeme dynamicValue(String value) {
        String pattern = "\"([^\\\\\"]|\\\\\\\\|\\\\\")*\"";
        Pattern checkAgainst = Pattern.compile(pattern);
        Matcher m = checkAgainst.matcher(value);

        if (m.find()) {
            if (m.group(0).length() == value.length()) {
                return new Lexeme("STRING", value);
            }
        }

        pattern = "([0-9])+";
        checkAgainst = Pattern.compile(pattern);
        m = checkAgainst.matcher(value);

        if (m.find()) {
            if (m.group(0).length() == value.length()) {
                return new Lexeme("INT", value);
            }
        }

        return new Lexeme("IDENT", value);
    }
}
