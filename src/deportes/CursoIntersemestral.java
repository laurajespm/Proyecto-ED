public class CursoIntersemestral extends Deporte {
    private final int periodo;
    private int duracion;

    public CursoIntersemestral(int id, String nombre, String horario, int cupos, String tipo_curso, String entrenador, int periodo, int duracionSemanas){
        super(id, nombre, horario, cupos, "Intersemestral", entrenador);
        this.periodo = periodo;
        this.duracion = duracionSemanas;
    }

    public int getPeriodo() {
        return periodo;
    }

    public int getDuracion() {
        return duracion;
    }
}
