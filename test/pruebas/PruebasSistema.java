package pruebas;

import deportes.Comunidad;
import deportes.Deporte;
import deportes.Estudiante;
import deportes.SistemaDeportes;
import estructuras.ArbolAVL;
import estructuras.DLL;
import estructuras.DLLNode;
import estructuras.Queue;

// Casos borde de las estructuras y de cada requisito del sistema. Imprime si cada caso pasó o falló.
// Uso: java -cp out pruebas.PruebasSistema
public class PruebasSistema {

    private static int pasaron = 0;
    private static int total = 0;

    public static void main(String[] args) {
        ejecutar("DLL", PruebasSistema::probarDLL);
        ejecutar("Queue", PruebasSistema::probarQueue);
        ejecutar("ArbolAVL", PruebasSistema::probarAVL);
        ejecutar("SistemaDeportes: registro, consulta y eliminación", PruebasSistema::probarRegistro);
        ejecutar("SistemaDeportes: comunidades y conexiones", PruebasSistema::probarConexiones);
        ejecutar("SistemaDeportes: deportes ordenados por practicantes", PruebasSistema::probarRanking);

        System.out.println();
        System.out.println(pasaron + " de " + total + " pruebas pasaron");
        if (pasaron != total) {
            System.exit(1);
        }
    }

    // si algo lanza una excepción que no se esperaba, cuenta como falla y se sigue con el siguiente grupo
    static void ejecutar(String nombre, Runnable casos) {
        System.out.println();
        System.out.println("== " + nombre + " ==");
        try {
            casos.run();
        } catch (RuntimeException e) {
            verificar("excepción inesperada: " + e, false);
        }
    }

    static void probarDLL() {
        DLL<Integer> vacia = new DLL<>();
        verificar("lista nueva vacía", vacia.isEmpty() && vacia.size() == 0 && vacia.toString().equals("[]"));
        verificar("popFront en vacía lanza excepción", lanza(vacia::popFront));
        verificar("popBack en vacía lanza excepción", lanza(vacia::popBack));
        verificar("find en vacía retorna null", vacia.find(5) == null);

        DLL<Integer> lista = new DLL<>();
        DLLNode<Integer> dos = lista.pushBack(2);
        lista.pushBack(3);
        DLLNode<Integer> uno = lista.pushFront(1);
        verificar("pushBack y pushFront: [1, 2, 3]", lista.toString().equals("[1, 2, 3]") && lista.size() == 3);
        lista.deleteNode(dos);
        verificar("deleteNode del medio con la referencia: [1, 3]", lista.toString().equals("[1, 3]") && lista.size() == 2);
        lista.deleteNode(uno);
        verificar("deleteNode del head: [3]", lista.toString().equals("[3]") && lista.topFront() == 3);
        lista.deleteNode(lista.getTail());
        verificar("deleteNode del único nodo deja head y tail en null",
                lista.isEmpty() && lista.getHead() == null && lista.getTail() == null);
        lista.pushBack(7);
        verificar("la lista sigue sirviendo después de vaciarla", lista.toString().equals("[7]"));

        DLL<Integer> otra = new DLL<>();
        DLLNode<Integer> nodo = otra.pushBack(10);
        otra.addNodeAfter(nodo, 20);
        otra.addNodeBefore(nodo, 5);
        verificar("addNodeAfter sobre tail y addNodeBefore sobre head: [5, 10, 20]",
                otra.toString().equals("[5, 10, 20]") && otra.topFront() == 5 && otra.topBack() == 20);
        verificar("erase(2) borra el de la posición 2", otra.erase(2) == 10 && otra.toString().equals("[5, 20]"));
        verificar("find con un valor mayor que 127 usa equals", otra.find(Integer.valueOf(1000)) == null
                && find1000());
        verificar("popBack y popFront retornan el dato", otra.popBack() == 20 && otra.popFront() == 5 && otra.isEmpty());
    }

    // 1000 no está en el caché de Integer: si find comparara con == no lo encontraría
    static boolean find1000() {
        DLL<Integer> lista = new DLL<>();
        lista.pushBack(1000);
        return lista.find(1000) != null;
    }

