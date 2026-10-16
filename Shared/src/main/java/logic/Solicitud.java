package logic;

import java.io.Serializable;

public class Solicitud implements Serializable {
    private static final long serialVersionUID = 1L;

    private String operacion;
    private Object[] parametros;

    public Solicitud(String operacion, Object... parametros) {
        this.operacion = operacion;
        this.parametros = parametros;
    }

    public String getOperacion() {
        return operacion;
    }

    public Object[] getParametros() {
        return parametros;
    }
}