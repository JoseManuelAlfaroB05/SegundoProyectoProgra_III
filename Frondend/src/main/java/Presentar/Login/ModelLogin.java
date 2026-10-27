
package Presentar.Login;

import ClasesUsuario.User;

public class ModelLogin {

    private String varId;
    private String varClave;
    private User usuarioAutenticado;

    public ModelLogin() {
        this.varId = "";
        this.varClave = "";
        this.usuarioAutenticado = null;
    }

    public ModelLogin(String id, String clave) {
        this.varId = id;
        this.varClave = clave;
        this.usuarioAutenticado = null;
    }

    public String getVarId() {
        return varId;
    }

    public void setVarId(String id) {
        this.varId = id;
    }

    public String getVarClave() {
        return varClave;
    }

    public void setVarClave(String clave) {
        this.varClave = clave;
    }

    public User getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    public void setUsuarioAutenticado(User usuarioAutenticado) {
        this.usuarioAutenticado = usuarioAutenticado;
    }
}
