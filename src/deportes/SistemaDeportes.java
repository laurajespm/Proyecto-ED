package deportes;

import estructuras.ArbolAVL;
import estructuras.DLL;
import estructuras.DLLNode;
import estructuras.Queue;

// Sistema que atiende los requisitos funcionales RF1 a RF7. Notación de los costos: n estudiantes, d deportes,
// p e i los deportes que practica y que le interesan a un estudiante, y m el total de parejas (estudiante,
// deporte que practica).
//
// Estudiantes y deportes forman un grafo bipartito implícito: cada estudiante tiene la lista de deportes que
// practica y cada deporte la lista de sus practicantes, y esas listas hacen de listas de adyacencia. Dos
// estudiantes están conectados directamente si comparten un deporte, e indirectamente si hay una cadena de
// estudiantes que comparten deportes entre sí. Las comunidades y la conexión se calculan recorriendo ese
// grafo por anchura con la cola.
public class SistemaDeportes {

    // los deportes que se crean al registrar estudiantes no tienen límite de cupos
    private static final int SIN_LIMITE_DE_CUPOS = Integer.MAX_VALUE;

    private final ArbolAVL<Integer, Estudiante> estudiantes = new ArbolAVL<>(); // acceso por ID
    private final ArbolAVL<String, Deporte> deportes = new ArbolAVL<>(); // deportes por nombre normalizado
    private final ArbolAVL<ClaveRanking, Deporte> ranking = new ArbolAVL<>(); // deportes por practicantes

    // Cada recorrido usa un número distinto: un estudiante o un deporte está visitado si su marca es igual al
    // número del recorrido actual. Así no hay que limpiar marcas antes de cada recorrido y revisar si alguien ya
    // se visitó cuesta O(1), sin usar conjuntos de java.util
    private int numeroRecorrido = 0;

