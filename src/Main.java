/**
 * Hastings
 * Designer Programming Language
 * Written by Joshua Burgin
 */

public class Main {

    public static void main(String[] args) {

        Lexer mainLexer = new Lexer("/Users/josh/Desktop/main.has");
        List lexemes = mainLexer.getLexemes();

        while(!lexemes.isEmpty()) {
            System.out.println(lexemes.removeFront());
        }
    }
}