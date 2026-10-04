package estructuras;

// Lista doblemente enlazada con head, tail y contador. Como cada nodo conoce a su anterior,
// teniendo la referencia a un nodo se puede borrar o insertar junto a él en O(1)
public class DLL<T> {
    private DLLNode<T> head;
    private DLLNode<T> tail;
    private int count;

    public DLL() {
        this.head = null;
        this.tail = null;
        this.count = 0;
    }

    // retorna el nodo creado, para que quien lo agrega pueda borrarlo después en O(1)
    public DLLNode<T> pushBack(T data) { // O(1)
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
        return n;
    }

    public DLLNode<T> pushFront(T data) { // O(1)
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
        return n;
    }

    public DLLNode<T> addNodeAfter(DLLNode<T> node, T key) { // O(1)
        revisarNodo(node);
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
        return n;
    }

    public DLLNode<T> addNodeBefore(DLLNode<T> node, T key) { // O(1)
        revisarNodo(node);
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
        return n;
    }

    public T popBack() { // O(1): tail.prev es el nuevo último
        revisarNoVacia();
        T data = tail.data;
        deleteNode(tail);
        return data;
    }

    public T popFront() { // O(1)
        revisarNoVacia();
        T data = head.data;
        deleteNode(head);
        return data;
    }

    public T topFront() { // O(1)
        revisarNoVacia();
        return head.data;
    }

    public T topBack() { // O(1)
        revisarNoVacia();
        return tail.data;
    }

    public void deleteNodeAfter(DLLNode<T> node) { // O(1)
        revisarNodo(node);
        if (node.next == null) {
            throw new IllegalArgumentException("No hay un nodo después de este");
        }
        deleteNode(node.next);
    }

    public void deleteNodeBefore(DLLNode<T> node) { // O(1)
        revisarNodo(node);
        if (node.prev == null) {
            throw new IllegalArgumentException("No hay un nodo antes de este");
        }
        deleteNode(node.prev);
    }

    // el nodo debe pertenecer a esta lista; no se revisa porque eso costaría O(n)
    public void deleteNode(DLLNode<T> node) { // O(1)
        revisarNodo(node);
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }
        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        node.next = null;
        node.prev = null;
        count--;
    }

    // retorna el primer nodo cuyo dato es igual a item (con equals), o null si no está
    public DLLNode<T> find(T item) { // O(n)
        DLLNode<T> temp = head;
        while (temp != null) {
            if (temp.data != null && temp.data.equals(item)) {
                return temp;
            }
            temp = temp.next;
        }
        return null;
    }

    // borra el elemento de la posición n (la primera es 1)
    public T erase(int n) { // O(n): toca llegar a la posición
        if (n < 1 || n > count) {
            throw new IllegalArgumentException("Posición fuera de la lista: " + n);
        }
        DLLNode<T> pointer = head;
        for (int i = 1; i < n; i++) {
            pointer = pointer.next;
        }
        deleteNode(pointer);
        return pointer.data;
    }

    public boolean isEmpty() { // O(1)
        return head == null;
    }

    public int size() { // O(1): se lleva un contador
        return count;
    }

    public DLLNode<T> getHead() { // O(1)
        return head;
    }

    public DLLNode<T> getTail() { // O(1)
        return tail;
    }

    @Override
    public String toString() { // O(n)
        StringBuilder texto = new StringBuilder("[");
        DLLNode<T> temp = head;
        while (temp != null) {
            texto.append(temp.data);
            if (temp.next != null) {
                texto.append(", ");
            }
            temp = temp.next;
        }
        return texto.append("]").toString();
    }

    private void revisarNoVacia() {
        if (isEmpty()) {
            throw new IllegalStateException("La lista está vacía");
        }
    }

    private void revisarNodo(DLLNode<T> node) {
        if (node == null) {
            throw new IllegalArgumentException("El nodo no puede ser null");
        }
    }
}
