/**
 * Created by josh on 9/28/16.
 */
public class ListNode {
    private ListNode next;
    private ListNode prev;
    private ListObject value;

    public ListNode(ListObject value) {
        this.value = value;
        this.next = null;
        this.prev = null;
    }

    public ListNode getNext() {
        return this.next;
    }

    public void setNext(ListNode next) {
        this.next = next;
    }

    public ListNode getPrev() { return this.prev; }

    public void setPrev(ListNode prev) { this.prev = prev; }

    public ListObject getValue() {
        return this.value;
    }

    public void setValue(ListObject value) {
        this.value = value;
    }
}
