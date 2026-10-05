package estructuras;

// Nodo de la lista doblemente enlazada: guarda el dato y las referencias al anterior y al siguiente
public class DLLNode<T> {
    T data;
    DLLNode<T> prev;
    DLLNode<T> next;
    DLL<T> lista;

    public DLLNode(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
        this.lista = null;
    }

    public T getData() { // O(1)
        return data;
    }

    public DLLNode<T> getNext() { // O(1)
        return next;
    }

    public DLLNode<T> getPrev() { // O(1)
        return prev;
    }
}
