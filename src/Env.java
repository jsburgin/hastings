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

    public static Lexeme lookupEnv(Lexeme env, Lexeme variable) {
        while (env != null) {
            Lexeme table = car(env);
            Lexeme variables = car(table);
            Lexeme vals = cdr(table);

            while (variables != null) {
                if (sameVariable(car(variables), variable)) {
                    return  car(vals);
                }

                variables = cdr(variables);
                vals = cdr(vals);
            }

            env = Env.cdr(env);
        }

        return null;
    }

    public static boolean sameVariable(Lexeme a, Lexeme b) {
        return a.getValue().equals(b.getValue());
    }
}
