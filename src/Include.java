public class Include {
    public static void execute(String filename, Lexeme env) {
        Lexer lexer = new Lexer(filename);
        Parser parser = new Parser(lexer.getLexemes());
        Evaluator evaluator = new Evaluator(parser.getStatements(), env, filename);
    }
}
