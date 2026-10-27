
package Presentar.Login.LoginView;

import Presentar.Login.ControllerLogin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangePassView extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField actualContraseña;
    private JPasswordField contraseñaNueva1;
    private JPasswordField contraseñaNueva2;
    private JButton exitButton;

    private ControllerLogin controller = new ControllerLogin();
    private String idUsuario;

    public ChangePassView(String id) {
        this.idUsuario = id;

        setContentPane(contentPane);
        setModal(true);
        pack();
        setLocationRelativeTo(null);

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarCambio();
            }
        });
    }

    public JButton getButtonOK() {
        return buttonOK;
    }

    public JButton getButtonCancel() {
        return buttonCancel;
    }

    public JTextField getTextField1() {
        return actualContraseña;
    }

    public JPasswordField getPasswordField1() {
        return contraseñaNueva1;
    }

    public JPasswordField getPasswordField2() {
        return contraseñaNueva2;
    }

    private void confirmarCambio() {
        String claveActual = actualContraseña.getText();
        String claveNueva = new String(contraseñaNueva1.getPassword());
        String confirmacion = new String(contraseñaNueva2.getPassword());

        if (claveActual.isEmpty()
                || claveNueva.isEmpty()
                || confirmacion.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Todos los campos son obligatorios.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!claveNueva.equals(confirmacion)) {
            JOptionPane.showMessageDialog(
                    this,
                    "La clave nueva no coincide con la confirmación.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            boolean exito = controller.cambiarClave(
                    idUsuario,
                    claveActual,
                    claveNueva
            );

            if (exito) {
                JOptionPane.showMessageDialog(
                        this,
                        "Clave actualizada correctamente."
                );
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "La clave actual es incorrecta o no fue posible actualizarla.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                limpiarCampos();
            }

        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarCampos() {
        actualContraseña.setText("");
        contraseñaNueva1.setText("");
        contraseñaNueva2.setText("");
        actualContraseña.requestFocus();
    }
}
