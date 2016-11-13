/**
 * Hastings
 * Designer Programming Language
 * Written by Joshua Burgin
 */

public class Main {

    public static void main(String[] args) {

        Lexer mainLexer = new Lexer("/Users/josh/Desktop/test.has");
        List lexemes = mainLexer.getLexemes();

        while(!lexemes.isEmpty()) {
            System.out.println(lexemes.removeFront());
        }
    }
}