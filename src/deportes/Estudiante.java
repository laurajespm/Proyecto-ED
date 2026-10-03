package deportes;

import estructuras.DLL;

// Estudiante registrado en el sistema. Los deportes que practica y los que le interesan se guardan en
// listas doblemente enlazadas porque cambian con cada registro o eliminación y no tienen tamaño fijo
public class Estudiante {
    private final int id;
    private final String nombre;
    private final DLL<Deporte> practica;
    private final DLL<Deporte> intereses;

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

    public DLL<Deporte> getPractica() { // O(1)
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
