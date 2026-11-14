package app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import deportes.Comunidad;
import deportes.Deporte;
import deportes.Estudiante;
import deportes.SistemaDeportes;
import estructuras.DLL;
import estructuras.DLLNode;

// Interfaz de consola del prototipo. Los datos se pueden cargar desde un archivo de texto en UTF-8 con una
// línea por estudiante: id;nombre;deportes que practica;deportes que le interesan (las listas, separadas por coma)
// Uso: java -cp out app.Main [archivo de datos]
public class Main {

    private static final SistemaDeportes sistema = new SistemaDeportes();
    private static final Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length > 0) {
            cargarArchivo(args[0]);
        }
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Opción: ");
            try {
                switch (opcion) {
                    case 1 -> registrar();
                    case 2 -> consultar();
                    case 3 -> eliminar();
                    case 4 -> mostrarComunidades();
                    case 5 -> buscarConexion();
                    case 6 -> mostrarRanking();
                    case 7 -> cargarArchivo(leerTexto("Ruta del archivo: "));
                    case 8 -> listarEstudiantes();
                    case 9 -> mostrarPracticantes();
                    case 10 -> dibujarArbol();
                    case 0 -> System.out.println("Hasta luego.");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("=== Deportes UNAL (" + sistema.cantidadEstudiantes() + " estudiantes, "
                + sistema.cantidadDeportes() + " deportes) ===");
        System.out.println("1. Registrar un estudiante (RF1)");
        System.out.println("2. Consultar un estudiante por ID (RF2)");
        System.out.println("3. Eliminar un estudiante (RF3)");
        System.out.println("4. Ver las comunidades deportivas (RF4)");
        System.out.println("5. Buscar conexión con un deporte de interés (RF5 y RF6)");
        System.out.println("6. Ver los deportes ordenados por cantidad de practicantes (RF7)");
        System.out.println("7. Cargar estudiantes desde un archivo");
        System.out.println("8. Listar todos los estudiantes");
        System.out.println("9. Ver los practicantes de un deporte");
        System.out.println("10. Dibujar el árbol AVL de estudiantes");
        System.out.println("0. Salir");
    }

    private static void registrar() {
        int id = leerEntero("ID: ");
        String nombre = leerTexto("Nombre: ");
        String[] practica = leerTexto("Deportes que practica (separados por coma): ").split(",");
        String[] intereses = leerTexto("Deportes que le interesan (separados por coma): ").split(",");
        Estudiante estudiante = sistema.registrarEstudiante(id, nombre, practica, intereses);
        System.out.println("Registrado: " + describir(estudiante));
    }

    private static void consultar() {
        Estudiante estudiante = sistema.buscarEstudiante(leerEntero("ID: "));
        System.out.println(estudiante == null ? "No existe un estudiante con ese ID." : describir(estudiante));
    }

    private static void eliminar() {
        boolean eliminado = sistema.eliminarEstudiante(leerEntero("ID: "));
        System.out.println(eliminado ? "Estudiante eliminado." : "No existe un estudiante con ese ID.");
    }

    private static void mostrarComunidades() {
        DLL<Comunidad> comunidades = sistema.comunidades();
        if (comunidades.isEmpty()) {
            System.out.println("Todavía no hay estudiantes que compartan un deporte.");
            return;
        }
        int numero = 1;
        for (Comunidad comunidad : comunidades) {
            DLL<Estudiante> miembros = comunidad.getEstudiantes().valoresEnOrden();
            System.out.println("Comunidad " + numero + " (" + miembros.size() + " estudiantes): " + nombres(miembros));
            numero++;
        }
    }

    private static void buscarConexion() {
        int id = leerEntero("ID del estudiante: ");
        Estudiante origen = sistema.buscarEstudiante(id);
        if (origen == null) {
            System.out.println("No existe un estudiante con ese ID.");
            return;
        }
        if (origen.getIntereses().isEmpty()) {
            System.out.println("No existe conexión para " + origen.getNombre() + ": no tiene deportes de interés.");
            return;
        }
        DLL<Estudiante> camino = sistema.buscarConexion(id);
        if (camino.isEmpty()) {
            System.out.println("No existe conexión para " + origen.getNombre() + ": ningún estudiante conectado con "
                    + origen.getNombre() + " practica " + nombresDeportes(origen.getIntereses()) + ".");
            return;
        }
        Estudiante contacto = camino.getTail().getData();
        Deporte deporte = sistema.deporteDeInteresDe(id, contacto);
        int intermediarios = camino.size() - 2;
        StringBuilder texto = new StringBuilder();
        texto.append(origen.getNombre()).append(" puede contactar a ").append(contacto.getNombre())
                .append(" (ID ").append(contacto.getId()).append("), que practica ").append(deporte.getNombreCurso())
                .append(". Conexión ")
                .append(intermediarios == 0 ? "directa" : "indirecta, con " + intermediarios
                        + (intermediarios == 1 ? " intermediario" : " intermediarios"))
                .append(": ");
        for (DLLNode<Estudiante> n = camino.getHead(); n != null; n = n.getNext()) {
            texto.append(n.getData().getNombre());
            if (n.getNext() != null) {
                Deporte comun = SistemaDeportes.deporteEnComun(n.getData(), n.getNext().getData());
                texto.append(" -[").append(comun.getNombreCurso()).append("]- ");
            }
        }
        System.out.println(texto);
    }

    private static void mostrarRanking() {
        int posicion = 1;
        for (Deporte deporte : sistema.deportesPorPracticantes()) {
            int cantidad = deporte.getCantidadPracticantes();
            System.out.println(posicion + ". " + deporte.getNombreCurso() + ": " + cantidad
                    + (cantidad == 1 ? " estudiante" : " estudiantes"));
            posicion++;
        }
    }

    private static void listarEstudiantes() {
        for (Estudiante estudiante : sistema.estudiantesPorId()) {
            System.out.println(describir(estudiante));
        }
    }

    private static void mostrarPracticantes() {
        DLL<Estudiante> practicantes = sistema.practicantesDe(leerTexto("Deporte: "));
        if (practicantes == null) {
            System.out.println("Ese deporte no está registrado.");
        } else {
            System.out.println(practicantes.size() + " practicantes: " + nombres(practicantes));
        }
    }

    // la visualización es para pocos datos; con muchos estudiantes solo se informa la altura
    private static void dibujarArbol() {
        int n = sistema.cantidadEstudiantes();
        System.out.println("Árbol AVL de estudiantes por ID (" + n + " nodos, altura " + sistema.alturaArbolEstudiantes()
                + "). Se lee de lado: la raíz está a la izquierda y entre corchetes va la altura de cada nodo.");
        if (n > 64) {
            System.out.println("Hay demasiados estudiantes para dibujarlo en la consola.");
        } else {
            System.out.print(sistema.dibujarArbolEstudiantes());
        }
    }

    // carga el archivo línea por línea; las líneas vacías o que empiezan con # se ignoran
    private static void cargarArchivo(String ruta) {
        int cargados = 0;
        int numeroLinea = 0;
        // se lee siempre en UTF-8: con la codificación por defecto (en Windows con Java 17 no es UTF-8) "Fútbol"
        // llegaría con otros caracteres y quedaría como un deporte distinto de "futbol"
        try (BufferedReader lector = new BufferedReader(new FileReader(ruta, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (numeroLinea == 1 && linea.startsWith("﻿")) {
                    linea = linea.substring(1); // marca BOM que agregan algunos editores al guardar en UTF-8
                }
                if (linea.isBlank() || linea.trim().startsWith("#")) {
                    continue;
                }
                String[] partes = linea.split(";", -1);
                if (partes.length != 4) {
                    System.out.println("Línea " + numeroLinea + " ignorada: se esperaban 4 campos separados por ;");
                    continue;
                }
                try {
                    sistema.registrarEstudiante(Integer.parseInt(partes[0].trim()), partes[1],
                            partes[2].split(","), partes[3].split(","));
                    cargados++;
                } catch (NumberFormatException e) { // va antes porque es un caso de IllegalArgumentException
                    System.out.println("Línea " + numeroLinea + " ignorada: el ID debe ser un número entero");
                } catch (IllegalArgumentException e) {
                    System.out.println("Línea " + numeroLinea + " ignorada: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
        }
        System.out.println(cargados + (cargados == 1 ? " estudiante cargado" : " estudiantes cargados") + " desde " + ruta);
    }

    private static String describir(Estudiante estudiante) {
        StringBuilder practica = new StringBuilder("[");
        for (DLLNode<Estudiante.Practica> n = estudiante.getPractica().getHead(); n != null; n = n.getNext()) {
            practica.append(n.getData().getDeporte().getNombreCurso());
            if (n.getNext() != null) {
                practica.append(", ");
            }
        }
        practica.append("]");
        return estudiante.getNombre() + " (ID " + estudiante.getId() + ") | practica: " + practica
                + " | le interesan: " + nombresDeportes(estudiante.getIntereses());
    }

    private static String nombres(DLL<Estudiante> estudiantes) {
        StringBuilder texto = new StringBuilder();
        for (DLLNode<Estudiante> n = estudiantes.getHead(); n != null; n = n.getNext()) {
            texto.append(n.getData().getNombre());
            if (n.getNext() != null) {
                texto.append(", ");
            }
        }
        return texto.toString();
    }

    private static String nombresDeportes(DLL<Deporte> deportes) {
        StringBuilder texto = new StringBuilder("[");
        for (DLLNode<Deporte> n = deportes.getHead(); n != null; n = n.getNext()) {
            texto.append(n.getData().getNombreCurso());
            if (n.getNext() != null) {
                texto.append(", ");
            }
        }
        return texto.append("]").toString();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return Integer.parseInt(texto.trim());
            } catch (NumberFormatException e) {
                System.out.println("Se esperaba un número entero.");
            }
        }
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!entrada.hasNextLine()) { // fin de la entrada (por ejemplo, Ctrl+D o un archivo redirigido)
            System.out.println();
            System.out.println("Fin de la entrada. Hasta luego.");
            System.exit(0);
        }
        return entrada.nextLine();
    }
}
