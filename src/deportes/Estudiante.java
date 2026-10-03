package deportes;

import estructuras.DLL;
import estructuras.DLLNode;

// Estudiante registrado en el sistema. Los deportes que practica y los que le interesan se guardan en
// listas doblemente enlazadas porque cambian con cada registro o eliminación y no tienen tamaño fijo
public class Estudiante {
    private final int id;
    private final String nombre;
    private final DLL<Practica> practica;
    private final DLL<Deporte> intereses;

    // Cada deporte que practica se guarda junto con el nodo que el estudiante ocupa en la lista de
    // practicantes de ese deporte (el que retorna pushBack). Con ese nodo, al eliminar al estudiante se
    // le quita de cada lista en O(1), sin buscarlo
    public static class Practica {
        private final Deporte deporte;
        private final DLLNode<Estudiante> nodo;

        public Practica(Deporte deporte, DLLNode<Estudiante> nodo) {
            this.deporte = deporte;
            this.nodo = nodo;
        }

        public Deporte getDeporte() { // O(1)
            return deporte;
        }

        public DLLNode<Estudiante> getNodo() { // O(1)
            return nodo;
        }
    }

    public Estudiante(int id, String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.practica = new DLL<>();
        this.intereses = new DLL<>();
    }

    public int getId() { // O(1)
        return id;
    }

    public String getNombre() { // O(1)
        return nombre;
    }

    public DLL<Practica> getPractica() { // O(1)
        return practica;
    }

    public DLL<Deporte> getIntereses() { // O(1)
        return intereses;
    }

    @Override
    public String toString() { // O(1)
        return nombre + " (ID " + id + ")";
    }
}
