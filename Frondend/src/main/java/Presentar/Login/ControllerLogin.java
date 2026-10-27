
package Presentar.Login;

import ClasesUsuario.User;
import Presentar.Login.LoginView.ChangePassView;
import Presentar.MainViews.MainView;
import logic.ServiceLocator;

public class ControllerLogin {

    private ModelLogin model;

    public ControllerLogin() {
        model = new ModelLogin();
    }

    public User autenticar(String id, String claveIngresada) {
        model.setVarId(id.trim());
        model.setVarClave(claveIngresada);

        try {
            ServiceLocator.conectar();

            User usuario = ServiceLocator.getService()
                    .buscarUsuarioPorId(model.getVarId());

            System.out.println("ID ingresado: [" + model.getVarId() + "]");
            System.out.println("Usuario encontrado: " + (usuario != null));

            if (usuario != null
                    && usuario.getVarClave().equals(model.getVarClave())) {
                model.setUsuarioAutenticado(usuario);
                return usuario;
            }

            return null;

        } catch (Exception ex) {
            ex.printStackTrace();

            throw new IllegalStateException(
                    "No fue posible autenticar mediante el Backend: "
                            + ex.getMessage(),
                    ex
            );
        }
    }

    public void invocarPrincipal(User usuarioLogueado) {
        MainView main = new MainView(usuarioLogueado);
        main.setVisible(true);
    }

    public void changePass(String id) {
        ChangePassView cpv = new ChangePassView(id);
        cpv.setVisible(true);
    }

    public boolean cambiarClave(String id, String claveActual, String claveNueva) {
        try {
            User usuario = ServiceLocator.getService()
                    .buscarUsuarioPorId(id);

            if (usuario == null ||
                    !usuario.getVarClave().equals(claveActual)) {
                return false;
            }

            return ServiceLocator.getService()
                    .actualizarClave(id, claveNueva);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "No fue posible cambiar la clave mediante el Backend.", ex);
        }
    }
}
