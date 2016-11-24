import java.util.ArrayList;

public class Lexeme {
    private String type;
    private String value;
    private int line;
    private ArrayList<Lexeme> array;
    private Lexeme next = null;
    private Lexeme prev = null;

    public Lexeme(String type) {
        this.type = type;
        this.value = "_";

        if (this.getType().equals("BOOLF")) {
            this.value = "false";
        } else if (this.getType().equals("BOOLT")) {
            this.value = "true";
        }
    }

    public Lexeme getNext() {
        return this.next;
    }

    public void setNext(Lexeme next) {
        this.next = next;
    }

    public void setPrev(Lexeme prev) {
        this.prev = prev;
    }

    public Lexeme getPrev() {
        return this.prev;
    }

    public void initArray() {
        array = new ArrayList<>();
    }

    public ArrayList<Lexeme> getArray() {
        return array;
    }

    public Lexeme(String type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.next = null;
        this.prev = null;
    }

    public String getType() {
        return this.type;
    }


    public String getValue() {
        return this.value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLine() {
        return this.line;
    }

    public String toString() {

        String localType = this.type;

        while (localType.length() < 8) {
            localType = localType + " ";
        }

        return localType + " : " + value;
    }
}
