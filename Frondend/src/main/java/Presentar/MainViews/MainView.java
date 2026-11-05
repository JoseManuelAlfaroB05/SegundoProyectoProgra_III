package Presentar.MainViews;

import ClasesUsuario.Rol;
import ClasesUsuario.User;

import Presentar.Actividades.ActividadesView;
import Presentar.Calendarizacion.CalendarizacionView;
import Presentar.Categoria.CategoriaView;
import Presentar.Estadisticas.EstadisticasView;
import Presentar.Funcionarios.FuncionariosView;
import Presentar.Informacion.InformacionView;
import Presentar.Recursos.RecursosView;
import Presentar.Reserva.ReservaView;
import logic.ServiceLocator;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {

    private JPanel principalPanel;
    private JPanel contetPanel;
    private JTabbedPane tabbedPane;
    private JPanel reservaPanel;

    private User usuarioLogueado;

    private ReservaView reservaView;
    private RecursosView recursosView;
    private ActividadesView actividadesView;
    private CalendarizacionView calendarizacionView;
    private InformacionView informacionView;
    private FuncionariosView funcionariosView;
    private CategoriaView categoriaView;
    private EstadisticasView estadisticasView;

    private ControllerMainView controller;

    public MainView(User usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        this.controller = new ControllerMainView(this);

        setTitle("Sistema de Reserva de Recursos - Usuario logueado: "
                + usuarioLogueado.getVarId());

        inicializarComponentes();

        setContentPane(principalPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        cargarVistas();
        configurarPestanas();
        configurarNotificaciones();
    }

    private void inicializarComponentes() {
        principalPanel = new JPanel(new BorderLayout());
        contetPanel = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();

        reservaPanel = new JPanel(new BorderLayout());

        contetPanel.add(tabbedPane, BorderLayout.CENTER);
        principalPanel.add(contetPanel, BorderLayout.CENTER);
    }

    private void cargarVistas() {
        // Pestañas disponibles para ambos roles
        calendarizacionView = new CalendarizacionView();
        actividadesView = new ActividadesView();
        estadisticasView = new EstadisticasView();
        informacionView = new InformacionView(usuarioLogueado);

        tabbedPane.addTab("Calendarización", calendarizacionView);
        tabbedPane.addTab("Actividades", actividadesView);
        tabbedPane.addTab("Estadísticas", estadisticasView);
        tabbedPane.addTab("Información", informacionView);

        // Pestañas exclusivas del funcionario
        if (usuarioLogueado.getVarRol() == Rol.FUNCIONARIO) {
            reservaView = new ReservaView(usuarioLogueado);
            tabbedPane.insertTab("Reservas", null, reservaView, null, 0);
        }

        // Pestañas exclusivas del administrador
        if (usuarioLogueado.getVarRol() == Rol.ADMINISTRADOR) {
            recursosView = new RecursosView();
            categoriaView = new CategoriaView();
            funcionariosView = new FuncionariosView();

            tabbedPane.insertTab("Recursos", null, recursosView, null, 0);
            tabbedPane.insertTab("Categorías", null, categoriaView, null, 0);
            tabbedPane.insertTab("Funcionarios", null, funcionariosView, null, 0);
        }
    }

    private void configurarPestanas() {
        tabbedPane.addChangeListener(
                new javax.swing.event.ChangeListener() {
                    @Override
                    public void stateChanged(
                            javax.swing.event.ChangeEvent e) {

                        if (tabbedPane.getSelectedComponent() == reservaPanel) {
                            controller.recargarReservas();
                        }

                        if (tabbedPane.getSelectedComponent() == calendarizacionView) {
                            calendarizacionView.recargarDatos();
                        }

                        if (tabbedPane.getSelectedComponent() == recursosView) {
                            recursosView.recargarDatos();
                        }

                        if (tabbedPane.getSelectedComponent() == categoriaView
                                && categoriaView != null) {
                            categoriaView.recargarDatos();
                        }

                        if (tabbedPane.getSelectedComponent() == funcionariosView
                                && funcionariosView != null) {
                            funcionariosView.recargarDatos();
                        }

                        if (tabbedPane.getSelectedComponent() == estadisticasView
                                && estadisticasView != null) {
                            estadisticasView.recargarDatos();
                        }
                    }
                }
        );
    }

    public void recargarReservas() {
        reservaView.recargarDatos();
    }

    public User getUsuarioLogueado() {
        return usuarioLogueado;
    }

    private void configurarNotificaciones() {
        ServiceLocator.getService().setNotificacionListener(
                mensaje -> SwingUtilities.invokeLater(
                        () -> informacionView.agregarNotificacion(mensaje)
                )
        );

        try {
            ServiceLocator.getService().conectarNotificaciones(usuarioLogueado);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible conectar el sistema de notificaciones: " + ex.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}