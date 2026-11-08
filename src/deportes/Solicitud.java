package deportes;

public class Solicitud {

    private final Estudiante estudiante;
    private final Deporte deporte;

    public Solicitud(Estudiante estudiante, Deporte deporte) {
        this.estudiante = estudiante;
        this.deporte = deporte;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Deporte getDeporte() {
        return deporte;
    }
}
