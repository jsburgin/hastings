public class Parser {

    private Lexeme current;

    public Parser(List input) {
        if (!input.isEmpty()) {
            current = (Lexeme) input.removeFront();
            Lexeme tree = statements("EOF");

            int a = 10;
        }
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
        tree.setPrev(conditional());
        match("OBRACE");
        tree.setNext(statements("CBRACE"));

        return tree;
    }

    private Lexeme conditional() {
        Lexeme tree = new Lexeme("COND");
        match("OPAREN");
        while (!check("CPAREN")) advance();
        match("CPAREN");

        return tree;
    }

    private Lexeme returnState() {
        Lexeme tree = new Lexeme("RETURNST");
        tree.setPrev(varBody());
        match("SEMIC");
        tree.setNext(null);
        return tree;
    }

    private Lexeme varDec() {
        Lexeme tree = new Lexeme("VARIABLE");

        tree.setPrev(match("IDENT"));
        match("ASSIGN");
        tree.setNext(varBody());
        match("SEMIC");

        return tree;
    }

    /**
     * varBody: string | bool | expression | funcDef
     */
    private Lexeme varBody() {
        if (check("STRING")) {
            return match("STRING");
        } else if (check("BOOLT")) {
            return match("BOOLT");
        } else if (check("FUNC")) {
            advance();
            return funcDef();
        }

        return expression();
    }


    private Lexeme identStart() {
        current = (Lexeme) current.getNext();

        if (check("OPAREN")) {
            current = (Lexeme) current.getPrev();
            return funcCall(match("IDENT"));
        }

        current = (Lexeme) current.getPrev();
        return match("IDENT");
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

        while(check("IDENT") || check("COMMA")) {
            if (check("COMMA")) advance();
            last.setNext(match("IDENT"));
            last = (Lexeme) last.getNext();
        }

        match("CPAREN");

        return tree;
    }

    private Lexeme argList() {
        Lexeme tree = new Lexeme("ARGS");
        Lexeme last = tree;
        match("OPAREN");

        while(check("IDENT") || check("STRING") || check("INT") || check("COMMA")) {
            if (check("COMMA")) advance();
            last.setNext(match("ANYTHING"));
            last = (Lexeme) last.getNext();
        }

        match("CPAREN");

        return tree;
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
     * primary : IDENT | INT | FUNCCALL
     */
    private Lexeme primary() {
        Lexeme tree = null;

        if (check("IDENT")) {
            tree = identStart();
        } else if (check("INT")) {
            tree = match("INT");
        }

        return tree;
    }

    private boolean opPending() {
        return check("PLUS") || check("MINUS") || check("MULT") || check("DIV");
    }
}
