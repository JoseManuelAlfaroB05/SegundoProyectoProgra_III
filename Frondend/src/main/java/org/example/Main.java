package org.example;

import Presentar.Login.LoginView.LoginView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    "javax.swing.plaf.nimbus.NimbusLookAndFeel"
            );
        } catch (Exception ex) {
            System.err.println(
                    "No se pudo aplicar el estilo Nimbus: "
                            + ex.getMessage()
            );
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginView login = new LoginView();
                login.setVisible(true);
            }
        });
    }
}