/**
 * Hastings
 * Designer Programming Language
 * Written by Joshua Burgin
 */

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide .has source file.");
            System.exit(1);
        }

        try {
            Lexeme env = Env.createEnv();
            Include.execute(args[0], env);
        } catch(StackOverflowError e) {
            System.out.println("\nSTACK OVERFLOW.");
            System.exit(1);
        }
    }
}