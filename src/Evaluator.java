/**
 * Created by josh on 11/13/16.
 */
public class Evaluator {
    Lexeme globalEnv;
    private boolean returned = false;

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
                return evalFuncCall(tree, env);
            case "IDENT":
                return Env.lookupEnv(env, tree);
        }

        return null;
    }

    private Lexeme evalVarDef(Lexeme tree, Lexeme env) {
        Env.insert((Lexeme) tree.getPrev(), eval((Lexeme) tree.getNext(), env), env);

        return new Lexeme("NONE");
    }

    private Lexeme evalStatements(Lexeme tree, Lexeme env) {
        Lexeme possibleReturn = eval((Lexeme) tree.getPrev(), env);

        if (!returned) {
            eval((Lexeme) tree.getNext(), env);
        }

        return possibleReturn;
    }

    private Lexeme evalStatement(Lexeme tree, Lexeme env) {
        return eval((Lexeme) tree.getPrev(), env);
    }

    private void evalFuncDef(Lexeme ident, Lexeme tree, Lexeme env) {
        Lexeme closure = Env.cons("CLOSURE", env,
                Env.cons("JOIN", getFuncDefParams((Lexeme) tree.getPrev().getNext()),
                        Env.cons("JOIN", getFuncDefBody(tree), null)
                        )
                );

        Env.insert(ident, closure, env);
    }

    private Lexeme evalFuncCall(Lexeme tree, Lexeme env) {

        Lexeme funcName = (Lexeme) tree.getNext();
        Lexeme args = getFuncCallArgs(tree);
        Lexeme eargs = evalArgs(args, env);

        if (funcName.getValue().equals("print")) {
            while (args != null) {
                System.out.print(Env.car(args).getValue() + " ");
                args = Env.cdr(args);
            }

            System.out.print("\n");

            return null;
        }

        Lexeme closure = eval(getFuncCallName(tree), env);
        Lexeme params = getClosureParams(closure);
        Lexeme body = getClosureBody(closure);
        Lexeme senv = getClosureEnv(closure);
        Lexeme xenv = Env.extendEnv(senv, params, eargs);

        return eval(body, xenv);
    }

    private Lexeme evalArgs(Lexeme args, Lexeme env) {

        Lexeme current = args;

        while (current != null) {
            Env.setCar(current, eval(Env.car(current), env));
            current = Env.cdr(current);
        }

        return args;
    }

    private Lexeme getFuncCallName(Lexeme tree) {
        return (Lexeme) tree.getPrev();
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
        if (tree == null) {
            return null;
        }

        return Env.cons("GLUE", tree, getFuncDefParams((Lexeme) tree.getNext()));
    }

    private Lexeme getFuncDefBody(Lexeme tree) {
        return (Lexeme) tree.getNext();
    }
}
