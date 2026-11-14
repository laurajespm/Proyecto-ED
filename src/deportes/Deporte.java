package deportes;

import estructuras.DLL;
import estructuras.Queue;

// Deporte y lista de los estudiantes que lo practican. La cantidad de practicantes es el tamaño de esa
// lista, que la DLL lleva en un contador, así que contarlos no exige recorrerla
public class Deporte{
    private final int id;
    private final String nombreCurso;
    private String horario;
    private final int cuposMaximos;
    final String tipoCurso;
    private String entrenador;

    private Queue<Estudiante> colaSolicitudes;
    private DLL<Estudiante> listaInscritos;
    private Queue<Estudiante> colaEsperaInscripcion;

    // marcas que usan los recorridos por anchura de SistemaDeportes: en qué recorrido se expandió el deporte
    // por última vez y en qué búsqueda de conexión es un deporte de interés (ver allí la explicación)
    int marcaVisita;
    int marcaInteres;

    public Deporte(int id, String nombre, String horario, int cupos, String tipo_curso, String entrenador){
        this.id = id;
        this.nombreCurso = nombre;
        this.horario = horario;
        this.cuposMaximos = cupos;
        this.tipoCurso = tipo_curso;
        this.entrenador = entrenador;

        // la cola crece sola, así que no se reserva un arreglo del tamaño de los cupos (pueden ser muchos)
        this.colaSolicitudes = new Queue<Estudiante>(Math.max(1, Math.min(cuposMaximos, 16)));
        this.listaInscritos = new DLL <Estudiante>();
        this.colaEsperaInscripcion = new Queue<Estudiante>(10);
    }

    public int get_Id(){
        return this.id;
    }

    public String getNombreCurso(){
        return this.nombreCurso;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public int getCuposMaximos() {
        return cuposMaximos;
    }

    public int getCuposDisponibles() {
        return Math.max(0,cuposMaximos - listaInscritos.size());
    }

    public int getCantidadPracticantes(){
        return listaInscritos.size();
    }
    
    public String getTipoCurso() {
        return tipoCurso;
    }

    public String getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(String entrenador) {
        this.entrenador = entrenador;
    }

    public Queue<Estudiante> getColaSolicitudes() {
        return colaSolicitudes;
    }

    public DLL<Estudiante> getListaInscritos() {
        return listaInscritos;
    }
    public Queue<Estudiante> getColaEsperaInscripcion() {
        return colaEsperaInscripcion;
    }

    @Override
    public String toString() {
        return "Deporte{" +
                "id=" + id +
                ", nombre='" + nombreCurso + '\'' +
                ", tipo='" + tipoCurso + '\'' +
                ", cuposDisponibles=" + getCuposDisponibles() + "/" + cuposMaximos +
                '}';
    }

}
