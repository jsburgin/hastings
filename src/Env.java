public class Env {

    public Lexeme createEnv() {
        return extendEnv(null, null, null);
    }

    private Lexeme cons (String type, Lexeme carValue, Lexeme cdrValue) {
        Lexeme newLexeme = new Lexeme(type);
        newLexeme.setPrev(carValue);
        newLexeme.setNext(cdrValue);

        return newLexeme;
    }

    private Lexeme car(Lexeme cell) {
        return (Lexeme) cell.getPrev();
    }

    private Lexeme cdr (Lexeme cell) {
        return (Lexeme) cell.getNext();
    }

    private void setCar(Lexeme cell, Lexeme value) {
        cell.setPrev(value);
    }

    private void setCdr(Lexeme cell, Lexeme value) {
        cell.setNext(value);
    }

    public Lexeme extendEnv(Lexeme env, Lexeme variables, Lexeme values) {
        return cons("ENV", makeTable(variables, values), env);
    }

    public Lexeme makeTable(Lexeme variables, Lexeme values) {
        return cons("TABLE", variables, values);
    }

    public Lexeme lookupEnv(Lexeme env, Lexeme variable) {
        while (env != null) {
            Lexeme table = car(env);
            Lexeme variables = car(table);
            Lexeme vals = cdr(table);

            while (variables != null) {
                if (sameVariable(car(variables), variable)) {
                    return  car(variables);
                }

                variables = cdr(variables);
                vals = cdr(vals);
            }
        }

        return null;
    }

    public boolean sameVariable(Lexeme a, Lexeme b) {
        return a.getValue().equals(b.getValue());
    }
}
