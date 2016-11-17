public class Env {

    public static Lexeme createEnv() {
        return extendEnv(null, null, null);
    }

    public static Lexeme cons (String type, Lexeme carValue, Lexeme cdrValue) {
        Lexeme newLexeme = new Lexeme(type);
        newLexeme.setPrev(carValue);
        newLexeme.setNext(cdrValue);

        return newLexeme;
    }

    public static Lexeme car(Lexeme cell) {
        return (Lexeme) cell.getPrev();
    }

    public static Lexeme cdr (Lexeme cell) {
        return (Lexeme) cell.getNext();
    }

    public static void setCar(Lexeme cell, Lexeme value) {
        cell.setPrev(value);
    }

    public static void setCdr(Lexeme cell, Lexeme value) {
        cell.setNext(value);
    }

    public static void insert(Lexeme ident, Lexeme value, Lexeme env) {
        while (ident.getNext() != null) {
            ident = (Lexeme) ident.getNext();
        }

        ident = (Lexeme) ident.getPrev();

        Lexeme table = Env.car(env);
        Env.setCar(table ,cons("JOIN", ident, car(table)));
        Env.setCdr(table, cons("JOIN", value, cdr(table)));
    }

    public static Lexeme extendEnv(Lexeme env, Lexeme variables, Lexeme values) {
        return Env.cons("ENV", makeTable(variables, values), env);
    }

    public static Lexeme makeTable(Lexeme variables, Lexeme values) {
        return cons("TABLE", variables, values);
    }

    public static Lexeme updateValue(Lexeme env, Lexeme variable, Lexeme value) {
        while (env != null) {
            Lexeme table = car(env);
            Lexeme variables = car(table);
            Lexeme vals = cdr(table);

            while (variables != null) {
                if (sameVariable(variable, car(variables))) {
                    if ((Lexeme) variable.getNext() == null) {
                        setCar(vals, value);
                        return null;
                    }

                    return updateValue(car(vals), (Lexeme) variable.getNext(), value);
                }

                variables = cdr(variables);
                vals = cdr(vals);
            }

            env = (cdr(env));
        }

        return null;
    }

    public static Lexeme lookupEnv(Lexeme env, Lexeme variable) {
        while (env != null) {
            Lexeme table = car(env);
            Lexeme variables = car(table);
            Lexeme vals = cdr(table);

            while (variables != null) {
                if (sameVariable(variable, car(variables))) {

                    if ((Lexeme) variable.getNext() != null) {
                        return lookupEnv(car(vals), (Lexeme) variable.getNext());
                    }

                    return  car(vals);
                }

                variables = cdr(variables);
                vals = cdr(vals);
            }

            env = Env.cdr(env);
        }

        variable = (Lexeme) variable.getPrev();
        System.out.print("UNDEFINED ERROR. ");
        System.out.println("Line " + variable.getLine() + ": " + variable.getValue() + " is not defined.");
        System.exit(1);
        return null;
    }

    public static boolean sameVariable(Lexeme a, Lexeme b) {
        a = (Lexeme) a.getPrev();
        return a.getValue().equals(b.getValue());
    }
}
