package Presentar.Funcionarios;

import ClasesUsuario.User;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FuncionariosView extends JPanel implements PropertyChangeListener {

    private JTextField filtroId;
    private JTextField filtroNombre;

    private JTextField campoId;
    private JTextField campoNombre;
    private JTextField campoTelefono;

    private JTable tablaFuncionarios;

    private JButton botonBuscar;
    private JButton botonGuardar;
    private JButton botonEliminar;
    private JButton botonLimpiar;

    private ControllerFuncionario controller;
    private TableModelFuncionario tableModel;

    public FuncionariosView() {
        inicializarComponentes();

        controller = new ControllerFuncionario();

        tableModel = new TableModelFuncionario(
                new int[]{
                        TableModelFuncionario.ID,
                        TableModelFuncionario.NOMBRE,
                        TableModelFuncionario.TELEFONO
                },
                controller.getModel().getList()
        );

        tablaFuncionarios.setModel(tableModel);
        tablaFuncionarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaFuncionarios.setRowSorter(new TableRowSorter<>(tableModel));

        controller.getModel().addPropertyChangeListener(this);

        configurarEventos();
        configurarSeleccionTabla();
        actualizarTabla();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelContenido = new JPanel(new BorderLayout(10, 10));
        panelContenido.setBorder(
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        );

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new GridBagLayout());
        panelBusqueda.setBorder(
                BorderFactory.createTitledBorder("Buscar funcionarios")
        );

        filtroId = new JTextField(10);
        filtroNombre = new JTextField(18);
        botonBuscar = new JButton("Buscar");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        panelBusqueda.add(new JLabel("ID:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        panelBusqueda.add(filtroId, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        panelBusqueda.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.5;
        panelBusqueda.add(filtroNombre, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        panelBusqueda.add(botonBuscar, gbc);

        // Panel de datos del funcionario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(
                BorderFactory.createTitledBorder("Datos del funcionario")
        );

        campoId = new JTextField(18);
        campoNombre = new JTextField(25);
        campoTelefono = new JTextField(25);

        botonGuardar = new JButton("Guardar / Actualizar");
        botonEliminar = new JButton("Eliminar");
        botonLimpiar = new JButton("Limpiar");

        GridBagConstraints datos = new GridBagConstraints();
        datos.insets = new Insets(5, 5, 5, 5);
        datos.fill = GridBagConstraints.HORIZONTAL;
        datos.anchor = GridBagConstraints.WEST;

        datos.gridx = 0;
        datos.gridy = 0;
        datos.weightx = 0;
        panelFormulario.add(new JLabel("ID:"), datos);

        datos.gridx = 1;
        datos.weightx = 1;
        panelFormulario.add(campoId, datos);

        datos.gridx = 0;
        datos.gridy = 1;
        datos.weightx = 0;
        panelFormulario.add(new JLabel("Nombre:"), datos);

        datos.gridx = 1;
        datos.weightx = 1;
        panelFormulario.add(campoNombre, datos);

        datos.gridx = 0;
        datos.gridy = 2;
        datos.weightx = 0;
        panelFormulario.add(new JLabel("Teléfono:"), datos);

        datos.gridx = 1;
        datos.weightx = 1;
        panelFormulario.add(campoTelefono, datos);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panelBotones.add(botonGuardar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonLimpiar);

        datos.gridx = 1;
        datos.gridy = 3;
        datos.weightx = 1;
        panelFormulario.add(panelBotones, datos);

        // Tabla de funcionarios
        tablaFuncionarios = new JTable();
        tablaFuncionarios.setFillsViewportHeight(true);
        tablaFuncionarios.setAutoCreateRowSorter(true);

        JScrollPane scrollTabla = new JScrollPane(tablaFuncionarios);
        scrollTabla.setBorder(
                BorderFactory.createTitledBorder("Funcionarios registrados")
        );

        panelContenido.add(panelBusqueda, BorderLayout.NORTH);
        panelContenido.add(panelFormulario, BorderLayout.CENTER);
        panelContenido.add(scrollTabla, BorderLayout.SOUTH);

        // La tabla debe tener espacio suficiente para mostrarse.
        panelContenido.add(scrollTabla, BorderLayout.SOUTH);

        add(panelContenido, BorderLayout.CENTER);

        // Distribuye el espacio para que la tabla sea visible.
        panelContenido.setPreferredSize(new Dimension(760, 520));
        scrollTabla.setPreferredSize(new Dimension(700, 230));
    }

    private void configurarEventos() {
        botonBuscar.addActionListener(e -> ejecutarBusqueda());

        botonGuardar.addActionListener(e -> guardarOActualizar());

        botonEliminar.addActionListener(e -> eliminarFuncionario());

        botonLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void configurarSeleccionTabla() {
        tablaFuncionarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaFuncionarios.getSelectedRow();

                if (fila >= 0) {
                    int filaModelo = tablaFuncionarios.convertRowIndexToModel(fila);
                    User funcionario = tableModel.getFuncionario(filaModelo);

                    if (funcionario != null) {
                        campoId.setText(funcionario.getVarId());
                        campoNombre.setText(funcionario.getVarNombre());
                        campoTelefono.setText(funcionario.getVarTelefono());

                        // El ID existente no se modifica al actualizar.
                        campoId.setEditable(false);
                    }
                }
            }
        });
    }

    private void ejecutarBusqueda() {
        try {
            controller.buscar(
                    filtroId.getText().trim(),
                    filtroNombre.getText().trim()
            );
        } catch (RuntimeException ex) {
            mostrarError("No se pudo buscar a los funcionarios.", ex);
        }
    }

    private void guardarOActualizar() {
        String id = campoId.getText().trim();
        String nombre = campoNombre.getText().trim();
        String telefono = campoTelefono.getText().trim();

        if (id.isEmpty() || nombre.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes completar todos los campos.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            boolean actualizar = !campoId.isEditable();
            boolean resultado;

            if (actualizar) {
                resultado = controller.actualizar(id, nombre, telefono);
            } else {
                resultado = controller.guardar(id, nombre, telefono);
            }

            if (resultado) {
                JOptionPane.showMessageDialog(
                        this,
                        actualizar
                                ? "Funcionario actualizado correctamente."
                                : "Funcionario registrado correctamente."
                );

                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo completar la operación. Verifica los datos y que el ID no esté duplicado.",
                        "Operación no realizada",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        } catch (RuntimeException ex) {
            mostrarError("Ocurrió un error al guardar el funcionario.", ex);
        }
    }

    private void eliminarFuncionario() {
        String id = campoId.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un funcionario de la tabla.",
                    "Funcionario no seleccionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar al funcionario " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (controller.eliminar(id)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Funcionario eliminado correctamente."
                );

                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar el funcionario."
                );
            }
        } catch (RuntimeException ex) {
            mostrarError("Ocurrió un error al eliminar el funcionario.", ex);
        }
    }

    private void limpiarCampos() {
        controller.limpiar();

        tablaFuncionarios.clearSelection();

        filtroId.setText("");
        filtroNombre.setText("");
        campoId.setText("");
        campoNombre.setText("");
        campoTelefono.setText("");

        campoId.setEditable(true);
    }

    private void actualizarTabla() {
        if (tableModel != null && controller != null) {
            tableModel.setLista(controller.getModel().getList());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("list".equals(evt.getPropertyName())) {
            actualizarTabla();
        }

        if ("current".equals(evt.getPropertyName())) {
            User funcionario = (User) evt.getNewValue();

            if (funcionario != null) {
                campoId.setText(funcionario.getVarId());
                campoNombre.setText(funcionario.getVarNombre());
                campoTelefono.setText(funcionario.getVarTelefono());
            }
        }
    }

    private void mostrarError(String mensaje, RuntimeException ex) {
        JOptionPane.showMessageDialog(
                this,
                mensaje + "\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}