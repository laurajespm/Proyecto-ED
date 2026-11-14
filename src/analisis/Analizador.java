package analisis;

import deportes.Comunidad;
import deportes.Estudiante;
import deportes.SistemaDeportes;
import estructuras.DLL;
import estructuras.DLLNode;
import estructuras.Queue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

// Mide los tiempos de cada operacion y los guarda en resultados/tiempos.csv
public class Analizador {

    private static final int MAX_PRACTICA = 3;
    private static final int MAX_INTERESES = 2;
    private static final int MIN_DEPORTES = 40;

    private static final int BUSQUEDAS = 100_000;
    private static final int BUSQUEDAS_LISTA = 50;
    private static final int CONEXIONES = 200;

    private static final int OPS_CALENTAMIENTO = 100_000;
    private static final int MAX_VUELTAS_CALENTAMIENTO = 30;
    private static final long NS_CALENTAMIENTO = 2_000_000_000L;

    private static final String[] OPCIONES = {"tamanos", "vueltas", "calentamiento", "semilla", "deportes", "ids", "solo", "salida"};

    private static long sumidero;

    private interface Prueba {
        long ejecutar(int vuelta) throws Exception;
    }

    private final int[] tamanos;
    private final int vueltas;
    private final int calentamiento;
    private final long semilla;
    private final String deportes;
    private final boolean idsEnOrden;
    private final PrintWriter csv;
    private int avisos;
    private int omitidas;
    private boolean silencioso;
    private int avisosPrueba;
    private boolean sinMemoria;

    private Analizador(int[] tamanos, int vueltas, int calentamiento, long semilla, String deportes,
                       boolean idsEnOrden, PrintWriter csv) {
        this.tamanos = tamanos;
        this.vueltas = vueltas;
        this.calentamiento = calentamiento;
        this.semilla = semilla;
        this.deportes = deportes;
        this.idsEnOrden = idsEnOrden;
        this.csv = csv;
    }

