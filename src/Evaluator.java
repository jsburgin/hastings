/**
 * Created by josh on 11/13/16.
 */
public class Evaluator {
    Lexeme globalEnv;
    Lexeme current;

    public Evaluator() {
        globalEnv = new Env().createEnv();
    }

}
