package deportes;

import estructuras.DLL;

public class DeporteRepresentativo extends Deporte{
    private final String requisitosNivel;
    private boolean estadoConvocatoria;
    private Estudiante capitanPrincipal;
    private DLL<Estudiante> titulares;

    public DeporteRepresentativo(int id, String nombre, String horario, int cupos, String tipo_curso, String entrenador, String requisitosNivel, boolean estadoConvocatoria, Estudiante capitanPrincipal, DLL<Estudiante> titulares){
        super(id, nombre, horario, cupos, "Representativo", entrenador);
        this.requisitosNivel = requisitosNivel;
        this.estadoConvocatoria = estadoConvocatoria;
        this.capitanPrincipal = capitanPrincipal;
        this.titulares = new DLL<Estudiante>();
    }

    public String getRequisitosNivel() {
        return requisitosNivel;
    }

    public boolean isConvocatoriaAbierta() {
        return estadoConvocatoria;
    }

    public void setEstadoConvocatoria(boolean estadoConvocatoria) {
        this.estadoConvocatoria = estadoConvocatoria;
    }

    public Estudiante getCapitanPrincipal() {
        return capitanPrincipal;
    }

    public void setCapitanPrincipal(Estudiante capitanPrincipal) {
        this.capitanPrincipal = capitanPrincipal;
    }

    public DLL<Estudiante> getTitulares() {
        return titulares;
    }
}
