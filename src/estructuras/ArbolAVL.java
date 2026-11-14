package estructuras;

// Árbol AVL de clave y valor: árbol binario de búsqueda que después de cada inserción o eliminación revisa el
// factor de balance de los nodos del camino y hace rotaciones para que las alturas de los dos subárboles de
// cada nodo no difieran en más de 1. Así la altura queda en O(log n) aunque las claves lleguen en orden.
// Los valores no pueden ser null, porque buscar retorna null para decir que la clave no está.
public class ArbolAVL<K extends Comparable<K>, V> {

    private static class Nodo<K, V> {
        final K clave;
        V valor;
        int altura;
        Nodo<K, V> izquierdo;
        Nodo<K, V> derecho;

        Nodo(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
            this.altura = 1;
        }
    }

    private Nodo<K, V> raiz;
    private int tamano;

    private int altura(Nodo<K, V> n) {
        return (n == null) ? 0 : n.altura;
    }

    private int obtenerFactorBalance(Nodo<K, V> n) {
        return (n == null) ? 0 : altura(n.izquierdo) - altura(n.derecho);
    }

    private void actualizarAltura(Nodo<K, V> n) {
        if (n != null) {
            n.altura = 1 + Math.max(altura(n.izquierdo), altura(n.derecho));
        }
    }

    public V buscar(K clave) { // O(log n)
        revisarClave(clave);
        Nodo<K, V> actual = raiz;
        while (actual != null) {
            int cmp = clave.compareTo(actual.clave);
            if (cmp == 0) {
                return actual.valor;
            }
            actual = (cmp < 0) ? actual.izquierdo : actual.derecho;
        }
        return null;
    }

    public boolean contiene(K clave) { // O(log n)
        return buscar(clave) != null;
    }

    // inserta la pareja; si la clave ya existe, cambia el valor
    public void insertar(K clave, V valor) { // O(log n)
        revisarClave(clave);
        if (valor == null) {
            throw new IllegalArgumentException("El valor no puede ser null");
        }
        raiz = insertarRecursivo(raiz, clave, valor);
    }

