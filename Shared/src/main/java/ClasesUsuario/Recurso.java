package ClasesUsuario;

public class Recurso {
    private String varId;
    private String categoriaId;
    private CategoriaRecurso varRecurso;
    private String varDescripciom;

    public Recurso() {
    }

    public Recurso(String pId, CategoriaRecurso pRecurso, String pDescripcion) {
        this.varId = pId;
        this.varRecurso = pRecurso;
        this.categoriaId = pRecurso != null ? pRecurso.getVarId() : null;
        this.varDescripciom = pDescripcion;
    }

    public String getId() {
        return varId;
    }

    public CategoriaRecurso getRecurso() {
        return varRecurso;
    }

    public String getDescripcion() {
        return varDescripciom;
    }

    public String getCategoriaId() {
        return categoriaId;
    }

    public void setRecurso(CategoriaRecurso pRecurso) {
        this.varRecurso = pRecurso;
        this.categoriaId = pRecurso != null ? pRecurso.getVarId() : null;
    }

    public void setDescripcion(String pDescripcion) {
        this.varDescripciom = pDescripcion;
    }

    public void setCategoriaId(String pCategoriaId) {
        this.categoriaId = pCategoriaId;
    }
}