    public static void main(String[] args) throws IOException {
        try {
            correr(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void correr(String[] args) throws IOException {
        revisarOpciones(args);
        String[] partes = valor(args, "tamanos", "1000,10000,100000,1000000").split(",");
        int[] tamanos = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            tamanos[i] = (int) numero("tamanos", partes[i].trim());
            if (tamanos[i] < 100) {
                throw new IllegalArgumentException("Los tamanos deben ser de al menos 100 (llego " + tamanos[i] + ")");
            }
        }
        Arrays.sort(tamanos);
        int vueltas = (int) numero("vueltas", valor(args, "vueltas", "5"));
        int calentamiento = (int) numero("calentamiento", valor(args, "calentamiento", "1"));
        long semilla = numero("semilla", valor(args, "semilla", "2026"));
        String deportes = valor(args, "deportes", String.valueOf(MIN_DEPORTES));
        String ids = valor(args, "ids", "aleatorio");
        String solo = valor(args, "solo", "todo");
        String salida = valor(args, "salida", "resultados/tiempos.csv");
        if (vueltas < 1 || calentamiento < 0) {
            throw new IllegalArgumentException("Hace falta al menos una vuelta medida y el calentamiento no puede ser negativo");
        }
        revisarDeportes(deportes);
        if (!ids.equals("aleatorio") && !ids.equals("creciente")) {
            throw new IllegalArgumentException("--ids debe ser aleatorio o creciente");
        }
        if (!solo.equals("todo") && !solo.equals("estructuras") && !solo.equals("sistema")) {
            throw new IllegalArgumentException("--solo debe ser todo, estructuras o sistema");
        }

        File archivo = new File(salida).getAbsoluteFile();
        if (archivo.getParentFile() != null) {
            archivo.getParentFile().mkdirs();
        }
        guardarAnterior(archivo);
        try (PrintWriter csv = new PrintWriter(new FileWriter(archivo))) {
            Analizador analizador = new Analizador(tamanos, vueltas, calentamiento, semilla, deportes,
                    ids.equals("creciente"), csv);
            analizador.escribirEncabezado();
            analizador.ejecutar(solo);
            System.out.println("\nResultados en " + archivo);
            System.out.println("Operaciones omitidas: " + analizador.omitidas + ", avisos: " + analizador.avisos);
            System.out.println("(valor de control, se puede ignorar: " + sumidero + ")");
        }
    }

    private void ejecutar(String solo) {
        silencioso = true;
        System.out.println("Precalentando la JVM...");
        try {
            if (!solo.equals("sistema")) {
                medirEstructuras(5000);
            }
            if (!solo.equals("estructuras")) {
                medirSistema(5000);
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Ni el precalentamiento cabe en la memoria: hay que subir -Xmx");
            return;
        } finally {
            silencioso = false;
        }

        for (int n : tamanos) {
            System.out.println("\n=== n = " + n + " ===");
            long inicio = System.nanoTime();
            try {
                if (!solo.equals("sistema")) {
                    medirEstructuras(n);
                }
                if (!solo.equals("estructuras")) {
                    medirSistema(n);
                }
            } catch (OutOfMemoryError e) {
                System.out.println("Sin memoria con n = " + n + ": se deja de medir (subir -Xmx para llegar a tamanos mayores)");
                omitidas++;
                return;
            }
            System.out.println(String.format(Locale.ROOT, "(n = %d tomo %.1f s)", n, (System.nanoTime() - inicio) / 1e9));
            if (sinMemoria) {
                System.out.println("Se dejo de medir: con tamanos mayores tampoco alcanzaria la memoria (subir -Xmx)");
                return;
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void medirEstructuras(int n) {
        Random azar = new Random(semilla + n);
        Integer[] valores = new Integer[n];
        for (int i = 0; i < n; i++) {
            valores[i] = i;
        }
        int[] orden = GeneradorDatos.permutacion(n, azar);

        medir("DLL", "pushBack", n, "", n, "O(1)", v -> {
            DLL<Integer> lista = new DLL<>();
            long t0 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                lista.pushBack(valores[i]);
            }
            long t1 = System.nanoTime();
            sumidero += lista.size();
            return t1 - t0;
        });

        medir("DLL", "deleteNode", n, "", n, "O(1)", v -> {
            DLL<Integer> lista = new DLL<>();
            DLLNode<Integer>[] nodos = (DLLNode<Integer>[]) new DLLNode[n];
            for (int i = 0; i < n; i++) {
                nodos[i] = lista.pushBack(valores[i]);
            }
            long t0 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                lista.deleteNode(nodos[orden[i]]);
            }
            long t1 = System.nanoTime();
            sumidero += lista.size();
            return t1 - t0;
        });

        DLL<Integer> listaLlena = new DLL<>();
        for (int i = 0; i < n; i++) {
            listaLlena.pushBack(valores[i]);
        }
        int[] buscados = GeneradorDatos.posicionesAlAzar(BUSQUEDAS_LISTA, n, azar);
        medir("DLL", "find", n, "", BUSQUEDAS_LISTA, "O(n)", v -> {
            long acumulado = 0;
            long t0 = System.nanoTime();
            for (int i = 0; i < BUSQUEDAS_LISTA; i++) {
                if (listaLlena.find(valores[buscados[i]]) != null) {
                    acumulado++;
                }
            }
            long t1 = System.nanoTime();
            sumidero += acumulado;
            if (acumulado != BUSQUEDAS_LISTA) {
                avisar("DLL.find no encontro elementos que si estan");
            }
            return t1 - t0;
        });

        medir("Queue", "enqueue", n, "", n, "O(1)", v -> {
            Queue<Integer> cola = new Queue<>();
            long t0 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                cola.enqueue(valores[i]);
            }
            long t1 = System.nanoTime();
            sumidero += cola.size();
            return t1 - t0;
        });

        medir("Queue", "dequeue", n, "", n, "O(1)", v -> {
            Queue<Integer> cola = new Queue<>(n);
            for (int i = 0; i < n; i++) {
                cola.enqueue(valores[i]);
            }
            long acumulado = 0;
            long t0 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                if (cola.dequeue() != null) {
                    acumulado++;
                }
            }
            long t1 = System.nanoTime();
            sumidero += acumulado;
            return t1 - t0;
        });
    }

    private void medirSistema(int n) {
        int d = deportesPara(n);
        GeneradorDatos gen = new GeneradorDatos(semilla + n, d, MAX_PRACTICA, MAX_INTERESES);
        GeneradorDatos.DatosPrueba datos = gen.generar(n, idsEnOrden);
        String dTexto = String.valueOf(d);
        describirDatos(datos, n, d);
        SistemaDeportes[] ultimo = new SistemaDeportes[1];

        boolean hecho = medir("SistemaDeportes", "registrar", n, dTexto, n, "O(log n)", v -> {
            ultimo[0] = null;
            SistemaDeportes s = new SistemaDeportes();
            long acumulado = 0;
            long t0 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                if (s.registrarEstudiante(datos.ids[i], datos.nombres[i], datos.practica[i], datos.intereses[i]) != null) {
                    acumulado++;
                }
            }
            long t1 = System.nanoTime();
            sumidero += acumulado;
            if (acumulado != n) {
                avisar("registrar devolvio null para " + (n - acumulado) + " estudiantes con ID distinto");
            }
            ultimo[0] = s;
            return t1 - t0;
        });
        if (!hecho || ultimo[0] == null) {
            informar("Sin registrar no se puede medir lo demas con n = " + n);
            return;
        }
        SistemaDeportes sistema = ultimo[0];

        int[] posBuscados = gen.posicionesAlAzar(BUSQUEDAS, n);
        int[] idsBuscados = new int[BUSQUEDAS];
        for (int i = 0; i < BUSQUEDAS; i++) {
            idsBuscados[i] = datos.ids[posBuscados[i]];
        }
        medir("SistemaDeportes", "buscar", n, dTexto, BUSQUEDAS, "O(log n)", v -> {
            long acumulado = 0;
            long t0 = System.nanoTime();
            for (int i = 0; i < BUSQUEDAS; i++) {
                if (sistema.buscarEstudiante(idsBuscados[i]) != null) {
                    acumulado++;
                }
            }
            long t1 = System.nanoTime();
            sumidero += acumulado;
            if (acumulado != BUSQUEDAS) {
                avisar("buscar no encontro " + (BUSQUEDAS - acumulado) + " estudiantes registrados");
            }
            return t1 - t0;
        });

        int k = Math.max(1, n / 10);
        int[] orden = gen.permutacion(n);
        medir("SistemaDeportes", "eliminar", n, dTexto, k, "O(log n)", v -> {
            int inicio = (v % 10) * k;
            long acumulado = 0;
            long t0 = System.nanoTime();
            for (int i = 0; i < k; i++) {
                if (sistema.eliminarEstudiante(datos.ids[orden[(inicio + i) % n]])) {
                    acumulado++;
                }
            }
            long t1 = System.nanoTime();
            sumidero += acumulado;
            if (acumulado != k) {
                avisar("eliminar devolvio false para " + (k - acumulado) + " estudiantes que si estaban");
            }
            long restaurados = 0;
            for (int i = 0; i < k; i++) {
                int p = orden[(inicio + i) % n];
                if (sistema.registrarEstudiante(datos.ids[p], datos.nombres[p], datos.practica[p], datos.intereses[p]) != null) {
                    restaurados++;
                }
            }
            if (restaurados != k) {
                avisar("no se pudieron volver a registrar " + (k - restaurados) + " de los eliminados: el sistema ya no tiene "
                        + n + " estudiantes y las mediciones siguientes no son confiables");
            }
            return t1 - t0;
        });

        int veces = Math.max(1, 100_000 / n);

        describirComunidades(sistema, n);
        medir("SistemaDeportes", "comunidades", n, dTexto, veces, "O(n log n)", v -> { // O(n log n + m): AVL por comunidad
            long acumulado = 0;
            long t0 = System.nanoTime();
            for (int i = 0; i < veces; i++) {
                DLL<Comunidad> grupos = sistema.comunidades();
                acumulado += grupos.size();
            }
            long t1 = System.nanoTime();
            sumidero += acumulado;
            return t1 - t0;
        });

        int[] posConexion = gen.posicionesAlAzar(CONEXIONES, n);
        int[] idsConexion = new int[CONEXIONES];
        for (int i = 0; i < CONEXIONES; i++) {
            idsConexion[i] = datos.ids[posConexion[i]];
        }
        medir("SistemaDeportes", "conexion_promedio", n, dTexto, CONEXIONES, "-", v -> {
            long acumulado = 0;
            long t0 = System.nanoTime();
            for (int i = 0; i < CONEXIONES; i++) {
                DLL<Estudiante> camino = sistema.buscarConexion(idsConexion[i]);
                acumulado += camino.size();
            }
            long t1 = System.nanoTime();
            sumidero += acumulado;
            return t1 - t0;
        });

        Estudiante aislado = sistema.registrarEstudiante(n + 1, "Estudiante sin conexion",
                new String[] {gen.deporte(0)}, new String[] {GeneradorDatos.DEPORTE_FANTASMA});
        if (aislado == null) {
            avisar("no se pudo registrar al estudiante del peor caso de conexion");
            cerrarAvisos();
        } else {
            int idAislado = n + 1;
            medir("SistemaDeportes", "conexion_peor_caso", n, dTexto, veces, "O(n+m)", v -> {
                long acumulado = 0;
                long t0 = System.nanoTime();
                for (int i = 0; i < veces; i++) {
                    DLL<Estudiante> camino = sistema.buscarConexion(idAislado);
                    acumulado += camino.size();
                }
                long t1 = System.nanoTime();
                sumidero += acumulado;
                if (acumulado != 0) {
                    avisar("el peor caso de conexion devolvio un camino, y no deberia existir");
                }
                return t1 - t0;
            });
        }

        int vecesRanking = Math.max(1, 200_000 / d);
        medir("SistemaDeportes", "ranking", n, dTexto, vecesRanking, "O(d)", v -> {
            long acumulado = 0;
            long t0 = System.nanoTime();
            for (int i = 0; i < vecesRanking; i++) {
                acumulado += sistema.deportesPorPracticantes().size();
            }
            long t1 = System.nanoTime();
            sumidero += acumulado;
            return t1 - t0;
        });
    }

    private void describirDatos(GeneradorDatos.DatosPrueba datos, int n, int d) {
        if (silencioso) {
            return;
        }
        long m = 0;
        for (String[] practica : datos.practica) {
            m += practica.length;
        }
        String texto = "n=" + n + ": d=" + d + " deportes, m=" + m + " parejas (estudiante, deporte que practica)";
        System.out.println("  (" + texto + ")");
        csv.println("# " + texto);
        csv.flush();
    }

    private void describirComunidades(SistemaDeportes sistema, int n) {
        if (silencioso) {
            return;
        }
        try {
            DLL<Comunidad> grupos = sistema.comunidades();
            int mayor = 0;
            for (DLLNode<Comunidad> g = grupos.getHead(); g != null; g = g.getNext()) {
                mayor = Math.max(mayor, g.getData().getEstudiantes().size());
            }
            String texto = "n=" + n + ": " + grupos.size() + " comunidades, la mayor con " + mayor + " estudiantes";
            System.out.println("  (" + texto + ")");
            csv.println("# " + texto);
            csv.flush();
        } catch (RuntimeException | StackOverflowError e) {
        }
    }

    private int deportesPara(int n) {
        if (deportes.startsWith("n/")) {
            return Math.max(MIN_DEPORTES, n / Integer.parseInt(deportes.substring(2)));
        }
        return Integer.parseInt(deportes);
    }

    private boolean medir(String estructura, String operacion, int n, String d, int ops, String teorica, Prueba prueba) {
        double[] porOperacion = new double[vueltas];
        avisosPrueba = 0;
        try {
            long inicio = System.nanoTime();
            long opsHechas = 0;
            int descartadas = 0;
            while (calentamiento > 0 && (descartadas < calentamiento || (descartadas < MAX_VUELTAS_CALENTAMIENTO
                    && opsHechas < OPS_CALENTAMIENTO && System.nanoTime() - inicio < NS_CALENTAMIENTO))) {
                prueba.ejecutar(descartadas);
                descartadas++;
                opsHechas += ops;
            }
            System.gc();
            for (int r = 0; r < vueltas; r++) {
                porOperacion[r] = prueba.ejecutar(descartadas + r) / (double) ops;
            }
        } catch (UnsupportedOperationException e) {
            informar(String.format("%-16s %-18s omitida (%s)", estructura, operacion,
                    e.getMessage() == null ? "pendiente" : e.getMessage()));
            if (!silencioso) {
                omitidas++;
            }
            return false;
        } catch (OutOfMemoryError e) {
            informar(String.format("%-16s %-18s omitida: no alcanzo la memoria, hay que subir -Xmx", estructura, operacion));
            if (!silencioso) {
                omitidas++;
                sinMemoria = true;
            }
            return false;
        } catch (StackOverflowError e) {
            informar(String.format("%-16s %-18s FALLO: StackOverflowError, revisar la recursion o subir -Xss", estructura, operacion));
            avisosPrueba = silencioso ? 0 : Math.max(avisosPrueba, 1);
            return false;
        } catch (Exception e) {
            StackTraceElement donde = e.getStackTrace().length > 0 ? e.getStackTrace()[0] : null;
            informar(String.format("%-16s %-18s FALLO: %s en %s", estructura, operacion, e, donde));
            avisosPrueba = silencioso ? 0 : Math.max(avisosPrueba, 1);
            return false;
        } finally {
            cerrarAvisos();
        }
        if (silencioso) {
            return true;
        }

        Arrays.sort(porOperacion);
        int mitad = porOperacion.length / 2;
        double mediana = porOperacion.length % 2 == 1 ? porOperacion[mitad]
                : (porOperacion[mitad - 1] + porOperacion[mitad]) / 2.0;
        double minimo = porOperacion[0];
        double maximo = porOperacion[porOperacion.length - 1];

        csv.println(String.format(Locale.ROOT, "%s,%s,%d,%s,%d,%d,%.2f,%.2f,%.2f,%s",
                estructura, operacion, n, d, ops, vueltas, mediana, minimo, maximo, teorica));
        csv.flush();
        System.out.println(String.format(Locale.ROOT, "%-16s %-18s %14.1f ns/op   (min %.1f, max %.1f)",
                estructura, operacion, mediana, minimo, maximo));
        return true;
    }

    private void avisar(String mensaje) {
        if (silencioso) {
            return;
        }
        if (avisosPrueba == 0) {
            System.out.println("AVISO: " + mensaje);
        }
        avisosPrueba++;
    }

    private void cerrarAvisos() {
        if (avisosPrueba > 0) {
            avisos++;
            if (avisosPrueba > 1) {
                System.out.println("  (se repitio " + avisosPrueba + " veces en esa prueba)");
            }
        }
        avisosPrueba = 0;
    }

    private void informar(String mensaje) {
        if (!silencioso) {
            System.out.println(mensaje);
        }
    }

    private void escribirEncabezado() {
        Runtime jvm = Runtime.getRuntime();
        csv.println("# Mediciones del Analizador, " + LocalDateTime.now().withNano(0));
        csv.println("# Java " + System.getProperty("java.version") + " (" + System.getProperty("java.vm.name") + ")");
        csv.println("# " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " "
                + System.getProperty("os.arch") + ", " + jvm.availableProcessors() + " procesadores, memoria maxima de la JVM "
                + jvm.maxMemory() / (1024 * 1024) + " MB");
        csv.println("# semilla=" + semilla + ", vueltas=" + vueltas + ", calentamiento=" + calentamiento
                + ", deportes=" + deportes + ", ids=" + (idsEnOrden ? "creciente" : "aleatorio"));
        csv.println("# cada estudiante practica entre 1 y " + MAX_PRACTICA + " deportes y le interesan entre 1 y " + MAX_INTERESES);
        csv.println("estructura,operacion,n,d,ops_por_vuelta,vueltas,mediana_ns_por_op,minimo_ns_por_op,maximo_ns_por_op,teorica");
        csv.flush();
    }

    private static void guardarAnterior(File archivo) throws IOException {
        if (!archivo.exists()) {
            return;
        }
        String nombre = archivo.getName();
        int punto = nombre.lastIndexOf('.');
        String base = punto > 0 ? nombre.substring(0, punto) : nombre;
        String extension = punto > 0 ? nombre.substring(punto) : "";
        String fecha = LocalDateTime.ofInstant(Instant.ofEpochMilli(archivo.lastModified()), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        File respaldo = new File(archivo.getParentFile(), base + "_" + fecha + extension);
        if (respaldo.exists() || !archivo.renameTo(respaldo)) {
            throw new IOException("No se pudo guardar el archivo anterior como " + respaldo + "; muevalo o borrelo a mano");
        }
        System.out.println("Ya existia " + nombre + ", quedo guardado como " + respaldo.getName());
    }

    private static String valor(String[] args, String clave, String porDefecto) {
        String prefijo = "--" + clave + "=";
        for (String a : args) {
            if (a.startsWith(prefijo)) {
                return a.substring(prefijo.length());
            }
        }
        return porDefecto;
    }

    private static long numero(String opcion, String texto) {
        try {
            return Long.parseLong(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("--" + opcion + " espera numeros enteros y llego \"" + texto + "\"");
        }
    }

    private static void revisarOpciones(String[] args) {
        for (String a : args) {
            boolean conocida = false;
            for (String opcion : OPCIONES) {
                if (a.startsWith("--" + opcion + "=")) {
                    conocida = true;
                }
            }
            if (!conocida) {
                throw new IllegalArgumentException("Opcion desconocida: " + a + " (las validas estan al inicio de Analizador.java)");
            }
        }
    }

    private static void revisarDeportes(String texto) {
        if (texto.startsWith("n/")) {
            if (numero("deportes", texto.substring(2)) < 1) {
                throw new IllegalArgumentException("--deportes=n/k necesita un k de al menos 1");
            }
        } else if (numero("deportes", texto) < MAX_PRACTICA + MAX_INTERESES) {
            throw new IllegalArgumentException("--deportes necesita al menos " + (MAX_PRACTICA + MAX_INTERESES)
                    + " para poder sortear sin repetir");
        }
    }
}
