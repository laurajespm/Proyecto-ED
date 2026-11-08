package deportes;
public class Comunidad {

    private int id;
    private ArbolAVL<Integer, Estudiante> estudiantes;

    public Comunidad(int id){
        this.id = id;
        this.estudiantes = new ArbolAVL<>();
    }

    public int getId(){
        return this.id;
    }

    public ArbolAVL<Integer, Estudiante> getEstudiantes() {
        return estudiantes;
    }
}
