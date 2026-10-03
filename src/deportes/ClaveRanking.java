package deportes;

// Clave del árbol AVL que ordena los deportes: primero los de más practicantes y, con empate, por nombre.
// Cuando cambia la cantidad de un deporte, se saca del árbol con la clave vieja y se inserta con la nueva
public class ClaveRanking implements Comparable<ClaveRanking> {
    private final int cantidad;
    private final String nombre;

    public ClaveRanking(int cantidad, String nombre) {
        this.cantidad = cantidad;
        this.nombre = nombre;
    }

    @Override
    public int compareTo(ClaveRanking otra) { // O(L), con L la longitud del nombre
        if (cantidad != otra.cantidad) {
            return Integer.compare(otra.cantidad, cantidad); // más practicantes va primero
        }
        return nombre.compareTo(otra.nombre);
    }
}
