package deportes;

import java.text.Normalizer;
import java.util.Locale;

// Normaliza nombres para que las búsquedas y comparaciones ignoren mayúsculas, tildes y espacios de más.
// "Fútbol", "  futbol " y "FÚTBOL" quedan como "futbol".
public final class NormalizadorNombre {

    private NormalizadorNombre() {
        // Clase de utilidades: no se instancia.
    }

    public static String normalizar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser null ni estar vacío");
        }

        return Normalizer.normalize(nombre.strip(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }
}
