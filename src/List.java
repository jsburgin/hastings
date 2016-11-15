
public class List {
    private ListNode head;
    private ListNode tail;

    public List() {
        this.head = null;
        this.tail = null;
    }

    public boolean isEmpty() {
        return this.head == null;
    }

    public void append(ListNode node) {

        if (node == null) {
            return;
        }

        if (head == null) {
            head = node;
            tail = node;

            return;
        }

        this.tail.setNext(node);
        node.setPrev(tail);
        this.tail = node;
    }

    public void prepend(ListNode node) {

        if (head == null) {
            head = node;
            tail = node;

            return;
        }

        node.setNext(this.head);
        this.head.setPrev(node);
        this.head = node;
    }

    public ListNode peekFront() {
        return this.head;
    }
    public ListNode peekBack() { return this.tail; }

    public ListNode removeFront() {
        ListNode value = this.head;
        this.head = this.head.getNext();

        if (this.head == null) {
            this.tail = null;
        }

        return value;
    }

    public ListNode removeBack() {
        ListNode value = this.tail;

        this.tail = this.tail.getPrev();

        if (this.tail == null) {
            this.head = null;
        } else {
            this.tail.setNext(null);
        }

        return value;
    }
}
