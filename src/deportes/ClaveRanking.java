package deportes;

// Clave del árbol AVL que ordena los deportes: primero los de más practicantes y, con empate, por nombre.
// El nombre se normaliza para ignorar mayúsculas y tildes en el desempate.
public class ClaveRanking implements Comparable<ClaveRanking> {
    private final int cantidad;
    private final String nombreNormalizado;

    public ClaveRanking(int cantidad, String nombre) {
        this.cantidad = cantidad;
        this.nombreNormalizado = NormalizadorNombre.normalizar(nombre);
    }

    @Override
    public int compareTo(ClaveRanking otra) { // O(L), con L la longitud del nombre
        if (cantidad != otra.cantidad) {
            return Integer.compare(otra.cantidad, cantidad); // más practicantes va primero
        }
        return nombreNormalizado.compareTo(otra.nombreNormalizado);
    }
}