    // RF1. Registrar un estudiante: se busca el ID en el AVL de estudiantes para rechazar repetidos y cada
    // deporte se busca o se crea en el AVL de deportes. Por cada deporte que practica, el estudiante se agrega a
    // la DLL de practicantes, guarda el nodo que retorna pushBack y el deporte se mueve en el ranking (sacar con
    // la clave vieja e insertar con la nueva). Los deportes de interés van solo a la DLL de intereses del
    // estudiante, y un deporte que ya practica no se cuenta como interés. Los nombres vacíos se ignoran
    public Estudiante registrarEstudiante(int id, String nombre, String[] practica, String[] intereses) {
        // O(log n + (p + i) log d + (p + i)²); p e i son pocos, así que en la práctica es O(log n + log d)
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del estudiante no puede estar vacío");
        }
        if (estudiantes.contiene(id)) {
            throw new IllegalArgumentException("Ya existe un estudiante con el ID " + id);
        }
        Estudiante estudiante = new Estudiante(nombre, id, 0, 0, "", "", "");
        if (practica != null) {
            for (String nombreDeporte : practica) {
                if (nombreDeporte == null || nombreDeporte.isBlank()) {
                    continue;
                }
                Deporte deporte = obtenerOCrearDeporte(nombreDeporte);
                if (contienePractica(estudiante, deporte)) {
                    continue; // el deporte estaba repetido en la lista
                }
                int cantidadAnterior = deporte.getCantidadPracticantes();
                DLLNode<Estudiante> nodo = deporte.getListaInscritos().pushBack(estudiante);
                estudiante.getPractica().pushBack(new Estudiante.Practica(deporte, nodo));
                actualizarRanking(deporte, cantidadAnterior);
            }
        }
        if (intereses != null) {
            for (String nombreDeporte : intereses) {
                if (nombreDeporte == null || nombreDeporte.isBlank()) {
                    continue;
                }
                Deporte deporte = obtenerOCrearDeporte(nombreDeporte);
                if (!contienePractica(estudiante, deporte) && !contieneInteres(estudiante, deporte)) {
                    estudiante.getIntereses().pushBack(deporte);
                }
            }
        }
        estudiantes.insertar(id, estudiante);
        return estudiante;
    }

    // RF2. Datos completos de un estudiante por su ID, con una búsqueda en el AVL; null si no existe
    public Estudiante buscarEstudiante(int id) { // O(log n)
        return estudiantes.buscar(id);
    }

    // RF3. Eliminar un estudiante: se quita del AVL de estudiantes y, con los nodos que guardó al registrarse,
    // de la DLL de cada deporte que practicaba en O(1); luego se actualiza el ranking de esos deportes
    public boolean eliminarEstudiante(int id) { // O(log n + p log d)
        Estudiante estudiante = estudiantes.eliminar(id);
        if (estudiante == null) {
            return false;
        }
        for (DLLNode<Estudiante.Practica> n = estudiante.getPractica().getHead(); n != null; n = n.getNext()) {
            Deporte deporte = n.getData().getDeporte();
            int cantidadAnterior = deporte.getCantidadPracticantes();
            deporte.getListaInscritos().deleteNode(n.getData().getNodo()); // O(1) con el nodo guardado
            actualizarRanking(deporte, cantidadAnterior);
        }
        return true;
    }

    // RF4. Comunidades deportivas: desde cada estudiante que no se haya visitado se hace un recorrido por
    // anchura con la cola, pasando de estudiante a deporte y de deporte a sus practicantes. Cada recorrido
    // encuentra un grupo, y un grupo de dos o más estudiantes es una comunidad; quien no comparte deportes con
    // nadie no forma comunidad. Cada comunidad guarda a sus estudiantes en un AVL por ID, así que salen
    // ordenados, y por eso el costo es O(n log n + m) y no O(n + m)
    public DLL<Comunidad> comunidades() { // O(n log n + m)
        DLL<Comunidad> listaComunidades = new DLL<>();
        int idComunidad = 1000;
        numeroRecorrido++;
        for (Estudiante estudiante : estudiantes.valoresEnOrden()) {
            if (estudiante.marcaVisita == numeroRecorrido) {
                continue;
            }
            Comunidad comunidad = new Comunidad(idComunidad);
            Queue<Estudiante> cola = new Queue<>();
            visitar(estudiante, null, cola);
            while (!cola.isEmpty()) {
                Estudiante actual = cola.dequeue();
                comunidad.getEstudiantes().insertar(actual.getId(), actual);
                encolarVecinos(actual, cola, false);
            }
            if (comunidad.getEstudiantes().size() > 1) {
                listaComunidades.pushBack(comunidad);
                idComunidad++;
            }
        }
        return listaComunidades;
    }

    // RF5 y RF6. Conexión con alguien que practica uno de los deportes de interés del estudiante: recorrido por
    // anchura desde él. La cola descubre a los estudiantes por niveles (primero los que comparten un deporte con
    // él, luego los que están a un intermediario...), así que el primero descubierto que practica un deporte de
    // interés es el de menos intermediarios. Cada estudiante se revisa en cuanto se descubre y no cuando sale de
    // la cola: el camino es el mismo, pero el recorrido se detiene antes. Retorna el camino (del estudiante al
    // contacto) o una lista vacía si la conexión no existe. Lanza una excepción si el ID no está registrado
    public DLL<Estudiante> buscarConexion(int id) { // O(i + n + m)
        Estudiante origen = buscarObligatorio(id);
        DLL<Estudiante> camino = new DLL<>();
        if (origen.getIntereses().isEmpty()) {
            return camino;
        }
        numeroRecorrido++;
        // se marcan los deportes de interés para revisar a cada estudiante en O(p) y no en O(p · i)
        for (Deporte deporte : origen.getIntereses()) {
            deporte.marcaInteres = numeroRecorrido;
        }
        Queue<Estudiante> cola = new Queue<>();
        visitar(origen, null, cola);
        while (!cola.isEmpty()) {
            Estudiante actual = cola.dequeue();
            Estudiante contacto = encolarVecinos(actual, cola, true);
            if (contacto != null) {
                // se reconstruye el camino hacia atrás con la referencia que dejó el recorrido
                for (Estudiante e = contacto; e != null; e = e.anterior) {
                    camino.pushFront(e);
                }
                return camino;
            }
        }
        return camino;
    }

    // deporte de interés que practica el último estudiante de un camino encontrado por buscarConexion
    public Deporte deporteDeInteresDe(int idOrigen, Estudiante contacto) { // O(i · p)
        Estudiante origen = buscarObligatorio(idOrigen);
        for (Deporte interes : origen.getIntereses()) {
            if (contienePractica(contacto, interes)) {
                return interes;
            }
        }
        return null;
    }

    // un deporte que practican los dos estudiantes (para mostrar por qué están unidos en un camino)
    public static Deporte deporteEnComun(Estudiante a, Estudiante b) { // O(p²)
        for (Estudiante.Practica practica : a.getPractica()) {
            if (contienePractica(b, practica.getDeporte())) {
                return practica.getDeporte();
            }
        }
        return null;
    }

    // RF7. Deportes de más a menos practicantes (con empate, por nombre): recorrido inorden del ranking
    public DLL<Deporte> deportesPorPracticantes() { // O(d)
        return ranking.valoresEnOrden();
    }

    // copia de la lista de quienes practican un deporte; null si el deporte no existe
    public DLL<Estudiante> practicantesDe(String nombreDeporte) { // O(log d + k), con k los practicantes
        if (nombreDeporte == null || nombreDeporte.isBlank()) {
            return null;
        }
        Deporte deporte = deportes.buscar(NormalizadorNombre.normalizar(nombreDeporte));
        if (deporte == null) {
            return null;
        }
        DLL<Estudiante> copia = new DLL<>();
        for (Estudiante estudiante : deporte.getListaInscritos()) {
            copia.pushBack(estudiante);
        }
        return copia;
    }

    public DLL<Estudiante> estudiantesPorId() { // O(n)
        return estudiantes.valoresEnOrden();
    }

    public int cantidadEstudiantes() { // O(1)
        return estudiantes.size();
    }

    public int cantidadDeportes() { // O(1)
        return deportes.size();
    }

    // dibujo del AVL de estudiantes, para ver su forma con pocos datos
    public String dibujarArbolEstudiantes() { // O(n)
        return estudiantes.dibujar();
    }

    public int alturaArbolEstudiantes() { // O(1)
        return estudiantes.altura();
    }

    // marca al estudiante como visitado en el recorrido actual, guarda desde quién se llegó y lo encola
    private void visitar(Estudiante estudiante, Estudiante desde, Queue<Estudiante> cola) { // O(1) amortizado
        estudiante.marcaVisita = numeroRecorrido;
        estudiante.anterior = desde;
        cola.enqueue(estudiante);
    }

    // Encola a los estudiantes que comparten un deporte con actual y que no se han visitado. Cada deporte se
    // expande una sola vez por recorrido, así que en total cada pareja (estudiante, deporte) se revisa un
    // número constante de veces y el recorrido completo cuesta O(n + m). Si buscarContacto es true, retorna el
    // primer vecino nuevo que practica un deporte de interés (o null si no hay)
    private Estudiante encolarVecinos(Estudiante actual, Queue<Estudiante> cola, boolean buscarContacto) {
        for (Estudiante.Practica practica : actual.getPractica()) {
            Deporte deporte = practica.getDeporte();
            if (deporte.marcaVisita == numeroRecorrido) {
                continue;
            }
            deporte.marcaVisita = numeroRecorrido;
            for (Estudiante vecino : deporte.getListaInscritos()) {
                if (vecino.marcaVisita != numeroRecorrido) {
                    visitar(vecino, actual, cola);
                    if (buscarContacto && deporteDeInteresQuePractica(vecino) != null) {
                        return vecino;
                    }
                }
            }
        }
        return null;
    }

    private Deporte deporteDeInteresQuePractica(Estudiante estudiante) { // O(p)
        for (Estudiante.Practica practica : estudiante.getPractica()) {
            if (practica.getDeporte().marcaInteres == numeroRecorrido) {
                return practica.getDeporte();
            }
        }
        return null;
    }

    private static boolean contienePractica(Estudiante estudiante, Deporte deporte) { // O(p)
        DLLNode<Estudiante.Practica> actual = estudiante.getPractica().getHead();
        while (actual != null) {
            if (actual.getData().getDeporte() == deporte) {
                return true;
            }
            actual = actual.getNext();
        }
        return false;
    }

    private static boolean contieneInteres(Estudiante estudiante, Deporte deporte) { // O(i)
        DLLNode<Deporte> actual = estudiante.getIntereses().getHead();
        while (actual != null) {
            if (actual.getData() == deporte) {
                return true;
            }
            actual = actual.getNext();
        }
        return false;
    }

    // Los deportes que llegan desde el registro se crean como recreativos y sin límite de cupos, porque el
    // problema no da su tipo, horario ni entrenador
    private Deporte obtenerOCrearDeporte(String nombre) { // O(log d)
        String clave = NormalizadorNombre.normalizar(nombre);
        Deporte deporte = deportes.buscar(clave);
        if (deporte != null) {
            return deporte;
        }
        deporte = new Recreativo(deportes.size() + 1, nombre.trim(), "", SIN_LIMITE_DE_CUPOS, "", 0.0, true);
        deportes.insertar(clave, deporte);
        ranking.insertar(new ClaveRanking(0, deporte.getNombreCurso()), deporte);
        return deporte;
    }

    // la clave del ranking depende de la cantidad: se saca con la cantidad vieja y se inserta con la nueva
    private void actualizarRanking(Deporte deporte, int cantidadAnterior) { // O(log d)
        ranking.eliminar(new ClaveRanking(cantidadAnterior, deporte.getNombreCurso()));
        ranking.insertar(new ClaveRanking(deporte.getCantidadPracticantes(), deporte.getNombreCurso()), deporte);
    }

    private Estudiante buscarObligatorio(int id) { // O(log n)
        Estudiante estudiante = estudiantes.buscar(id);
        if (estudiante == null) {
            throw new IllegalArgumentException("No existe un estudiante con el ID " + id);
        }
        return estudiante;
    }
}
