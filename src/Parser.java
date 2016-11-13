/**
 * Created by josh on 11/13/16.
 */
public class Parser {

    private List input;
    private Lexeme current;

    public Parser(List input) {
        this.input = input;

        if (!this.input.isEmpty()) {
            current = (Lexeme) input.removeFront();
        }
    }

    public void expression() {
        primary();
    }

    public void primary() {

    }
}