    private Nodo<K, V> insertarRecursivo(Nodo<K, V> nodo, K clave, V valor) {
        if (nodo == null) {
            tamano++;
            return new Nodo<>(clave, valor);
        }

        int cmp = clave.compareTo(nodo.clave);

        if (cmp < 0) {
            nodo.izquierdo = insertarRecursivo(nodo.izquierdo, clave, valor);
        } else if (cmp > 0) {
            nodo.derecho = insertarRecursivo(nodo.derecho, clave, valor);
        } else {
            nodo.valor = valor;
            return nodo;
        }

        actualizarAltura(nodo);
        int balance = obtenerFactorBalance(nodo);

        if (balance > 1 && clave.compareTo(nodo.izquierdo.clave) < 0) {
            return rotarDerecha(nodo);
        }

        if (balance < -1 && clave.compareTo(nodo.derecho.clave) > 0) {
            return rotarIzquierda(nodo);
        }

        if (balance > 1 && clave.compareTo(nodo.izquierdo.clave) > 0) {
            nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            return rotarDerecha(nodo);
        }

        if (balance < -1 && clave.compareTo(nodo.derecho.clave) < 0) {
            nodo.derecho = rotarDerecha(nodo.derecho);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    // quita la clave y retorna su valor, o null si no estaba
    public V eliminar(K clave) { // O(log n)
        V valorEliminado = buscar(clave);
        if (valorEliminado != null) {
            raiz = eliminarRecursivo(raiz, clave);
            tamano--;
        }
        return valorEliminado;
    }

    private Nodo<K, V> eliminarRecursivo(Nodo<K, V> nodo, K clave) {
        if (nodo == null) {
            return null;
        }

        int cmp = clave.compareTo(nodo.clave);

        if (cmp < 0) {
            nodo.izquierdo = eliminarRecursivo(nodo.izquierdo, clave);
        } else if (cmp > 0) {
            nodo.derecho = eliminarRecursivo(nodo.derecho, clave);
        } else {
            if (nodo.izquierdo == null || nodo.derecho == null) {
                nodo = (nodo.izquierdo != null) ? nodo.izquierdo : nodo.derecho;
            } else {
                // con dos hijos, el nodo se reemplaza por su sucesor inorden (el menor del subárbol derecho).
                // La clave es final, así que se crea un nodo nuevo con la clave y el valor del sucesor, que
                // hereda los hijos, y el sucesor se borra una sola vez del subárbol derecho
                Nodo<K, V> sucesor = nodoMinimo(nodo.derecho);
                Nodo<K, V> reemplazo = new Nodo<>(sucesor.clave, sucesor.valor);
                reemplazo.izquierdo = nodo.izquierdo;
                reemplazo.derecho = eliminarRecursivo(nodo.derecho, sucesor.clave);
                nodo = reemplazo;
            }
        }

        if (nodo == null) {
            return null;
        }

        actualizarAltura(nodo);
        int balance = obtenerFactorBalance(nodo);

        if (balance > 1 && obtenerFactorBalance(nodo.izquierdo) >= 0) {
            return rotarDerecha(nodo);
        }

        if (balance > 1 && obtenerFactorBalance(nodo.izquierdo) < 0) {
            nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            return rotarDerecha(nodo);
        }

        if (balance < -1 && obtenerFactorBalance(nodo.derecho) <= 0) {
            return rotarIzquierda(nodo);
        }

        if (balance < -1 && obtenerFactorBalance(nodo.derecho) > 0) {
            nodo.derecho = rotarDerecha(nodo.derecho);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    private Nodo<K, V> nodoMinimo(Nodo<K, V> nodo) {
        Nodo<K, V> actual = nodo;
        while (actual.izquierdo != null) {
            actual = actual.izquierdo;
        }
        return actual;
    }

    // valores ordenados por clave (recorrido inorden)
    public DLL<V> valoresEnOrden() { // O(n)
        DLL<V> lista = new DLL<>();
        valoresEnOrdenRecursivo(raiz, lista);
        return lista;
    }

    private void valoresEnOrdenRecursivo(Nodo<K, V> nodo, DLL<V> lista) {
        if (nodo != null) {
            valoresEnOrdenRecursivo(nodo.izquierdo, lista);
            lista.pushBack(nodo.valor);
            valoresEnOrdenRecursivo(nodo.derecho, lista);
        }
    }

    public int size() { // O(1)
        return tamano;
    }

    public boolean isEmpty() { // O(1)
        return raiz == null;
    }

    // número de niveles: 0 si está vacío, 1 si solo tiene la raíz
    public int altura() { // O(1)
        return altura(raiz);
    }

    // dibujo del árbol acostado (la raíz a la izquierda y el subárbol derecho arriba), con la altura de cada
    // nodo entre corchetes; sirve para ver las rotaciones con pocos datos
    public String dibujar() { // O(n)
        if (raiz == null) {
            return "(árbol vacío)\n";
        }
        StringBuilder texto = new StringBuilder();
        dibujar(raiz, "", texto);
        return texto.toString();
    }

    private void dibujar(Nodo<K, V> nodo, String sangria, StringBuilder texto) {
        if (nodo == null) {
            return;
        }
        dibujar(nodo.derecho, sangria + "        ", texto);
        texto.append(sangria).append(nodo.clave).append(" [").append(nodo.altura).append("]\n");
        dibujar(nodo.izquierdo, sangria + "        ", texto);
    }

    private void revisarClave(K clave) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave no puede ser null");
        }
    }

    private Nodo<K, V> rotarDerecha(Nodo<K, V> nodo) {
        Nodo<K, V> x = nodo.izquierdo;
        Nodo<K, V> T2 = x.derecho;

        x.derecho = nodo;
        nodo.izquierdo = T2;

        actualizarAltura(nodo);
        actualizarAltura(x);

        return x;
    }

    private Nodo<K, V> rotarIzquierda(Nodo<K, V> nodo) {
        Nodo<K, V> y = nodo.derecho;
        Nodo<K, V> T2 = y.izquierdo;

        y.izquierdo = nodo;
        nodo.derecho = T2;

        actualizarAltura(nodo);
        actualizarAltura(y);

        return y;
    }
}
