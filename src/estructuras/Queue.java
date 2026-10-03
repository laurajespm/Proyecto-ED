public class Queue<T> implements MyQueue<T> {
    private int n;
    private int front, rear, count;
    private T[] qarray;

    public Queue(int n) {
        this.n = n;
        this.qarray = (T[]) new Object[n];
        this.front = 0;
        this.rear = 0;
        this.count = 0;
    }

    @Override
    public void enqueue(T x) {
        if (isFull()) {
            System.out.println("La cola está llena. No se pueden agregar más elementos.");
            return;
        }

        qarray[rear] = x;
        rear = (rear + 1) % qarray.length;
        count++;
    }

    @Override
    public T dequeue() {
        if (isEmpty()) {
            System.out.println("La cola está vacía. No hay elementos para descolar.");
            return null;
        }

        T item = qarray[front];
        qarray[front] = null; // Limpieza de referencia
        front = (front + 1) % qarray.length;
        count--;
        return item;
    }

    @Override
    public T front() {
        if (isEmpty()) {
            System.out.println("La cola está vacía.");
            return null;
        }
        return qarray[front];
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == n;
    }

    public int size() {
        return count;
    }

    public void delete(T item) {
        if (isEmpty()) {
            System.out.println("La cola está vacía. Nada que eliminar.");
            return;
        }

        int i = front;
        for (int j = 0; j < count; j++) {
            if (qarray[i] != null && qarray[i].equals(item)) {
                int current = i;
                int next = (current + 1) % qarray.length;

                while (next != rear) {
                    qarray[current] = qarray[next];
                    current = next;
                    next = (next + 1) % qarray.length;
                }

                rear = (rear - 1 + qarray.length) % qarray.length;
                qarray[rear] = null; // Se limpia la posición liberada
                count--;
                return;
            }
            i = (i + 1) % qarray.length;
        }
        System.out.println("Elemento no encontrado.");
    }
}