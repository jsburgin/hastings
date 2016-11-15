public class ListNode {
    protected ListNode next;
    protected ListNode prev;

    public ListNode() {
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
}
