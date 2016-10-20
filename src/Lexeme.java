public class Lexeme extends ListObject {
    private String type;
    private String value;

    public Lexeme(String type, String value) {
        this.type = type;
        this.value = value;
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

    public String toString() {

        String localType = this.type;

        while (localType.length() < 8) {
            localType = localType + " ";
        }

        return localType + " : " + value;
    }
}