    static void probarQueue() {
        Queue<Integer> vacia = new Queue<>();
        verificar("dequeue en cola vacía lanza excepción", lanza(vacia::dequeue));
        verificar("front en cola vacía lanza excepción", lanza(vacia::front));

        Queue<Integer> cola = new Queue<>(4);
        for (int i = 1; i <= 4; i++) {
            cola.enqueue(i);
        }
        cola.dequeue();
        cola.dequeue();
        cola.enqueue(5);
        cola.enqueue(6);
        verificar("encolar, desencolar y encolar da la vuelta sin crecer", cola.size() == 4 && cola.capacidad() == 4
                && cola.front() == 3);
        cola.enqueue(7);
        boolean enOrden = cola.capacidad() == 8;
        for (int esperado = 3; esperado <= 7; esperado++) {
            int sacado = cola.dequeue();
            enOrden = enOrden && sacado == esperado;
        }
        verificar("crece con el frente en una casilla distinta de 0 y conserva el orden", enOrden && cola.isEmpty());

        Queue<Integer> borrar = new Queue<>();
        for (int i = 1; i <= 5; i++) {
            borrar.enqueue(i);
        }
        boolean borrados = borrar.delete(1) && borrar.delete(3) && borrar.delete(5) && !borrar.delete(9);
        verificar("delete del frente, del medio, del final y de un valor que no está",
                borrados && borrar.size() == 2 && borrar.dequeue() == 2 && borrar.dequeue() == 4);
    }

    static void probarAVL() {
        ArbolAVL<Integer, String> arbol = new ArbolAVL<>();
        verificar("árbol nuevo vacío", arbol.isEmpty() && arbol.altura() == 0 && arbol.buscar(1) == null);
        int n = 1000;
        for (int i = 1; i <= n; i++) {
            arbol.insertar(i, "v" + i); // claves en orden: un árbol sin balancear quedaría como una lista
        }
        // 2^9 < 1000 < 2^10: ningún árbol binario con 1000 nodos tiene menos de 10 niveles
        verificar("1000 claves insertadas en orden: altura " + arbol.altura() + " (la mínima posible es 10)",
                arbol.size() == n && arbol.altura() == 10);
        ArbolAVL<Integer, Integer> grande = new ArbolAVL<>();
        for (int i = 1; i <= 320000; i++) {
            grande.insertar(i, i);
        }
        // 2^18 < 320 000 < 2^19, así que ningún árbol binario con 320 000 nodos tiene menos de 19 niveles
        verificar("320 000 claves insertadas en orden: altura " + grande.altura() + " (la mínima posible es 19)",
                grande.size() == 320000 && grande.altura() == 19);
        verificar("buscar una clave existente y una inexistente", "v500".equals(arbol.buscar(500)) && arbol.buscar(5000) == null);
        arbol.insertar(500, "nuevo");
        verificar("insertar una clave existente reemplaza el valor", "nuevo".equals(arbol.buscar(500)) && arbol.size() == n);
        verificar("eliminar una clave inexistente retorna null", arbol.eliminar(5000) == null && arbol.size() == n);

        ArbolAVL<Integer, Integer> pequeno = new ArbolAVL<>();
        for (int clave : new int[] { 50, 30, 70, 20, 40, 60, 80, 35 }) {
            pequeno.insertar(clave, clave);
        }
        boolean bien = pequeno.eliminar(20) == 20 // hoja
                && pequeno.eliminar(40) == 40 // un hijo
                && pequeno.eliminar(50) == 50; // dos hijos (la raíz)
        verificar("eliminar una hoja, un nodo con un hijo y la raíz con dos hijos",
                bien && pequeno.valoresEnOrden().toString().equals("[30, 35, 60, 70, 80]"));

        // secuencia larga de inserciones y eliminaciones al azar comparada con un arreglo de presencia; después de
        // cada operación la altura debe seguir por debajo de la cota del AVL, 1,44 log2(n + 2)
        ArbolAVL<Integer, Integer> azar = new ArbolAVL<>();
        boolean[] presente = new boolean[500];
        int cantidad = 0;
        long semilla = 2026;
        boolean coincide = true;
        boolean balanceado = true;
        for (int paso = 0; paso < 20000; paso++) {
            semilla = (semilla * 1103515245 + 12345) & 0x7fffffff; // generador lineal congruencial
            int clave = (int) (semilla % 500);
            if (paso % 3 == 0) {
                Integer quitado = azar.eliminar(clave);
                coincide = coincide && ((quitado != null) == presente[clave]);
                if (presente[clave]) {
                    cantidad--;
                }
                presente[clave] = false;
            } else {
                if (!presente[clave]) {
                    cantidad++;
                }
                azar.insertar(clave, clave);
                presente[clave] = true;
            }
            balanceado = balanceado && azar.altura() <= 1.44 * Math.log(azar.size() + 2) / Math.log(2);
        }
        verificar("20 000 operaciones al azar: tamaño y contenido coinciden con el modelo",
                coincide && azar.size() == cantidad && ordenada(azar.valoresEnOrden(), presente));
        verificar("y después de cada inserción o eliminación la altura respeta la cota del AVL", balanceado);
    }

