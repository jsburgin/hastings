public class Parser {

    private Lexeme current;
    private Lexeme root;

    public Parser(List input) {
        if (!input.isEmpty()) {
            current = (Lexeme) input.removeFront();
            root = statements("EOF");
        }
    }

    public Lexeme getStatements() {
        return root;
    }

    private Lexeme advance() {
        Lexeme old = current;
        current = (Lexeme) current.getNext();

        old.setPrev(null);
        old.setNext(null);

        return old;
    }

    private boolean check(String type) {
        return type.equals(current.getType());
    }

    private Lexeme match(String type) {
        if (type.equals("ANYTHING")) {
            return advance();
        }

        if (check(type)) {
            return advance();
        }

        System.out.println("SYNTAX ERROR.");
        System.out.print("Line " + current.getLine() + ": ");
        System.out.println("Expected symbol " + type + " got " + current.getType());
        System.exit(1);
        return null;
    }

    /**
     * Grammer Rules
     */
    private Lexeme statements(String terminate) {
        Lexeme tree = new Lexeme("STATEMENTS");

        if (check(terminate)) {
            advance();
            return new Lexeme("DONE");
        }

        tree.setPrev(statement());
        tree.setNext(statements(terminate));

        return tree;
    }


    /**
     * statement:
     */
    private Lexeme statement() {
        Lexeme tree = new Lexeme("STATEMENT");

        if (check("VAR")) {
            advance();
            tree.setPrev(varDec());
            tree.setNext(null);
        } else if (check("RETURN")) {
            advance();
            tree.setPrev(returnState());
            tree.setNext(null);
        } else if (check("ICOND")) {
            advance();
            tree.setPrev(ifState());
            tree.setNext(elseChain());
        } else if (check("WHILE")) {
            advance();
            tree.setPrev(whileLoop());
            tree.setNext(null);
        } else if (check("IDENT")) {
            tree.setPrev(funcCall(match("IDENT")));
            match("SEMIC");
            tree.setNext(null);
        }
        return tree;
    }


    private Lexeme elseChain() {
        if (!check("ELSE")) {
            return null;
        }

        Lexeme tree = new Lexeme("ELSECHAIN");
        tree.setPrev(elseState());
        tree.setNext(elseChain());

        return tree;
    }

    private Lexeme elseState() {
        Lexeme tree = new Lexeme("ELSESTATE");
        match("ELSE");
        match("ICOND");
        tree.setPrev(conditional());
        match("OBRACE");
        tree.setNext(statements("CBRACE"));

        return tree;
    }

    private Lexeme whileLoop() {
        Lexeme tree = new Lexeme("WHILELOOP");
        tree.setPrev(conditional());
        match("OBRACE");
        tree.setNext(statements("CBRACE"));

        return tree;
    }

    private Lexeme ifState() {
        Lexeme tree = new Lexeme("IFSTATE");
        tree.setPrev(condBody());
        match("OBRACE");
        tree.setNext(statements("CBRACE"));

        return tree;
    }

    private Lexeme condBody() {
        Lexeme tree = new Lexeme("COND");

        match("OPAREN");
        tree.setPrev(conditional());
        tree.setNext(null);
        match("CPAREN");

        return tree;
    }

    private Lexeme conditional() {
        Lexeme tree = expression();

        if (condPending()) {
            Lexeme temp = match("ANYTHING");
            temp.setPrev(tree);
            temp.setNext(expression());
            tree = temp;
        }

        return tree;
    }

    private boolean condPending() {
        return check("LESST") || check("GRTT") || check("CEQUAL");
    }

    private Lexeme returnState() {
        Lexeme tree = new Lexeme("RETURNST");
        tree.setPrev(expression());
        match("SEMIC");
        tree.setNext(null);
        return tree;
    }

    private Lexeme varDec() {
        Lexeme tree = new Lexeme("VARIABLE");

        tree.setPrev(match("IDENT"));
        match("ASSIGN");
        if (funcDefPending()) {
            advance();
            tree.setNext(funcDef());
        } else {
            tree.setNext(expression());
        }
        match("SEMIC");

        return tree;
    }

    private boolean funcDefPending() {
        return check("FUNC");
    }

    private boolean funcCallPending() {
        current = (Lexeme) current.getNext();
        if (check("OPAREN")) {
            current = (Lexeme) current.getPrev();
            return true;
        }

        current = (Lexeme) current.getPrev();
        return false;
    }

    private Lexeme funcCall(Lexeme identifier) {
        Lexeme tree = new Lexeme("FUNCCALL");

        tree.setPrev(argList());
        tree.setNext(identifier);

        return tree;
    }

    /**
     * funcDef: func argList oparen STATEMENTS cparen
     */
    private Lexeme funcDef() {
        Lexeme tree = new Lexeme("FUNCDEF");
        tree.setPrev(paramList());
        match("OBRACE");
        tree.setNext(statements("CBRACE"));

        return tree;
    }

    private Lexeme paramList() {
        Lexeme tree = new Lexeme("PARAMS");
        Lexeme last = tree;
        match("OPAREN");

        if (check("IDENT")) {
            tree.setPrev(Env.cons("GLUE", match("IDENT"), getAddParams()));
        }

        match("CPAREN");

        return tree;
    }

    private Lexeme getAddParams() {
        if (check("CPAREN")) return null;
        if (check("COMMA")) advance();

        return Env.cons("GLUE", match("IDENT"), getAddParams());
    }

    private Lexeme argList() {
        Lexeme tree = new Lexeme("ARGS");
        tree.setPrev(null);
        match("OPAREN");

        if (check("IDENT") || check("STRING") || check("INT")) {
            tree.setPrev(Env.cons("GLUE", expression(), getAddArgs()));
        }

        match("CPAREN");

        return tree;
    }

    private Lexeme getAddArgs() {
        if (check("CPAREN")) return null;
        if (check("COMMA")) advance();

        return Env.cons("GLUE", expression(), getAddArgs());
    }

    private Lexeme expression() {
        Lexeme tree = primary();

        if (opPending()) {
            Lexeme temp = match("ANYTHING");
            temp.setPrev(tree);
            temp.setNext(expression());
            tree = temp;
        }

        return tree;
    }

    /**
     * primary : IDENT | INT | FUNCCALL | STRING
     */
    private Lexeme primary() {
        Lexeme tree = null;

        if (check("IDENT")) {
            if (funcCallPending()) {
                return funcCall(match("IDENT"));
            }

            return match("IDENT");
        } else if (check("INT")) {
            tree = match("INT");
        } else if (check("STRING")) {
            tree = match("STRING");
        } else if (check("FUNC")) {
            advance();
            return funcDef();
        } else if (check("BOOLT")) {
            return match("BOOLT");
        } else if (check("BOOLF")) {
            return match("BOOLF");
        }

        return tree;
    }

    private boolean opPending() {
        return check("PLUS") || check("MINUS") || check("MULT") || check("DIV");
    }
}
