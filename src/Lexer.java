import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private String baseFile;
    private List lexemes;
    private Scanner scanner;

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
            case "[":
                return new Lexeme("OBRACK", value);
            case "]":
                return new Lexeme("CBRACK", value);
            case "#":
                while (scanner.hasNext()) {
                    String scanned = scanner.next();
                    if (scanned.equals("\n")) break;
                }
                return new Lexeme("COMMENT", value);
            case "\"":
                while (scanner.hasNext()) {
                    String scanned = scanner.next();
                    value += scanned;
                    if (scanned.equals("\"")) break;
                }
                return new Lexeme("STRING", value);
        }

        return null;
    }

    private boolean checkRepeats(Lexeme previous) {
        switch (previous.getType()) {
            case "ASSIGN":
                this.lexemes.removeBack();
                this.lexemes.append(new Lexeme("CEQUAL", "=="));
                return true;
            case "GRTT":
                this.lexemes.removeBack();
                this.lexemes.append(new Lexeme("GRTEQ", ">="));
                return true;
            case "LESST":
                this.lexemes.removeBack();
                this.lexemes.append(new Lexeme("LESSEQ", "<="));
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
            case "true":
                return new Lexeme("BOOLT", value);
            case "false":
                return new Lexeme("BOOLF", value);
        }

        return dynamicValue(value);
    }

    private Lexeme dynamicValue(String value) {
        // check for a valid integer value
        String pattern = "([0-9])+";
        Pattern checkAgainst = Pattern.compile(pattern);

        Matcher m = checkAgainst.matcher(value);

        if (m.find()) {
            if (m.group(0).length() == value.length()) {
                return new Lexeme("INT", value);
            }
        }

        // not an integer
        return new Lexeme("IDENT", value);
    }
}
