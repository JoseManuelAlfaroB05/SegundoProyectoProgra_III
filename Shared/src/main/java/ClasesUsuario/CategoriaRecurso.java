package ClasesUsuario;

import java.io.Serializable;

public class CategoriaRecurso implements Serializable {

    private static final long serialVersionUID = 1L;

    private String varId;
    private String varDescripcion;

    public CategoriaRecurso() {
    }

    public CategoriaRecurso(String varId, String varDescripcion) {
        this.varId = varId;
        this.varDescripcion = varDescripcion;
    }

    public String getVarId() {
        return varId;
    }

    public String getDescripcion() {
        return varDescripcion;
    }

    public void setDescripcion(String varDescripcion) {
        this.varDescripcion = varDescripcion;
    }

    @Override
    public String toString() {
        return varDescripcion;
    }
}