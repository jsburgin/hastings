/**
 * Hastings
 * Designer Programming Language
 * Written by Joshua Burgin
 */

public class Main {

    public static void main(String[] args) {
        try {
            Lexer mainLexer = new Lexer("/Users/josh/Desktop/test.has");
            Parser mainParser = new Parser(mainLexer.getLexemes());
            Evaluator mainEvaluator = new Evaluator(mainParser.getStatements());

        } catch(StackOverflowError e) {
            System.out.println("\nStack overflow.");
            System.exit(1);
        }
    }
}