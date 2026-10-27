
package Presentar.Login.LoginView;

import ClasesUsuario.User;
import Presentar.Login.ControllerLogin;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    private JPanel mainPanel;
    private JPanel centralPanel;
    private JTextField UserTextField;
    private JPasswordField passwordField;
    private JButton btnIngresar;
    private JButton btnRechazar;
    private JButton restaurarPass;

    private ControllerLogin controller = new ControllerLogin();

    public LoginView() {

        setContentPane(mainPanel);
        setTitle("Login");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel.setBackground(new Color(255, 255, 255));

        centralPanel.setBackground(new Color(120, 120, 124, 107));
        centralPanel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(107, 109, 112),
                        2
                )
        );

        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String id = UserTextField.getText().trim();
                String clave = new String(passwordField.getPassword());

                if (id.isEmpty() || clave.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            LoginView.this,
                            "Debe ingresar el usuario y la clave.",
                            "Campos obligatorios",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                try {
                    User usuario = controller.autenticar(id, clave);

                    if (usuario != null) {
                        JOptionPane.showMessageDialog(
                                LoginView.this,
                                "¡Bienvenido! Rol: " + usuario.getVarRol()
                        );

                        dispose();
                        controller.invocarPrincipal(usuario);

                    } else {
                        JOptionPane.showMessageDialog(
                                LoginView.this,
                                "Usuario o clave incorrectos.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );

                        UserTextField.setText("");
                        passwordField.setText("");
                        UserTextField.requestFocus();
                    }

                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(
                            LoginView.this,
                            ex.getMessage(),
                            "Error de conexión",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        btnRechazar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                UserTextField.setText("");
                passwordField.setText("");
                UserTextField.requestFocus();
            }
        });

        restaurarPass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String id = UserTextField.getText().trim();

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            LoginView.this,
                            "Ingrese su ID para cambiar la clave.",
                            "Campo obligatorio",
                            JOptionPane.WARNING_MESSAGE
                    );
                    UserTextField.requestFocus();
                    return;
                }

                controller.changePass(id);
            }
        });
    }
}
