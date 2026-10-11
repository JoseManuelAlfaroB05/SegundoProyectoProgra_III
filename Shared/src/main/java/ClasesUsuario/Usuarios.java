package ClasesUsuario;

import java.util.ArrayList;

public class Usuarios {
    private ArrayList<User> usuarios;

    public Usuarios() {
        usuarios = new ArrayList<>();
    }

    public ArrayList<User> getUsuarios() {
        return usuarios;
    }

    public void agregar(User usuario) {
        usuarios.add(usuario);
    }
}