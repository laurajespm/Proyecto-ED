package deportes;

import estructuras.ArbolAVL;
import estructuras.DLL;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

// Pruebas manuales de RF2 y RF7.
// RF1 aún no está implementado, por eso los árboles privados se preparan por reflexión
// exclusivamente en esta clase de pruebas. No se añade esta vía al código de producción.
public class PruebasRF2RF7 {

    public static void main(String[] args) throws ReflectiveOperationException {
        probarRF2();
        probarRF7();
        probarNormalizacion();

        System.out.println("\nTodas las pruebas de RF2 y RF7 finalizaron correctamente.");
    }

    private static void probarRF2() throws ReflectiveOperationException {
        System.out.println("=== RF2: buscar estudiante por ID ===");

        SistemaDeportes sistema = new SistemaDeportes();
        ArbolAVL<Integer, Estudiante> estudiantes = obtenerArbol(sistema, "estudiantes");

        Estudiante ana = new Estudiante(
                "Ana Pérez", 1001, 20, 123456789,
                "ana@unal.edu.co", "Ingeniería", "Sistemas"
        );
        Estudiante luis = new Estudiante(
                "Luis Gómez", 1002, 21, 300123456,
                "luis@unal.edu.co", "Ciencias", "Matemáticas"
        );

        estudiantes.insertar(ana.getId(), ana);
        estudiantes.insertar(luis.getId(), luis);

        verificar(sistema.buscarEstudiante(1001) == ana,
                "RF2 debe retornar el mismo estudiante registrado para el ID 1001.");
        verificar(sistema.buscarEstudiante(1002) == luis,
                "RF2 debe retornar el mismo estudiante registrado para el ID 1002.");
        verificar(sistema.buscarEstudiante(9999) == null,
                "RF2 debe retornar null cuando el ID no existe.");

        Estudiante encontrado = sistema.buscarEstudiante(1001);
        verificar("Ana Pérez".equals(encontrado.getNombre()), "Debe conservar el nombre.");
        verificar("ana@unal.edu.co".equals(encontrado.getCorreo()), "Debe conservar el correo.");
        verificar(encontrado.getEdad() == 20, "Debe conservar la edad.");

        System.out.println("OK: IDs existentes, ID inexistente y conservación de los datos.");
    }

    private static void probarRF7() throws ReflectiveOperationException {
        System.out.println("\n=== RF7: deportes ordenados por practicantes ===");

        SistemaDeportes sistema = new SistemaDeportes();
        ArbolAVL<ClaveRanking, Deporte> ranking = obtenerArbol(sistema, "ranking");

        // Cantidades deliberadamente desordenadas para comprobar ambos criterios.
        agregarDeporte(ranking, 1, "Natación", 3);
        agregarDeporte(ranking, 2, "Fútbol", 5);
        agregarDeporte(ranking, 3, "Baloncesto", 5);
        agregarDeporte(ranking, 4, "Atletismo", 1);
        agregarDeporte(ranking, 5, "Ájedrez", 5);

        DLL<Deporte> resultado = sistema.deportesPorPracticantes();
        List<String> nombres = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();

        for (Deporte deporte : resultado) {
            nombres.add(deporte.getNombreCurso());
            cantidades.add(deporte.getCantidadPracticantes());
        }

        List<String> esperado = List.of("Ájedrez", "Baloncesto", "Fútbol", "Natación", "Atletismo");
        verificar(nombres.equals(esperado),
                "Orden esperado: " + esperado + "; orden obtenido: " + nombres);

        verificar(cantidades.equals(List.of(5, 5, 5, 3, 1)),
                "Las cantidades deben quedar en orden descendente: " + cantidades);

        SistemaDeportes sistemaVacio = new SistemaDeportes();
        verificar(sistemaVacio.deportesPorPracticantes().isEmpty(),
                "RF7 debe retornar una DLL vacía cuando no hay deportes.");

        System.out.println("Orden obtenido: " + nombres);
        System.out.println("Cantidades: " + cantidades);
        System.out.println("OK: orden descendente, desempate alfabético y ranking vacío.");
    }

    private static void probarNormalizacion() {
        System.out.println("\n=== Normalización de nombres ===");

        String normalizado = NormalizadorNombre.normalizar("  FÚTBOL  ");
        verificar("futbol".equals(normalizado),
                "Se esperaba 'futbol', pero se obtuvo '" + normalizado + "'.");

        ClaveRanking conTilde = new ClaveRanking(4, "Ájedrez");
        ClaveRanking sinTilde = new ClaveRanking(4, "Baloncesto");
        verificar(conTilde.compareTo(sinTilde) < 0,
                "El desempate debe comparar nombres normalizados alfabéticamente.");

        System.out.println("OK: tildes, mayúsculas y espacios exteriores.");
    }

    private static void agregarDeporte(
            ArbolAVL<ClaveRanking, Deporte> ranking,
            int id,
            String nombre,
            int cantidadPracticantes) {

        Deporte deporte = new Deporte(
                id, nombre, "Por definir", 30, "Prueba", "Por definir"
        );

        for (int i = 0; i < cantidadPracticantes; i++) {
            Estudiante practicante = new Estudiante(
                    "Practicante " + id + "-" + i,
                    id * 100 + i,
                    18,
                    0,
                    null,
                    null,
                    null
            );
            deporte.getListaInscritos().pushBack(practicante);
        }

        ranking.insertar(
                new ClaveRanking(deporte.getCantidadPracticantes(), deporte.getNombreCurso()),
                deporte
        );
    }

    @SuppressWarnings("unchecked")
    private static <K extends Comparable<K>, V> ArbolAVL<K, V> obtenerArbol(
            SistemaDeportes sistema,
            String nombreCampo) throws ReflectiveOperationException {

        Field campo = SistemaDeportes.class.getDeclaredField(nombreCampo);
        campo.setAccessible(true);
        return (ArbolAVL<K, V>) campo.get(sistema);
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
