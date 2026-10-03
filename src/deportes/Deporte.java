package deportes;

import estructuras.DLL;

// Deporte y lista de los estudiantes que lo practican. La cantidad de practicantes es el tamaño de esa
// lista, que la DLL lleva en un contador, así que contarlos no exige recorrerla
public class Deporte {
    private final String nombre;
    private final DLL<Estudiante> practicantes;

    public Deporte(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del deporte no puede estar vacío");
        }
        this.nombre = nombre.trim();
        this.practicantes = new DLL<>();
    }

    public String getNombre() { // O(1)
        return nombre;
    }

    public DLL<Estudiante> getPracticantes() { // O(1)
        return practicantes;
    }

    public int cantidadPracticantes() { // O(1)
        return practicantes.size();
    }

    @Override
    public String toString() { // O(1)
        return nombre + " (" + cantidadPracticantes() + ")";
    }
}
