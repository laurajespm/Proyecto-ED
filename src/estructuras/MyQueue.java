package estructuras;

public interface MyQueue<T> {

    void enqueue(T x);

    T dequeue();

    T front();

    boolean isEmpty();

    int size();

    // elimina el primer valor n que encuentra empezando por el frente
    boolean delete(T n);
}
