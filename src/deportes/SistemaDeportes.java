package deportes;

import estructuras.ArbolAVL;
import estructuras.DLL;

// Plantilla del sistema para la Entrega 1. Cada método corresponde a un requisito funcional del reporte
// (RF1 a RF7) y su comentario dice qué estructura va a usar; la lógica se implementa en la Entrega 2.
// Notación de los costos: n estudiantes, d deportes, p deportes que practica un estudiante y m el total
// de parejas (estudiante, deporte que practica).
//
// Estudiantes y deportes forman un grafo bipartito implícito: cada estudiante tiene la lista de deportes
// que practica y cada deporte la lista de sus practicantes, y esas listas hacen de listas de adyacencia.
// Dos estudiantes están conectados directamente si comparten un deporte, e indirectamente si hay una
// cadena de estudiantes que comparten deportes entre sí.
public class SistemaDeportes {

    private final ArbolAVL<Integer, Estudiante> estudiantes = new ArbolAVL<>(); // acceso por ID
    private final ArbolAVL<String, Deporte> deportes = new ArbolAVL<>(); // deportes por nombre
    private final ArbolAVL<ClaveRanking, Deporte> ranking = new ArbolAVL<>(); // deportes por practicantes

    // RF1. Registrar un estudiante: se busca el ID en el AVL de estudiantes para rechazar repetidos; cada
    // deporte se busca o se crea en el AVL de deportes; el estudiante se agrega a la DLL de practicantes de
    // cada deporte que practica y el deporte se mueve en el ranking (sacar con la clave vieja e insertar
    // con la nueva). Los deportes de interés solo van a la DLL de intereses del estudiante
    public Estudiante registrarEstudiante(int id, String nombre, String[] practica, String[] intereses) {
        // O(log n + p log d)
        throw pendiente();
    }

    // RF2. Datos completos de un estudiante por su ID, con una búsqueda en el AVL; null si no existe
    public Estudiante buscarEstudiante(int id) { // O(log n)
        throw pendiente();
    }

    // RF3. Eliminar un estudiante: se quita del AVL de estudiantes y de la DLL de cada deporte que
    // practicaba, y se actualiza la posición de esos deportes en el ranking
    public boolean eliminarEstudiante(int id) { // O(log n + p log d)
        throw pendiente();
    }

    // RF4. Comunidades deportivas: desde cada estudiante que no se haya visitado se hace un recorrido por
    // anchura con la cola, pasando de estudiante a deporte y de deporte a sus practicantes; cada recorrido
    // encuentra un grupo, y un grupo de dos o más estudiantes es una comunidad
    public DLL<DLL<Estudiante>> comunidades() { // O(n + m)
        throw pendiente();
    }

    // RF5 y RF6. Conexión con alguien que practica uno de los deportes de interés del estudiante: recorrido
    // por anchura desde él. La cola visita por niveles, así que el primer estudiante que practica un deporte
    // de interés es el de menos intermediarios. Retorna el camino (del estudiante al contacto) o una lista
    // vacía si no existe la conexión
    public DLL<Estudiante> buscarConexion(int id) { // O(n + m)
        throw pendiente();
    }

    // RF7. Deportes de más a menos practicantes (con empate, por nombre): recorrido inorden del ranking
    public DLL<Deporte> deportesPorPracticantes() { // O(d)
        throw pendiente();
    }

    private static UnsupportedOperationException pendiente() {
        return new UnsupportedOperationException("Pendiente para la Entrega 2");
    }
}
