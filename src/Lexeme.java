public class Lexeme extends ListNode {
    private String type;
    private String value;
    private int line;

    public Lexeme(String type) {
        this.type = type;
        this.value = null;
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
