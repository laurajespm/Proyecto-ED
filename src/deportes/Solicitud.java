package deportes;

public class Solicitud {

    private final Estudiante estudiante;
    private final Deporte deporte;
    private String descripcion;

    public Solicitud(Estudiante estudiante, Deporte deporte, String descripcion) {
        this.estudiante = estudiante;
        this.deporte = deporte;
        this.descripcion = descripcion;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
