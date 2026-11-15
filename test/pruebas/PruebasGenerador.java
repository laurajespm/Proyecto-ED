package pruebas;

import analisis.GeneradorDatos;

import java.util.Arrays;

// Revisa que el generador de datos del Analizador sea reproducible: con la misma semilla tiene que dar
// los mismos estudiantes, porque si no las mediciones de dos corridas no se podrían comparar.
// Uso: java -cp out pruebas.PruebasGenerador
public class PruebasGenerador {

    private static int pasaron = 0;
    private static int total = 0;

    public static void main(String[] args) {
        System.out.println("== GeneradorDatos ==");
        mismaSemilla();
        otraSemilla();
        formaDeLosDatos();
        idsEnOrden();

        System.out.println();
        System.out.println(pasaron + " de " + total + " pruebas pasaron");
        if (pasaron != total) {
            System.exit(1);
        }
    }

    static void mismaSemilla() {
        GeneradorDatos.DatosPrueba a = new GeneradorDatos(2026, 40, 3, 2).generar(500, false);
        GeneradorDatos.DatosPrueba b = new GeneradorDatos(2026, 40, 3, 2).generar(500, false);
        verificar("misma semilla: mismos IDs en el mismo orden", Arrays.equals(a.ids, b.ids));
        verificar("misma semilla: mismos nombres", Arrays.equals(a.nombres, b.nombres));
        verificar("misma semilla: mismos deportes practicados", Arrays.deepEquals(a.practica, b.practica));
        verificar("misma semilla: mismos deportes de interés", Arrays.deepEquals(a.intereses, b.intereses));
    }

    static void otraSemilla() {
        GeneradorDatos.DatosPrueba a = new GeneradorDatos(2026, 40, 3, 2).generar(500, false);
        GeneradorDatos.DatosPrueba b = new GeneradorDatos(2027, 40, 3, 2).generar(500, false);
        verificar("otra semilla: cambia el orden de los IDs o los deportes",
                !Arrays.equals(a.ids, b.ids) || !Arrays.deepEquals(a.practica, b.practica));
    }

    static void formaDeLosDatos() {
        int n = 1000;
        GeneradorDatos.DatosPrueba d = new GeneradorDatos(7, 40, 3, 2).generar(n, false);

        boolean[] visto = new boolean[n + 1];
        boolean idsBien = d.ids.length == n;
        for (int id : d.ids) {
            if (id < 1 || id > n || visto[id]) {
                idsBien = false;
            } else {
                visto[id] = true;
            }
        }
        verificar("los IDs son 1..n y no se repiten", idsBien);

        boolean cantidadesBien = true;
        boolean sinRepetir = true;
        for (int i = 0; i < n; i++) {
            int p = d.practica[i].length;
            int q = d.intereses[i].length;
            if (p < 1 || p > 3 || q < 1 || q > 2) {
                cantidadesBien = false;
            }
            // un deporte no puede estar dos veces ni en practica y en intereses a la vez
            String[] todos = new String[p + q];
            System.arraycopy(d.practica[i], 0, todos, 0, p);
            System.arraycopy(d.intereses[i], 0, todos, p, q);
            for (int x = 0; x < todos.length; x++) {
                for (int y = x + 1; y < todos.length; y++) {
                    if (todos[x].equals(todos[y])) {
                        sinRepetir = false;
                    }
                }
            }
        }
        verificar("cada estudiante practica de 1 a 3 deportes y le interesan de 1 a 2", cantidadesBien);
        verificar("ningún deporte se repite dentro de un mismo estudiante", sinRepetir);
    }

    static void idsEnOrden() {
        GeneradorDatos.DatosPrueba d = new GeneradorDatos(7, 40, 3, 2).generar(100, true);
        boolean creciente = true;
        for (int i = 0; i < d.ids.length; i++) {
            if (d.ids[i] != i + 1) {
                creciente = false;
            }
        }
        verificar("con idsEnOrden los IDs salen 1, 2, 3...", creciente);
        verificar("pocos deportes lanza excepción", lanza(() -> new GeneradorDatos(1, 4, 3, 2)));
    }

    static boolean lanza(Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (RuntimeException e) {
            return true;
        }
    }

    static void verificar(String descripcion, boolean ok) {
        total++;
        if (ok) {
            pasaron++;
        }
        System.out.println((ok ? "PASÓ  " : "FALLÓ ") + descripcion);
    }
}
