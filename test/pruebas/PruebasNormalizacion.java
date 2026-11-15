package pruebas;

import deportes.ClaveRanking;
import deportes.NormalizadorNombre;

// Pruebas de NormalizadorNombre y ClaveRanking: que un mismo deporte escrito de formas distintas se reconozca
// como uno solo y que el ranking quede de más a menos practicantes, con empate alfabético.
// Uso: java -cp out pruebas.PruebasNormalizacion
public class PruebasNormalizacion {

    private static int pasaron = 0;
    private static int total = 0;

    public static void main(String[] args) {
        probarNormalizacion();
        probarRanking();

        System.out.println();
        System.out.println(pasaron + " de " + total + " pruebas pasaron");
        if (pasaron != total) {
            System.exit(1);
        }
    }

    static void probarNormalizacion() {
        System.out.println("== NormalizadorNombre ==");
        verificar("las tildes se quitan", NormalizadorNombre.normalizar("Natación").equals("natacion"));
        verificar("las mayúsculas pasan a minúsculas", NormalizadorNombre.normalizar("FÚTBOL").equals("futbol"));
        verificar("los espacios al inicio y al final se quitan",
                NormalizadorNombre.normalizar("   Rugby  ").equals("rugby"));
        verificar("los espacios repetidos en medio quedan como uno solo",
                NormalizadorNombre.normalizar("Tenis   de    mesa").equals("tenis de mesa"));
        verificar("tres formas de escribir fútbol dan lo mismo",
                NormalizadorNombre.normalizar("Fútbol").equals(NormalizadorNombre.normalizar(" futbol "))
                        && NormalizadorNombre.normalizar("futbol").equals(NormalizadorNombre.normalizar("FUTBOL")));
        verificar("la ñ se trata como n, así que Montañismo y Montanismo son el mismo deporte",
                NormalizadorNombre.normalizar("Montañismo").equals(NormalizadorNombre.normalizar("Montanismo")));
        verificar("un nombre vacío lanza excepción", lanza(() -> NormalizadorNombre.normalizar("   ")));
        verificar("un nombre null lanza excepción", lanza(() -> NormalizadorNombre.normalizar(null)));
    }

    static void probarRanking() {
        System.out.println();
        System.out.println("== ClaveRanking ==");
        ClaveRanking futbolCinco = new ClaveRanking(5, "Fútbol");
        ClaveRanking natacionTres = new ClaveRanking(3, "Natación");
        ClaveRanking ajedrezCinco = new ClaveRanking(5, "Ajedrez");
        verificar("el deporte con más practicantes va primero", futbolCinco.compareTo(natacionTres) < 0
                && natacionTres.compareTo(futbolCinco) > 0);
        verificar("con empate en practicantes va primero el nombre alfabéticamente menor",
                ajedrezCinco.compareTo(futbolCinco) < 0);
        verificar("el desempate ignora tildes y mayúsculas",
                new ClaveRanking(2, "Ájedrez").compareTo(new ClaveRanking(2, "baloncesto")) < 0);
        verificar("la misma cantidad y el mismo nombre (escrito distinto) son la misma clave",
                new ClaveRanking(4, "Natación").compareTo(new ClaveRanking(4, "NATACION")) == 0);
        verificar("un deporte sin practicantes va después de uno con practicantes",
                new ClaveRanking(0, "Ajedrez").compareTo(new ClaveRanking(1, "Voleibol")) > 0);
    }

    static boolean lanza(Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (IllegalArgumentException e) {
            return true;
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
