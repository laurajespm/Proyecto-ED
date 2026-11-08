package estructuras;

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

    public V buscar(K clave) {
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

    public void insertar(K clave, V valor) {
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

    public V eliminar(K clave) {
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
                Nodo<K, V> sucesor = nodoMinimo(nodo.derecho);
                
                // Usamos un nodo temporal o modificamos los campos directamente para evitar perder la inmutabilidad de 'clave' si fuera necesario, 
                // pero dado que 'clave' en tu plantilla es final, recreamos la estructura de punteros o copiamos los campos no-finales.
                // Como 'clave' es final, no podemos hacer nodo.clave = sucesor.clave. 
                // En su lugar, removemos el sucesor de abajo y reemplazamos el contenido de este nodo de forma segura:
                nodo.valor = sucesor.valor;
                // Para solucionar que 'clave' es final, pasamos el valor de la clave al eliminar recursivo:
                nodo.derecho = eliminarRecursivo(nodo.derecho, sucesor.clave);
                
                // NOTA: Para mantener consistencia con clave 'final', la simulación de intercambiar el valor se hace modificando las referencias o re-enlazando.
                // Sin embargo, esta es la estrategia clásica de reemplazo de contenido adaptada a claves inmutables.
                // Creamos un nodo sustituto que herede los hijos del actual pero con la clave/valor del sucesor
                Nodo<K, V> nuevoNodo = new Nodo<>(sucesor.clave, sucesor.valor);
                nuevoNodo.izquierdo = nodo.izquierdo;
                nuevoNodo.derecho = eliminarRecursivo(nodo.derecho, sucesor.clave);
                nuevoNodo.altura = nodo.altura;
                nodo = nuevoNodo;
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

    public DLL<V> valoresEnOrden() {
        DLL<V> lista = new DLL<>();
        valoresEnOrdenRecursivo(raiz, lista);
        return lista;
    }

    private void valoresEnOrdenRecursivo(Nodo<K, V> nodo, DLL<V> lista) {
        if (nodo != null) {
            valoresEnOrdenRecursivo(nodo.izquierdo, lista);
            lista.add(nodo.valor); 
            valoresEnOrdenRecursivo(nodo.derecho, lista);
        }
    }

    public int size() {
        return tamano;
    }

    public boolean isEmpty() {
        return raiz == null;
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
