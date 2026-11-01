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

        // 1. Reservas
        reservaView = new ReservaView(usuarioLogueado);
        reservaPanel.add(reservaView, BorderLayout.CENTER);
        tabbedPane.addTab("Reservas", reservaPanel);

        // 2. Calendarización
        calendarizacionView = new CalendarizacionView();
        tabbedPane.addTab("Calendarización", calendarizacionView);

        // 3. Recursos
        recursosView = new RecursosView();
        tabbedPane.addTab("Recursos", recursosView);

        // Pestañas exclusivas del administrador
        if (usuarioLogueado.getVarRol() == Rol.ADMINISTRADOR) {

            // 4. Categorías
            categoriaView = new CategoriaView();
            tabbedPane.addTab("Categorías", categoriaView);

            // 5. Funcionarios
            funcionariosView = new FuncionariosView();
            tabbedPane.addTab("Funcionarios", funcionariosView);

            // 6. Estadísticas
            estadisticasView = new EstadisticasView();
            tabbedPane.addTab("Estadísticas", estadisticasView);
        }

        // 7. Información
        informacionView = new InformacionView(usuarioLogueado);
        tabbedPane.addTab("Información", informacionView);
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
}