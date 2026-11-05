package Presentar.Informacion;

import ClasesUsuario.User;

import javax.swing.*;
import java.awt.*;

public class InformacionView extends JPanel {

    private JPanel PrincipalPanel;
    private JPanel panelInformacion;
    private JPanel panelNotificaciones;
    private JLabel labelTitulo;
    private JLabel labelId;
    private JLabel labelNombre;
    private JLabel labelTelefono;
    private JLabel labelRol;
    private JButton buttonCerrarSesion;
    private JLabel labelNotificaciones;
    private JTextArea textAreaNotificaciones;
    private JScrollPane scrollNotificaciones;

    private final User usuarioLogueado;
    private final ControllerInformacion controller;

    public InformacionView(User usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        this.controller = new ControllerInformacion();

        setLayout(new BorderLayout());
        add(PrincipalPanel, BorderLayout.CENTER);

        configurarInformacion();
        configurarNotificaciones();
        configurarListeners();
    }

    private void configurarInformacion() {
        labelId.setText("ID: " + usuarioLogueado.getVarId());
        labelNombre.setText("Nombre: " + usuarioLogueado.getVarNombre());
        labelTelefono.setText("Teléfono: " + usuarioLogueado.getVarTelefono());
        labelRol.setText("Rol: " + usuarioLogueado.getVarRol());
    }

    private void configurarNotificaciones() {
        textAreaNotificaciones.setEditable(false);
        textAreaNotificaciones.setLineWrap(true);
        textAreaNotificaciones.setWrapStyleWord(true);
        textAreaNotificaciones.setText("Aquí aparecerán las notificaciones del sistema.");
    }

    private void configurarListeners() {
        buttonCerrarSesion.addActionListener(e -> {
            Window ventana = SwingUtilities.getWindowAncestor(this);
            if (ventana instanceof JFrame) {
                controller.cerrarSesion((JFrame) ventana);
            }
        });
    }

    public void agregarNotificacion(String mensaje) {
        if (textAreaNotificaciones.getText().equals(
                "Aquí aparecerán las notificaciones del sistema.")) {
            textAreaNotificaciones.setText("");
        }

        textAreaNotificaciones.append(mensaje + "\n");
    }
}