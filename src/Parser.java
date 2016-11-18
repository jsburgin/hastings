public class Parser {

    private Lexeme current;
    private Lexeme root;

    public Parser(Lexeme input) {
        if (input != null) {
            current =  input;
            root = statements("EOF");
        }
    }

    public Lexeme getStatements() {
        return root;
    }

    private Lexeme advance() {
        Lexeme old = current;
        current =  current.getNext();

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
        System.out.print("Line ~" + current.getLine() + ": ");
        System.out.println("Expected " + type + ", got " + current.getType());
        System.exit(1);
        return null;
    }

    /**
     * Statements
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
     * Statement
     */
    private Lexeme statement() {
        Lexeme tree = new Lexeme("STATEMENT");

        if (check("VAR")) {
            advance();
            tree.setPrev(varDec(ident()));
            tree.setNext(null);
        } else if (check("RETURN")) {
            advance();
            tree.setPrev(returnState());
            tree.setNext(null);
        } else if (check("ICOND")) {
            advance();
            tree.setPrev(ifState());
            tree.setNext(elseChain());
        } else if (check("IDENT")) {
            Lexeme ident = ident();

            if (assignPending()) {
                tree.setPrev(setVar(ident));
            } else {
                tree.setPrev(funcCall(ident));
            }

            match("SEMIC");
            tree.setNext(null);
        } else if (check("FUNC")) {
            tree.setPrev(funcAssign());
        }
        return tree;
    }

    /**
     * Function Assignment
     */
    private Lexeme funcAssign() {
        Lexeme tree = new Lexeme("VARIABLE");
        match("FUNC");
        tree.setPrev(ident());
        tree.setNext(funcDef());

        return tree;
    }

    /**
     * Identifier
     */
    private Lexeme ident() {
        Lexeme tree = new Lexeme("IDENTH");
        tree.setPrev(match("IDENT"));

        if (dotPending()) {
            match("DOT");
            tree.setNext(ident());
        }

        if (arrayPending()) {
            tree.setNext(arrayIndex());
        }

        return tree;
    }

    /**
     * If Statement
     */
    private Lexeme ifState() {
        Lexeme tree = new Lexeme("IFSTATE");
        match("OPAREN");
        tree.setPrev(condBody());
        match("CPAREN");
        match("OBRACE");
        tree.setNext(statements("CBRACE"));

        return tree;
    }

    /**
     * Else/If else Chain
     */
    private Lexeme elseChain() {
        if (!check("ELSE")) {
            return null;
        }

        Lexeme tree = new Lexeme("ELSECHAIN");
        tree.setPrev(elseState());
        tree.setNext(elseChain());

        return tree;
    }

    /**
     * Else Statement
     */
    private Lexeme elseState() {
        Lexeme tree = new Lexeme("ELSESTATE");
        match("ELSE");
        match("ICOND");
        tree.setPrev(conditional());
        match("OBRACE");
        tree.setNext(statements("CBRACE"));

        return tree;
    }

    /**
     * Conditional Body/Chain
     */
    private Lexeme condBody() {
        Lexeme tree = conditional();

        if (andOrPending()) {
            Lexeme temp = match("ANYTHING");
            temp.setPrev(tree);
            temp.setNext(condBody());
            tree = temp;
        }

        return tree;
    }

    /**
     * Conditional
     */
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

    /**
     * Variable Declaration
     */
    private Lexeme varDec(Lexeme ident) {
        Lexeme tree = new Lexeme("VARIABLE");

        tree.setPrev(ident);
        match("ASSIGN");
        if (funcDefPending()) {
            advance();
            tree.setNext(funcDef());
        } else if (arrayPending()) {
            tree.setNext(arrayDef());
        } else {
            tree.setNext(expression());
        }
        match("SEMIC");

        return tree;
    }

    /**
     * Updating Variable
     */
    private Lexeme setVar(Lexeme ident) {
        Lexeme tree = new Lexeme("SET");
        tree.setPrev(ident);
        match("ASSIGN");
        tree.setNext(expression());

        return tree;
    }

    /**
     * Defining Array
     */
    private Lexeme arrayDef() {
        Lexeme tree = new Lexeme("ARRAYDEF");
        match("OBRACK");
        tree.setPrev(argList());
        match("CBRACK");

        return tree;
    }

    /**
     * Array Index Slot
     */
    private Lexeme arrayIndex() {
        Lexeme tree = new Lexeme("INDEX");

        match("OBRACK");
        tree.setPrev(expression());
        match("CBRACK");

        return tree;
    }

    /**
     * Function Call
     */
    private Lexeme funcCall(Lexeme identifier) {
        Lexeme tree = new Lexeme("FUNCCALL");

        match("OPAREN");
        tree.setPrev(argList());
        match("CPAREN");
        tree.setNext(identifier);

        return tree;
    }


    /**
     * Defining Function
     */
    private Lexeme funcDef() {
        Lexeme tree = new Lexeme("FUNCDEF");
        tree.setPrev(paramList());
        match("OBRACE");
        tree.setNext(statements("CBRACE"));

        return tree;
    }

    /**
     * Return Statement
     */
    private Lexeme returnState() {
        Lexeme tree = new Lexeme("RETURNST");
        tree.setPrev(expression());
        match("SEMIC");
        tree.setNext(null);
        return tree;
    }

    /**
     * Parameter List
     */
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

    /**
     * Parameter Chain
     */
    private Lexeme getAddParams() {
        if (check("CPAREN")) return null;
        if (check("COMMA")) advance();

        return Env.cons("GLUE", match("IDENT"), getAddParams());
    }

    /**
     * Argument List
     */
    private Lexeme argList() {
        Lexeme tree = new Lexeme("ARGS");
        tree.setPrev(null);

        if (check("IDENT") || check("STRING") || check("INT") || check("NIL") || check("OBRACK")) {
            tree.setPrev(Env.cons("GLUE", expression(), getAddArgs()));
        }

        return tree;
    }

    /**
     * Argument Chain
     */
    private Lexeme getAddArgs() {
        if (!check("IDENT") && !check("STRING") && !check("INT") && !check("COMMA") && !check("NIL")) return null;
        if (check("COMMA")) advance();

        return Env.cons("GLUE", expression(), getAddArgs());
    }

    /**
     * Expression
     */
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
     * Primary
     */
    private Lexeme primary() {
        Lexeme tree = null;

        if (check("IDENT")) {
            Lexeme ident = ident();
            if (funcCallPending()) {
                return funcCall(ident);
            }
            return ident;
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
        } else if (check("NIL")) {
            return match("NIL");
        } else if (check("THIS")) {
            return match("THIS");
        } else if (check("OBRACK")) {
            return arrayDef();
        }

        return tree;
    }

    private boolean arrayPending() {
        return check("OBRACK");
    }

    private boolean funcDefPending() {
        return check("FUNC");
    }

    private boolean funcCallPending() {

        return check("OPAREN");
    }

    private boolean dotPending() {
        return check("DOT");
    }

    private boolean assignPending() {
        return check("ASSIGN");
    }

    private boolean opPending() {
        return check("PLUS") || check("MINUS") || check("MULT") || check("DIV");
    }

    private boolean condPending() {
        return check("LESST") || check("GRTT") || check("CEQUAL") || check("NOTEQ");
    }

    private boolean andOrPending() {
        return check("AND") || check("OR");
    }
}
