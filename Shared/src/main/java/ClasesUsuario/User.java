package ClasesUsuario;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String varId;
    private String varClave;
    private Rol varRol;
    private String varNombre;
    private String varTelefono;

    public User() {
    }

    public User(String id, String clave, Rol rol) {
        this(id, clave, rol, "", "");
    }

    public User(String id, String clave, Rol rol, String nombre, String telefono) {
        this.varId = id;
        this.varClave = clave;
        this.varRol = rol;
        this.varNombre = nombre;
        this.varTelefono = telefono;
    }

    public String getVarId() {
        return this.varId;
    }

    public String getVarClave() {
        return this.varClave;
    }

    public Rol getVarRol() {
        return this.varRol;
    }

    public String getVarNombre() {
        return varNombre;
    }

    public String getVarTelefono() {
        return varTelefono;
    }
}