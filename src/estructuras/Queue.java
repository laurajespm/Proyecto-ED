package estructuras;

// Cola sobre un arreglo circular: el frente está en front y la posición lógica i está en
// (front + i) % capacidad. Cuando se llena, la capacidad se duplica (el recorrido por anchura
// no sabe de antemano cuántos estudiantes va a encolar)
public class Queue<T> implements MyQueue<T> {
    private T[] qarray;
    private int front;
    private int count;

    public Queue() {
        this(8);
    }

    @SuppressWarnings("unchecked") // Java no deja crear arreglos de T; se crea de Object y se convierte
    public Queue(int capacidadInicial) {
        if (capacidadInicial < 1) {
            throw new IllegalArgumentException("La capacidad inicial debe ser al menos 1");
        }
        this.qarray = (T[]) new Object[capacidadInicial];
        this.front = 0;
        this.count = 0;
    }

    @Override
    public void enqueue(T x) { // O(1) amortizado; O(n) cuando toca duplicar
        if (count == qarray.length) {
            redimensionar();
        }
        qarray[fisico(count)] = x;
        count++;
    }

    @Override
    public T dequeue() { // O(1): solo avanza el frente
        revisarNoVacia();
        T item = qarray[front];
        qarray[front] = null; // para que el recolector de basura lo pueda liberar
        front = (front + 1) % qarray.length;
        count--;
        return item;
    }

    @Override
    public T front() { // O(1)
        revisarNoVacia();
        return qarray[front];
    }

    @Override
    public boolean isEmpty() { // O(1)
        return count == 0;
    }

    @Override
    public int size() { // O(1)
        return count;
    }

    @Override
    public boolean delete(T item) { // O(n): buscar desde el frente y correr los de atrás
        for (int i = 0; i < count; i++) {
            if (qarray[fisico(i)] != null && qarray[fisico(i)].equals(item)) {
                for (int j = i; j < count - 1; j++) {
                    qarray[fisico(j)] = qarray[fisico(j + 1)];
                }
                qarray[fisico(count - 1)] = null;
                count--;
                return true;
            }
        }
        return false;
    }

    public int capacidad() { // O(1)
        return qarray.length;
    }

    private int fisico(int i) { // O(1)
        return (front + i) % qarray.length;
    }

    @SuppressWarnings("unchecked")
    private void redimensionar() { // O(n): copia en orden lógico, desde el frente, a partir de la casilla 0
        T[] nuevo = (T[]) new Object[qarray.length * 2];
        for (int i = 0; i < count; i++) {
            nuevo[i] = qarray[fisico(i)];
        }
        qarray = nuevo;
        front = 0;
    }

    private void revisarNoVacia() {
        if (count == 0) {
            throw new IllegalStateException("La cola está vacía");
        }
    }
}
