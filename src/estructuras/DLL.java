public class DLL<T> {
    private DLLNode<T> head;
    private DLLNode<T> tail;
    private int count;

    public DLL() {
        this.head = null;
        this.tail = null;
        this.count = 0;
    }

    public void pushBack(T data) {
        DLLNode<T> n = new DLLNode<>(data);

        if (isEmpty()) {
            head = n;
            tail = n;
        } else {
            tail.next = n;
            n.prev = tail;
            tail = n;
        }

        count++;
    }

    public void pushFront(T data) {
        DLLNode<T> n = new DLLNode<>(data);
        if (isEmpty()) {
            head = n;
            tail = n;
        } else {
            n.next = head;
            head.prev = n;
            head = n;
        }
        count++;
    }

    public void addNodeAfter(DLLNode<T> node, T key) {
        if (node == null) {
            System.out.println("Invalid node.");
            return;
        }

        DLLNode<T> n = new DLLNode<>(key);
        n.next = node.next;
        n.prev = node;

        if (node.next != null) {
            node.next.prev = n;
        } else {
            tail = n;
        }
        node.next = n;
        count++;
    }

    public void addNodeBefore(DLLNode<T> node, T key) {
        if (node == null) {
            System.out.println("Invalid node.");
            return;
        }

        DLLNode<T> n = new DLLNode<>(key);
        n.prev = node.prev;
        n.next = node;

        if (node.prev != null) {
            node.prev.next = n;
        } else {
            head = n;
        }

        node.prev = n;
        count++;
    }

    public void popBack() {
        if (isEmpty()) {
            System.out.println("The list is empty. Nothing to delete.");
            return;
        } else if (head == tail) {
            head = null;
            tail = null;
            count = 0;
            return;
        }
        tail = tail.prev;
        tail.next = null;
        count--;
    }

    public void popFront() {
        if (isEmpty()) {
            System.out.println("The list is empty. Nothing to delete.");
            return;
        } else if (head == tail) {
            head = null;
            tail = null;
            count = 0;
            return;
        }
        head = head.next;
        head.prev = null;
        count--;
    }

    public void deleteNodeAfter(DLLNode<T> node) {
        if (isEmpty()) {
            System.out.println("The list is empty, nothing to delete.");
            return;
        }

        if (node == null || node.next == null) {
            System.out.println("There is no node after this node.");
            return;
        }

        DLLNode<T> nodeToDelete = node.next;

        if (nodeToDelete.next != null) {
            node.next = nodeToDelete.next;
            nodeToDelete.next.prev = node;
        } else {
            node.next = null;
            tail = node;
        }
        count--;
    }

    public void deleteNodeBefore(DLLNode<T> node) {
        if (isEmpty()) {
            System.out.println("The list is empty, nothing to delete.");
            return;
        }
        if (node == null || node.prev == null) {
            System.out.println("There is no node before this node.");
            return;
        }

        DLLNode<T> nodeToDelete = node.prev;

        if (nodeToDelete.prev != null) {
            node.prev = nodeToDelete.prev;
            nodeToDelete.prev.next = node;
        } else {
            node.prev = null;
            head = node;
        }
        count--;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public DLLNode<T> find(T item) {
        DLLNode<T> temp = head;

        if (!isEmpty()) {
            while (temp != null) {
                if (temp.data != null && temp.data.equals(item)) {
                    return temp;
                }
                temp = temp.next;
            }
        }
        System.out.println("The element was not found.");
        return null;
    }

    public void erase(int n) {
        if (isEmpty()) {
            System.out.println("The list is empty. Nothing to delete.");
            return;
        }
        if (n <= 0) {
            System.out.println("Invalid position.");
            return;
        }
        if (n == 1) {
            popFront();
            return;
        }

        int counter = 1;
        DLLNode<T> pointer = head;

        while (counter < (n - 1) && pointer != null && pointer.next != null) {
            pointer = pointer.next;
            counter++;
        }
        if (pointer == null || pointer.next == null) {
            System.out.println("Position out of bounds.");
            return;
        }

        DLLNode<T> temp = pointer.next;

        pointer.next = temp.next;

        if (temp.next != null) {
            temp.next.prev = pointer;
        } else {
            tail = pointer;
        }

        temp.next = null;
        temp.prev = null;
        count--;
    }

    public DLLNode<T> getHead() {
        return head;
    }

    public DLLNode<T> getTail() {
        return tail;
    }

    public int size() {
        return count;
    }

    public void deleteNode(DLLNode<T> node){
        if (isEmpty() || node == null) {
            System.out.println("No se puede eliminar: nodo inválido o lista vacía.");
            return;
        }

        if (head == tail && node == head) {
            head = null;
            tail = null;
            count = 0;
            return;
        }

        if (node == head) {
            popFront();
            return;
        }

        if (node == tail) {
            popBack();
            return;
        }

        node.prev.next = node.next;
        node.next.prev = node.prev;

        node.next = null;
        node.prev = null;

        count--;

    }
}
