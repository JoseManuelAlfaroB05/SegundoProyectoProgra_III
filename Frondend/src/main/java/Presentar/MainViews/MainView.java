package Presentar.MainViews;

import ClasesUsuario.Rol;
import ClasesUsuario.User;
import Presentar.Funcionarios.FuncionariosView;
import Presentar.Recursos.RecursosView;
import Presentar.Reserva.ReservaView;
import Presentar.Calendarizacion.CalendarizacionView;
import Presentar.Categoria.CategoriaView;
import Presentar.Actividades.ActividadesView;

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
    private CalendarizacionView calendarizacionView;
    private CategoriaView categoriaView;
    private ActividadesView actividadesView;

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

        tabbedPane.addTab("Reservas", reservaPanel);

        recursosView = new RecursosView();
        tabbedPane.addTab("Recursos", recursosView);

        actividadesView = new ActividadesView();
        tabbedPane.addTab("Actividades", actividadesView);

        calendarizacionView = new CalendarizacionView();
        tabbedPane.addTab("Calendarización", calendarizacionView);

        if (usuarioLogueado.getVarRol() == Rol.ADMINISTRADOR) {
            FuncionariosView funcionariosView = new FuncionariosView();
            tabbedPane.addTab("Funcionarios", funcionariosView);

            categoriaView = new CategoriaView();
            tabbedPane.addTab("Categorías", categoriaView);
        }

        /*
         * La pestaña Funcionarios solamente estará disponible
         * para usuarios con el rol ADMINISTRADOR.
         */
        if (usuarioLogueado.getVarRol() == Rol.ADMINISTRADOR) {
            FuncionariosView funcionariosView = new FuncionariosView();
            tabbedPane.addTab("Funcionarios", funcionariosView);
        }

        contetPanel.add(tabbedPane, BorderLayout.CENTER);
        principalPanel.add(contetPanel, BorderLayout.CENTER);
    }

    private void cargarVistas() {
        reservaView = new ReservaView(usuarioLogueado);

        reservaPanel.setLayout(new BorderLayout());
        reservaPanel.add(reservaView, BorderLayout.CENTER);
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

                        if (tabbedPane.getSelectedComponent() == recursosView) {
                            recursosView.recargarDatos();
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