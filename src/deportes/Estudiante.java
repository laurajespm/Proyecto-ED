package deportes;

import estructuras.DLL;
import estructuras.DLLNode;
import estructuras.Queue;

public class Estudiante implements Comparable<Estudiante> {

    private final int id;
    private final String nombre;
    private final String correo;

    private int edad;
    private int numeroTelefono;
    private String facultad;
    private String carrera;

    private final DLL<Practica> practica;
    private final DLL<Deporte> intereses;
    private final Queue<Deporte> solicitudesPendientes;

    public static class Practica {

        private final Deporte deporte;
        private final DLLNode<Estudiante> nodo;

        public Practica(Deporte deporte, DLLNode<Estudiante> nodo) {
            this.deporte = deporte;
            this.nodo = nodo;
        }

        public Deporte getDeporte() {
            return deporte;
        }

        public DLLNode<Estudiante> getNodo() {
            return nodo;
        }
    }

    public Estudiante(
            String nombre,
            int id,
            int edad,
            int numeroTelefono,
            String correo,
            String facultad,
            String carrera) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        this.nombre = nombre.trim();
        this.id = id;
        this.edad = edad;
        this.numeroTelefono = numeroTelefono;
        this.correo = correo;
        this.facultad = facultad;
        this.carrera = carrera;

        this.practica = new DLL<>();
        this.intereses = new DLL<>();
        this.solicitudesPendientes = new Queue<>(5);
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public int getNumeroTelefono() {
        return numeroTelefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getFacultad() {
        return facultad;
    }

    public String getCarrera() {
        return carrera;
    }

    public DLL<Practica> getPractica() {
        return practica;
    }

    public DLL<Deporte> getIntereses() {
        return intereses;
    }

    public Queue<Deporte> getSolicitudesPendientes() {
        return solicitudesPendientes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Estudiante otro = (Estudiante) obj;

        return this.id == otro.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.id);
    }

    @Override
    public int compareTo(Estudiante otro) {
        return Integer.compare(this.edad, otro.edad);
    }

    @Override
    public String toString() {
        return nombre + " (ID " + id + ")";
    }
}
