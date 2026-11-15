package pruebas;

import deportes.Comunidad;
import deportes.Estudiante;
import deportes.SistemaDeportes;
import estructuras.DLL;
import estructuras.DLLNode;

// Pruebas de las comunidades (RF4) usando solo los métodos públicos de SistemaDeportes: quién forma comunidad,
// cómo se unen dos grupos cuando llega alguien que comparte deporte con ambos y cómo se separan al eliminarlo.
// Uso: java -cp out pruebas.PruebasComunidades
public class PruebasComunidades {

    private static int pasaron = 0;
    private static int total = 0;

    public static void main(String[] args) {
        probarCasosBasicos();
        probarUnionYSeparacion();
        probarOrdenDeMiembros();

        System.out.println();
        System.out.println(pasaron + " de " + total + " pruebas pasaron");
        if (pasaron != total) {
            System.exit(1);
        }
    }

    static void probarCasosBasicos() {
        System.out.println("== Casos básicos ==");
        SistemaDeportes s = new SistemaDeportes();
        verificar("un sistema vacío no tiene comunidades", s.comunidades().isEmpty());

        s.registrarEstudiante(1, "Ana", new String[] { "Fútbol" }, new String[] {});
        verificar("un estudiante solo no forma comunidad", s.comunidades().isEmpty());

        s.registrarEstudiante(2, "Felipe", new String[] {}, new String[] { "Fútbol" });
        verificar("quien no practica deportes no forma comunidad aunque le interese uno", s.comunidades().isEmpty());

        s.registrarEstudiante(3, "Luis", new String[] { "futbol" }, new String[] {});
        DLL<Comunidad> comunidades = s.comunidades();
        verificar("dos estudiantes que comparten deporte (escrito distinto) forman una comunidad",
                comunidades.size() == 1 && nombres(comunidades.getHead().getData()).equals("Ana, Luis"));
    }

    static void probarUnionYSeparacion() {
        System.out.println();
        System.out.println("== Unión y separación de grupos ==");
        SistemaDeportes s = new SistemaDeportes();
        s.registrarEstudiante(1, "Ana", new String[] { "Fútbol" }, new String[] {});
        s.registrarEstudiante(2, "Luis", new String[] { "Fútbol" }, new String[] {});
        s.registrarEstudiante(3, "Marta", new String[] { "Natación" }, new String[] {});
        s.registrarEstudiante(4, "Carlos", new String[] { "Natación" }, new String[] {});
        verificar("fútbol y natación forman dos comunidades separadas", s.comunidades().size() == 2);

        s.registrarEstudiante(5, "Sofía", new String[] { "Fútbol", "Natación" }, new String[] {});
        DLL<Comunidad> unidas = s.comunidades();
        verificar("al llegar alguien que practica los dos deportes, los grupos se unen en uno de 5",
                unidas.size() == 1 && unidas.getHead().getData().getEstudiantes().size() == 5);

        s.eliminarEstudiante(5);
        DLL<Comunidad> separadas = s.comunidades();
        verificar("al eliminar a ese estudiante, vuelven a ser dos comunidades de 2", separadas.size() == 2
                && separadas.getHead().getData().getEstudiantes().size() == 2
                && separadas.getTail().getData().getEstudiantes().size() == 2);

        s.eliminarEstudiante(2);
        verificar("si un grupo queda con una sola persona, deja de ser comunidad", s.comunidades().size() == 1
                && nombres(s.comunidades().getHead().getData()).equals("Marta, Carlos"));
    }

    static void probarOrdenDeMiembros() {
        System.out.println();
        System.out.println("== Orden de los miembros ==");
        SistemaDeportes s = new SistemaDeportes();
        s.registrarEstudiante(30, "Paula", new String[] { "Ajedrez" }, new String[] {});
        s.registrarEstudiante(10, "Daniel", new String[] { "Ajedrez" }, new String[] {});
        s.registrarEstudiante(20, "Laura", new String[] { "Ajedrez" }, new String[] {});
        DLL<Estudiante> miembros = s.comunidades().getHead().getData().getEstudiantes().valoresEnOrden();
        verificar("los miembros de una comunidad salen ordenados por ID aunque se registren en otro orden",
                miembros.getHead().getData().getId() == 10 && miembros.getTail().getData().getId() == 30
                        && miembros.size() == 3);
    }

    static String nombres(Comunidad comunidad) {
        StringBuilder texto = new StringBuilder();
        DLL<Estudiante> miembros = comunidad.getEstudiantes().valoresEnOrden();
        for (DLLNode<Estudiante> n = miembros.getHead(); n != null; n = n.getNext()) {
            texto.append(n.getData().getNombre());
            if (n.getNext() != null) {
                texto.append(", ");
            }
        }
        return texto.toString();
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
