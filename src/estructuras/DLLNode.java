public class DLLNode<T> {
    T data;
    DLLNode<T> prev;
    DLLNode<T> next;

    public DLLNode(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}