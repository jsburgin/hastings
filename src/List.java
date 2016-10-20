/**
 * Created by josh on 9/28/16.
 */
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

    public void append(ListObject value) {

        if (value == null) {
            return;
        }

        ListNode node = new ListNode(value);

        if (head == null) {
            head = node;
            tail = node;

            return;
        }

        this.tail.setNext(node);
        node.setPrev(tail);
        this.tail = node;
    }

    public void prepend(ListObject value) {
        ListNode node = new ListNode(value);

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

    public ListObject removeFront() {
        ListObject value = this.head.getValue();
        this.head = this.head.getNext();

        if (this.head == null) {
            this.tail = null;
        }

        return value;
    }

    public ListObject removeBack() {
        ListObject value = this.tail.getValue();

        this.tail = this.tail.getPrev();

        if (this.tail == null) {
            this.head = null;
        } else {
            this.tail.setNext(null);
        }

        return value;
    }
}
