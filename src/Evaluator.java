/**
 * Created by josh on 11/13/16.
 */
public class Evaluator {
    Lexeme globalEnv;
    private Lexeme returnValue = null;

    public Evaluator(Lexeme tree) {
        globalEnv = new Env().createEnv();
        eval(tree, globalEnv);
    }

    private Lexeme eval(Lexeme tree, Lexeme env) {
        switch (tree.getType()) {
            case "STATEMENTS":
                return evalStatements(tree, env);
            case "STATEMENT":
                return evalStatement(tree, env);
            case "VARIABLE":
                return evalVarDef(tree, env);
            case "INT":
                return tree;
            case "STRING":
                return tree;
            case "FUNCCALL":
                Lexeme call = evalFuncCall(tree, env);
                returnValue = null;
                return call;
            case "IDENT":
                return Env.lookupEnv(env, tree);
            case "PLUS":
                return evalOp("+", tree, env);
            case "MINUS":
                return evalOp("-", tree, env);
            case "MULT":
                return evalOp("*", tree, env);
            case "DIV":
                return evalOp("/", tree, env);
            case "FUNCDEF":
                return evalFuncDef(tree, env);
            case "RETURNST":
                returnValue = eval((Lexeme) tree.getPrev(), env);
                return null;
        }

        return null;
    }


    private Lexeme evalOp(String op, Lexeme tree, Lexeme env) {
        Lexeme left = eval((Lexeme) tree.getPrev(), env);
        Lexeme right = eval((Lexeme) tree.getNext(), env);

        if (!left.getType().equals(right.getType())) {
            System.out.print("Incompatible types for operation " + op + ": ");
            System.out.println(left.getType() + " and " + right.getType() + ".");
            System.exit(1);
        }

        if (left.getType().equals("STRING")) {
            String first = left.getValue();
            String second = right.getValue();
            Lexeme done = new Lexeme("STRING");

            switch (op) {
                case "+":
                    done.setValue(String.valueOf(first + second));
                    return done;
                default:
                    System.out.println("Invalid operation: " + op + " for types STRING.");
                    System.exit(1);
            }
        }

        Lexeme done = new Lexeme("INT");
        int first = Integer.valueOf(left.getValue());
        int second = Integer.valueOf(right.getValue());

        switch (op) {
            case "+":
                done.setValue(String.valueOf(first + second));
                return done;
            case "-":
                done.setValue(String.valueOf(first - second));
                return done;
            case "*":
                done.setValue(String.valueOf(first * second));
                return done;
            case "/":
                done.setValue(String.valueOf(first / second));
                return done;
        }

        return null;
    }

    private Lexeme evalVarDef(Lexeme tree, Lexeme env) {
        Env.insert((Lexeme) tree.getPrev(), eval((Lexeme) tree.getNext(), env), env);

        return new Lexeme("NONE");
    }

    private Lexeme evalStatements(Lexeme tree, Lexeme env) {
        eval((Lexeme) tree.getPrev(), env);

        if (returnValue == null) {
            eval((Lexeme) tree.getNext(), env);
        }

        return returnValue;
    }

    private Lexeme evalStatement(Lexeme tree, Lexeme env) {
        return eval((Lexeme) tree.getPrev(), env);
    }

    private Lexeme evalFuncDef(Lexeme tree, Lexeme env) {
        Lexeme closure = Env.cons("CLOSURE", env,
                Env.cons("JOIN", getFuncDefParams(tree),
                        Env.cons("JOIN", getFuncDefBody(tree), null)
                        )
                );

        return closure;
    }

    private Lexeme evalFuncCall(Lexeme tree, Lexeme env) {

        Lexeme funcName = (Lexeme) tree.getNext();
        Lexeme args = getFuncCallArgs(tree);
        Lexeme eargs = evalArgs(args, env);

        if (funcName.getValue().equals("print")) {
            while (eargs != null) {
                System.out.print(Env.car(eargs).getValue() + " ");
                eargs = Env.cdr(eargs);
            }

            System.out.print("\n");

            return null;
        }

        Lexeme closure = Env.lookupEnv(env, (Lexeme) tree.getNext());
        Lexeme params = getClosureParams(closure);
        Lexeme body = getClosureBody(closure);
        Lexeme senv = getClosureEnv(closure);
        Lexeme xenv = Env.extendEnv(senv, params, eargs);

        return eval(body, xenv);
    }

    private Lexeme evalArgs(Lexeme args, Lexeme env) {
        if (args != null) {
            return Env.cons("GLUE", eval(Env.car(args), env), evalArgs(Env.cdr(args), env));
        }

        return null;
    }

    private Lexeme getFuncCallArgs(Lexeme tree) {
        return (Lexeme) tree.getPrev().getPrev();
    }

    private Lexeme getClosureParams(Lexeme closure) {
        return Env.car(Env.cdr(closure));
    }

    private Lexeme getClosureBody(Lexeme closure) {
        return Env.car(Env.cdr(Env.cdr(closure)));
    }

    private Lexeme getClosureEnv(Lexeme closure) {
        return Env.car(closure);
    }

    private Lexeme getFuncDefParams(Lexeme tree) {
        return (Lexeme) tree.getPrev().getPrev();
    }

    private Lexeme getFuncDefBody(Lexeme tree) {
        return (Lexeme) tree.getNext();
    }
}