    static boolean ordenada(DLL<Integer> valores, boolean[] presente) {
        int siguiente = 0;
        for (DLLNode<Integer> n = valores.getHead(); n != null; n = n.getNext()) {
            while (siguiente < presente.length && !presente[siguiente]) {
                siguiente++;
            }
            if (siguiente >= presente.length || n.getData() != siguiente) {
                return false;
            }
            siguiente++;
        }
        while (siguiente < presente.length && !presente[siguiente]) {
            siguiente++;
        }
        return siguiente == presente.length;
    }

    static void probarRegistro() {
        SistemaDeportes sistema = new SistemaDeportes();
        Estudiante ana = sistema.registrarEstudiante(1, "Ana", new String[] { "Fútbol", "futbol", "Rugby" },
                new String[] { "Natación", "rugby", "NATACION" });
        verificar("deporte repetido con otra escritura cuenta una vez", ana.getPractica().size() == 2);
        verificar("un deporte que ya practica no queda como interés, y un interés repetido cuenta una vez",
                ana.getIntereses().size() == 1 && ana.getIntereses().topFront().getNombreCurso().equals("Natación"));
        verificar("ID duplicado lanza excepción", lanza(() -> sistema.registrarEstudiante(1, "Otra", new String[] {},
                new String[] {})));
        verificar("nombre vacío lanza excepción", lanza(() -> sistema.registrarEstudiante(2, " ", new String[] {},
                new String[] {})));
        sistema.registrarEstudiante(2, "Luis", new String[] { " FÚTBOL " }, new String[] {});
        verificar("consulta por ID", sistema.buscarEstudiante(2).getNombre().equals("Luis")
                && sistema.buscarEstudiante(99) == null);
        verificar("practicantes de fútbol: Ana y Luis", nombres(sistema.practicantesDe("Futbol")).equals("Ana, Luis"));
        verificar("eliminar un estudiante existente", sistema.eliminarEstudiante(1) && sistema.buscarEstudiante(1) == null);
        verificar("al eliminar, sale de la lista de cada deporte", nombres(sistema.practicantesDe("fútbol")).equals("Luis")
                && sistema.practicantesDe("rugby").isEmpty());
        verificar("eliminar un ID que no existe retorna false", !sistema.eliminarEstudiante(1));
        verificar("buscar conexión de un ID que no existe lanza excepción", lanza(() -> sistema.buscarConexion(1)));
    }

    // el mismo ejemplo de datos/estudiantes.txt, en pequeño
    static SistemaDeportes ejemplo() {
        SistemaDeportes s = new SistemaDeportes();
        s.registrarEstudiante(1, "Ana", new String[] { "Fútbol", "Atletismo" }, new String[] { "Natación" });
        s.registrarEstudiante(2, "Luis", new String[] { "Fútbol", "Rugby" }, new String[] { "Voleibol" });
        s.registrarEstudiante(3, "Marta", new String[] { "Rugby", "Natación" }, new String[] {});
        s.registrarEstudiante(4, "Carlos", new String[] { "Natación", "Baloncesto" }, new String[] { "Taekwondo" });
        s.registrarEstudiante(5, "Sofía", new String[] { "Baloncesto", "Voleibol" }, new String[] {});
        s.registrarEstudiante(6, "Valentina", new String[] { "Atletismo" }, new String[] { "Fútbol" });
        s.registrarEstudiante(7, "Julián", new String[] { "Taekwondo" }, new String[] {});
        s.registrarEstudiante(8, "Mateo", new String[] { "Taekwondo" }, new String[] { "Rugby" });
        s.registrarEstudiante(9, "Felipe", new String[] {}, new String[] { "Baloncesto" });
        return s;
    }

