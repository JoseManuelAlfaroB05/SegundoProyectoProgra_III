
package Presentar.Informacion;

import Presentar.Login.LoginView.LoginView;
import logic.ServiceLocator;

import javax.swing.JFrame;

public class ControllerInformacion {

    public void cerrarSesion(JFrame ventanaActual) {
        try {
            ServiceLocator.getService().desconectar();
        } catch (Exception ex) {
            System.err.println("Error al cerrar la conexión: " + ex.getMessage());
        }

        ventanaActual.dispose();

        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }
}
