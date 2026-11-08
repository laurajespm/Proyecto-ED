package analisis;

import java.util.Random;

public class GeneradorDatos {

    public static final String DEPORTE_FANTASMA = "Deporte fantasma";

    private static final String[] NOMBRES_BASE = {
        "Voleibol", "Rugby", "Taekwondo", "Natación", "Baloncesto", "Fútbol", "Tenis de mesa", "Ajedrez",
        "Atletismo", "Béisbol", "Softbol", "Judo", "Karate", "Esgrima", "Gimnasia", "Ciclismo", "Patinaje",
        "Escalada", "Yoga", "Ultimate", "Balonmano", "Hockey", "Squash", "Tenis", "Bádminton", "Boxeo",
        "Lucha", "Pesas", "Triatlón", "Montañismo"
    };

    public static class DatosPrueba {
        public final int[] ids;
        public final String[] nombres;
        public final String[][] practica;
        public final String[][] intereses;

        DatosPrueba(int[] ids, String[] nombres, String[][] practica, String[][] intereses) {
            this.ids = ids;
            this.nombres = nombres;
            this.practica = practica;
            this.intereses = intereses;
        }
    }

    private final Random azar;
    private final int maxPractica;
    private final int maxIntereses;
    private final String[] catalogo;
    private final int[] sorteo;

    public GeneradorDatos(long semilla, int numDeportes, int maxPractica, int maxIntereses) {
        if (maxPractica < 1 || maxIntereses < 1) {
            throw new IllegalArgumentException("Cada estudiante necesita al menos un deporte practicado y uno de interés");
        }
        if (numDeportes < maxPractica + maxIntereses) {
            throw new IllegalArgumentException("Con " + numDeportes + " deportes no alcanza para sortear "
                    + maxPractica + " practicados y " + maxIntereses + " de interés sin repetir");
        }
        this.azar = new Random(semilla);
        this.maxPractica = maxPractica;
        this.maxIntereses = maxIntereses;
        this.catalogo = new String[numDeportes];
        this.sorteo = new int[numDeportes];
        for (int i = 0; i < numDeportes; i++) {
            catalogo[i] = i < NOMBRES_BASE.length ? NOMBRES_BASE[i] : "Deporte " + (i + 1);
            sorteo[i] = i;
        }
    }

    public int cantidadDeportes() {
        return catalogo.length;
    }

    public String deporte(int posicion) {
        return catalogo[posicion];
    }

    public DatosPrueba generar(int n, boolean idsEnOrden) {
        if (n < 1) {
            throw new IllegalArgumentException("Hace falta al menos un estudiante");
        }
        int[] ids = new int[n];
        for (int i = 0; i < n; i++) {
            ids[i] = i + 1;
        }
        if (!idsEnOrden) {
            mezclar(ids, azar);
        }
        String[] nombres = new String[n];
        String[][] practica = new String[n][];
        String[][] intereses = new String[n][];
        for (int i = 0; i < n; i++) {
            nombres[i] = "Estudiante " + ids[i];
            int cuantosPractica = 1 + azar.nextInt(maxPractica);
            int cuantosIntereses = 1 + azar.nextInt(maxIntereses);
            sortear(cuantosPractica + cuantosIntereses);
            practica[i] = new String[cuantosPractica];
            for (int j = 0; j < cuantosPractica; j++) {
                practica[i][j] = catalogo[sorteo[j]];
            }
            intereses[i] = new String[cuantosIntereses];
            for (int j = 0; j < cuantosIntereses; j++) {
                intereses[i][j] = catalogo[sorteo[cuantosPractica + j]];
            }
        }
        return new DatosPrueba(ids, nombres, practica, intereses);
    }

    private void sortear(int k) {
        for (int j = 0; j < k; j++) {
            int r = j + azar.nextInt(sorteo.length - j);
            int temporal = sorteo[j];
            sorteo[j] = sorteo[r];
            sorteo[r] = temporal;
        }
    }

    public int[] permutacion(int n) {
        return permutacion(n, azar);
    }

    public int[] posicionesAlAzar(int cuantas, int n) {
        return posicionesAlAzar(cuantas, n, azar);
    }

    public static int[] permutacion(int n, Random azar) {
        int[] p = new int[n];
        for (int i = 0; i < n; i++) {
            p[i] = i;
        }
        mezclar(p, azar);
        return p;
    }

    public static int[] posicionesAlAzar(int cuantas, int n, Random azar) {
        int[] p = new int[cuantas];
        for (int i = 0; i < cuantas; i++) {
            p[i] = azar.nextInt(n);
        }
        return p;
    }

    private static void mezclar(int[] a, Random azar) {
        for (int i = a.length - 1; i > 0; i--) {
            int j = azar.nextInt(i + 1);
            int temporal = a[i];
            a[i] = a[j];
            a[j] = temporal;
        }
    }
}