    static void probarConexiones() {
        SistemaDeportes s = ejemplo();
        DLL<Comunidad> comunidades = s.comunidades();
        verificar("dos comunidades: 6 y 2 estudiantes (Felipe no comparte deportes)", comunidades.size() == 2
                && comunidades.getHead().getData().getEstudiantes().size() == 6
                && comunidades.getTail().getData().getEstudiantes().size() == 2);
        verificar("la segunda comunidad es la de Julián y Mateo",
                nombres(comunidades.getTail().getData().getEstudiantes().valoresEnOrden()).equals("Julián, Mateo"));

        DLL<Estudiante> directa = s.buscarConexion(6);
        verificar("conexión directa: Valentina comparte atletismo con Ana, que practica fútbol",
                directa.size() == 2 && directa.getTail().getData().getNombre().equals("Ana")
                        && s.deporteDeInteresDe(6, directa.getTail().getData()).getNombreCurso().equals("Fútbol"));
        DLL<Estudiante> indirecta = s.buscarConexion(1);
        verificar("conexión indirecta: Ana llega a Marta (natación) a través de Luis",
                indirecta.size() == 3 && nombres(indirecta).equals("Ana, Luis, Marta"));
        DLL<Estudiante> larga = s.buscarConexion(2);
        verificar("Luis llega a Sofía (voleibol) con el camino más corto: Luis, Marta, Carlos, Sofía",
                nombres(larga).equals("Luis, Marta, Carlos, Sofía"));
        verificar("sin conexión: el taekwondo solo se practica en otra comunidad", s.buscarConexion(4).isEmpty());
        verificar("sin conexión: Mateo no llega a nadie que practique rugby", s.buscarConexion(8).isEmpty());
        verificar("sin conexión: Felipe no practica ningún deporte", s.buscarConexion(9).isEmpty());
        verificar("sin conexión: Marta no tiene deportes de interés", s.buscarConexion(3).isEmpty());

        s.eliminarEstudiante(2);
        verificar("al eliminar a Luis, Ana ya no llega a quien practica natación", s.buscarConexion(1).isEmpty());
        verificar("y la comunidad grande se parte en dos", s.comunidades().size() == 3);
    }

    static void probarRanking() {
        SistemaDeportes s = ejemplo();
        String esperado = "Atletismo (2), Baloncesto (2), Fútbol (2), Natación (2), Rugby (2), Taekwondo (2), "
                + "Voleibol (1)";
        verificar("de más a menos practicantes y, con empate, alfabético", ranking(s).equals(esperado));
        s.registrarEstudiante(10, "Paula", new String[] { "Voleibol" }, new String[] {});
        s.registrarEstudiante(11, "Diego", new String[] { "Voleibol" }, new String[] {});
        verificar("al registrar, el deporte sube en el orden", s.deportesPorPracticantes().getHead().getData()
                .getNombreCurso().equals("Voleibol"));
        s.eliminarEstudiante(10);
        s.eliminarEstudiante(11);
        s.eliminarEstudiante(5);
        Deporte ultimo = s.deportesPorPracticantes().getTail().getData();
        verificar("al eliminar, baja: voleibol queda de último con 0", ultimo.getNombreCurso().equals("Voleibol")
                && ultimo.getCantidadPracticantes() == 0);
    }

    static String ranking(SistemaDeportes s) {
        StringBuilder texto = new StringBuilder();
        for (DLLNode<Deporte> n = s.deportesPorPracticantes().getHead(); n != null; n = n.getNext()) {
            texto.append(n.getData().getNombreCurso()).append(" (").append(n.getData().getCantidadPracticantes())
                    .append(")");
            if (n.getNext() != null) {
                texto.append(", ");
            }
        }
        return texto.toString();
    }

    static String nombres(DLL<Estudiante> estudiantes) {
        StringBuilder texto = new StringBuilder();
        for (DLLNode<Estudiante> n = estudiantes.getHead(); n != null; n = n.getNext()) {
            texto.append(n.getData().getNombre());
            if (n.getNext() != null) {
                texto.append(", ");
            }
        }
        return texto.toString();
    }

    static boolean lanza(Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage() != null && !e.getMessage().isEmpty();
        }
    }

    static void verificar(String caso, boolean condicion) {
        total++;
        if (condicion) {
            pasaron++;
            System.out.println("  PASÓ   " + caso);
        } else {
            System.out.println("  FALLÓ  " + caso);
        }
    }
}
