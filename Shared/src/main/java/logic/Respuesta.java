package logic;

import java.io.Serializable;

public class Respuesta implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean exitoso;
    private Object resultado;
    private String error;

    public Respuesta(boolean exitoso, Object resultado, String error) {
        this.exitoso = exitoso;
        this.resultado = resultado;
        this.error = error;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public Object getResultado() {
        return resultado;
    }

    public String getError() {
        return error;
    }
}