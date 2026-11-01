    package Presentar.Informacion;

    import ClasesUsuario.User;
    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;

    public class InformacionView extends JPanel {

        private JPanel PrincipalPanel;
        private JLabel labelTitulo;
        private JLabel labelId;
        private JLabel labelNombre;
        private JLabel labelTelefono;
        private JLabel labelRol;
        private JButton buttonCerrarSesion;

        private User usuarioLogueado;
        private ControllerInformacion controller;

        public InformacionView(User usuarioLogueado) {
            this.usuarioLogueado = usuarioLogueado;
            controller = new ControllerInformacion();

            setLayout(new BorderLayout());
            add(PrincipalPanel, BorderLayout.CENTER);

            labelId.setText("ID: " + usuarioLogueado.getVarId());
            labelNombre.setText("Nombre: " + usuarioLogueado.getVarNombre());
            labelTelefono.setText("Teléfono: " + usuarioLogueado.getVarTelefono());
            labelRol.setText("Rol: " + usuarioLogueado.getVarRol());

            buttonCerrarSesion.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(InformacionView.this);

                    if (ventana != null) {
                        controller.cerrarSesion(ventana);
                    }
                }
            });
        }
    }