
package Presentar.Reserva;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;
import ClasesUsuario.SolicitudRecurso;
import ClasesUsuario.User;
import PDF.PdfGenerator;
import ReservaIA.ReservaExtraccion;
import ReservaIA.ReservaExtractorManager;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaView extends JPanel implements PropertyChangeListener {

    private JPanel PrincipalPanel;
    private JPanel contetPanel;
    private JPanel ReservaPanel;
    private JPanel JPanelReservaAutomatica;
    private JLabel tabbReservaAutomatica;
    private JTextField textFieldReservaAutomatica;
    private JButton buttonReservaAuntomatica;
    private JPanel JPanelReservaManual;
    private JPanel panelActividad;
    private JLabel labelActividad;
    private JTextField textFieldActividad;
    private JPanel jpanelTiempo;
    private JPanel jpanelFecha;
    private JLabel labelFecha;
    private DatePicker datePicker;
    private JPanel jpanelHoraI;
    private JLabel labelHoraI;
    private TimePicker timePickerInicio;
    private JPanel jpanelHoraF;
    private JLabel labelhoraf;
    private TimePicker timePickerFin;
    private JPanel panelRecursos;
    private JPanel panelSeleccionCategoria;
    private JLabel labelCategoria;
    private JComboBox<CategoriaRecurso> comboBoxCategoria;
    private JLabel labelCantidad;
    private JSpinner spinnerCantidad;
    private JButton buttonAgregarRecurso;
    private JPanel panelRecursosSeleccionados;
    private JLabel labelRecursosSeleccionados;
    private JList<String> listRecursos;
    private JScrollPane scrollRecursos;
    private JButton buttonEliminarRecurso;
    private JPanel panelTable;
    private JTable tableReseravas;
    private JScrollPane scrollTabla;
    private JLabel labelHistorial;
    private JButton buttonAceptar;
    private JButton buttonRechazar;
    private JButton buttonPDF;
    private JButton crearReservaButton;

    private Reserva reservaSeleccionada;
    private ArrayList<Reserva> todasLasReservas;
    private User usuarioLogueado;
    private ControllerReserva controller;
    private ArrayList<SolicitudRecurso> solicitudes;
    private DefaultListModel<String> modeloRecursos;
    private TableModelReserva tableModelReserva;
    private ReservaExtractorManager reservaExtractorManager;

    public ReservaView(User usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        this.controller = new ControllerReserva();
        this.solicitudes = new ArrayList<>();
        this.modeloRecursos = new DefaultListModel<>();

        controller.getModel().addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        add(PrincipalPanel, BorderLayout.CENTER);
        contetPanel.setBackground(new Color(217, 217, 227));

        inicializarCategorias();
        configurarCantidad();
        cargarTabla();
        configurarListeners();

        buttonReservaAuntomatica.setEnabled(true);
        buttonPDF.setEnabled(true);
    }

    private void configurarListeners() {
        buttonAgregarRecurso.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarRecurso();
            }
        });

        buttonReservaAuntomatica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarReservaAutomatica();
            }
        });

        buttonEliminarRecurso.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarRecurso();
            }
        });

        buttonRechazar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarReserva();
            }
        });

        buttonAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearReserva();
            }
        });

        buttonPDF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarPDF();
            }
        });

        tableReseravas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarReserva();
            }
        });
    }

    private void inicializarCategorias() {
        comboBoxCategoria.removeAllItems();

        try {
            for (CategoriaRecurso categoria : controller.getCategorias()) {
                comboBoxCategoria.addItem(categoria);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No fue posible cargar las categorías desde el Backend.\n" + ex.getMessage(), "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarCantidad() {
        SpinnerNumberModel modelo = new SpinnerNumberModel(1, 1, 100, 1);
        spinnerCantidad.setModel(modelo);
    }

    private void agregarRecurso() {
        CategoriaRecurso categoria = (CategoriaRecurso) comboBoxCategoria.getSelectedItem();

        if (categoria == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una categoría.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fecha = datePicker.getDate();
        LocalTime horaInicio = timePickerInicio.getTime();
        LocalTime horaFin = timePickerFin.getTime();

        if (fecha == null || horaInicio == null || horaFin == null) {
            JOptionPane.showMessageDialog(this, "Seleccione primero la fecha y el horario de la reserva.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!horaInicio.isBefore(horaFin)) {
            JOptionPane.showMessageDialog(this, "La hora final debe ser posterior a la hora inicial.", "Horario inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidad = (Integer) spinnerCantidad.getValue();

        for (SolicitudRecurso solicitud : solicitudes) {
            if (solicitud.getCategoria().getVarId().equals(categoria.getVarId())) {
                JOptionPane.showMessageDialog(this, "La categoría ya fue agregada.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            int disponibles = controller.consultarDisponibilidad(categoria, fecha, horaInicio, horaFin, reservaSeleccionada != null ? reservaSeleccionada.getId() : null);

            if (cantidad > disponibles) {
                JOptionPane.showMessageDialog(this, "No hay suficientes recursos disponibles.\n\nCategoría: " + categoria.getDescripcion() + "\nCantidad solicitada: " + cantidad + "\nCantidad disponible: " + disponibles, "Disponibilidad insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }

            SolicitudRecurso solicitud = new SolicitudRecurso(categoria, cantidad);
            solicitudes.add(solicitud);
            modeloRecursos.addElement(categoria.getDescripcion() + " x" + cantidad);
            listRecursos.setModel(modeloRecursos);
            spinnerCantidad.setValue(1);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No fue posible consultar la disponibilidad.\n" + ex.getMessage(), "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarRecurso() {
        int indice = listRecursos.getSelectedIndex();

        if (indice == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un recurso de la lista.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        solicitudes.remove(indice);
        modeloRecursos.remove(indice);
    }

    private void crearReserva() {
        String actividad = textFieldActividad.getText().trim();
        LocalDate fecha = datePicker.getDate();
        LocalTime horaInicio = timePickerInicio.getTime();
        LocalTime horaFin = timePickerFin.getTime();

        if (actividad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la actividad.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (fecha == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una fecha.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (horaInicio == null || horaFin == null) {
            JOptionPane.showMessageDialog(this, "Seleccione la hora de inicio y la hora final.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!horaInicio.isBefore(horaFin)) {
            JOptionPane.showMessageDialog(this, "La hora final debe ser posterior a la hora inicial.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (solicitudes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos una categoría de recurso.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean editando = reservaSeleccionada != null;
            boolean resultado;

            if (editando) {
                resultado = controller.actualizarReserva(reservaSeleccionada.getId(), usuarioLogueado, actividad, fecha, horaInicio, horaFin, new ArrayList<>(solicitudes));
            } else {
                resultado = controller.crearReserva(usuarioLogueado, actividad, fecha, horaInicio, horaFin, new ArrayList<>(solicitudes));
            }

            if (resultado) {
                cargarTabla();
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Reserva creada correctamente.", "Reserva", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo crear la reserva. Verifique los recursos disponibles y los datos ingresados.", "No se pudo crear la reserva", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No fue posible crear la reserva mediante el Backend.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarReserva() {
        int fila = tableReseravas.getSelectedRow();

        if (fila == -1) {
            limpiarCampos();
            return;
        }

        if (tableReseravas.convertRowIndexToModel(fila) < 0) {
            return;
        }

        int filaModelo = tableReseravas.convertRowIndexToModel(fila);
        Reserva reserva = tableModelReserva.getReserva(filaModelo);

        if (reserva == null) {
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea cancelar esta reserva?", "Cancelar reserva", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                boolean resultado = controller.eliminarReserva(reserva.getId());

                if (resultado) {
                    cargarTabla();
                    limpiarCampos();
                    JOptionPane.showMessageDialog(this, "Reserva cancelada correctamente.", "Cancelar reserva", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo cancelar la reserva.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "No fue posible cancelar la reserva mediante el Backend.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void seleccionarReserva() {
        int fila = tableReseravas.getSelectedRow();

        if (fila >= 0) {
            int filaModelo = tableReseravas.convertRowIndexToModel(fila);
            Reserva reserva = tableModelReserva.getReserva(filaModelo);

            if (reserva != null) {
                cargarReservaSeleccionada(reserva);
            }
        }
    }

    private void cargarReservaSeleccionada(Reserva reserva) {
        reservaSeleccionada = reserva;

        textFieldActividad.setText(reserva.getActividad());
        datePicker.setDate(reserva.getFecha());
        timePickerInicio.setTime(reserva.getHoraInicio());
        timePickerFin.setTime(reserva.getHoraFin());

        solicitudes.clear();
        modeloRecursos.clear();

        if (reserva.getRecursos() != null) {
            for (Recurso recurso : reserva.getRecursos()) {
                CategoriaRecurso categoria = recurso.getRecurso();

                if (categoria == null) {
                    continue;
                }

                boolean existe = false;

                for (SolicitudRecurso solicitud : solicitudes) {
                    if (solicitud.getCategoria().getVarId().equals(categoria.getVarId())) {
                        solicitud.setCantidad(solicitud.getCantidad() + 1);
                        existe = true;
                        break;
                    }
                }

                if (!existe) {
                    solicitudes.add(new SolicitudRecurso(categoria, 1));
                }
            }
        }

        for (SolicitudRecurso solicitud : solicitudes) {
            modeloRecursos.addElement(solicitud.getCategoria().getDescripcion() + " x" + solicitud.getCantidad());
        }

        listRecursos.setModel(modeloRecursos);
    }

    private void cargarTabla() {
        try {
            todasLasReservas = controller.getReservas();
            ArrayList<Reserva> reservasUsuario = new ArrayList<>();

            if (usuarioLogueado != null && todasLasReservas != null) {
                for (Reserva reserva : todasLasReservas) {
                    if (usuarioLogueado.getVarId().equals(reserva.getUsuarioId())) {
                        reservasUsuario.add(reserva);
                    }
                }
            }

            if (tableModelReserva == null) {
                tableModelReserva = new TableModelReserva();
                tableReseravas.setModel(tableModelReserva);
            }

            tableModelReserva.setReservas(reservasUsuario);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar las reservas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        controller.limpiarModel();
        reservaSeleccionada = null;

        textFieldReservaAutomatica.setText("");
        textFieldActividad.setText("");

        datePicker.setDate(null);
        timePickerInicio.setTime(null);
        timePickerFin.setTime(null);

        solicitudes.clear();
        modeloRecursos.clear();
        listRecursos.setModel(modeloRecursos);
        spinnerCantidad.setValue(1);

        if (comboBoxCategoria.getItemCount() > 0) {
            comboBoxCategoria.setSelectedIndex(0);
        }

        tableReseravas.clearSelection();
    }

    public void recargarDatos() {
        inicializarCategorias();
        cargarTabla();
    }

    public void recargarTabla() {
        cargarTabla();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("solicitudes")) {
            modeloRecursos.clear();

            for (SolicitudRecurso solicitud : controller.getModel().getSolicitudes()) {
                modeloRecursos.addElement(solicitud.getCategoria().getDescripcion() + " x" + solicitud.getCantidad());
            }

            listRecursos.setModel(modeloRecursos);
        }

        if (evt.getPropertyName().equals("limpiar")) {
            modeloRecursos.clear();
            listRecursos.setModel(modeloRecursos);
        }
    }

    private void generarPDF() {
        if (todasLasReservas == null || todasLasReservas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No existen reservas para generar el PDF.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar registro de reservas");
        selector.setSelectedFile(new java.io.File("Registro de reservas.pdf"));

        int resultado = selector.showSaveDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            java.io.File archivo = selector.getSelectedFile();
            String ruta = archivo.getAbsolutePath();

            if (!ruta.toLowerCase().endsWith(".pdf")) {
                ruta += ".pdf";
            }

            try {
                PdfGenerator.generarPDFReservas(ruta, todasLasReservas);
                JOptionPane.showMessageDialog(this, "El PDF se generó correctamente.", "Registro de reservas", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void procesarReservaAutomatica() {
        String frase = textFieldReservaAutomatica.getText().trim();

        if (frase.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escriba una instrucción para la reserva.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        buttonReservaAuntomatica.setEnabled(false);

        SwingWorker<ReservaExtraccion, Void> worker = new SwingWorker<ReservaExtraccion, Void>() {
            @Override
            protected ReservaExtraccion doInBackground() throws Exception {
                if (reservaExtractorManager == null) {
                    reservaExtractorManager = new ReservaExtractorManager();
                }

                return reservaExtractorManager.extraerReserva(frase, controller.getCategorias());
            }

            @Override
            protected void done() {
                buttonReservaAuntomatica.setEnabled(true);

                try {
                    ReservaExtraccion extraccion = get();

                    if (extraccion == null) {
                        JOptionPane.showMessageDialog(ReservaView.this, "La IA no pudo interpretar la instrucción.", "Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    aplicarExtraccion(extraccion);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ReservaView.this, "No fue posible procesar la instrucción mediante la IA.\n" + ex.getMessage(), "Error de IA", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void aplicarExtraccion(ReservaExtraccion extraccion) {
        if (extraccion.getActividad() != null) {
            textFieldActividad.setText(extraccion.getActividad());
        }

        if (extraccion.getFecha() != null) {
            try {
                datePicker.setDate(LocalDate.parse(extraccion.getFecha()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "La IA devolvió una fecha con formato inválido.", "Fecha inválida", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (extraccion.getHoraInicio() != null) {
            try {
                timePickerInicio.setTime(LocalTime.parse(extraccion.getHoraInicio()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "La IA devolvió una hora de inicio inválida.", "Hora inválida", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (extraccion.getHoraFinal() != null) {
            try {
                timePickerFin.setTime(LocalTime.parse(extraccion.getHoraFinal()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "La IA devolvió una hora final inválida.", "Hora inválida", JOptionPane.WARNING_MESSAGE);
            }
        }

        cargarCategoriasIA(extraccion.getCategoriasRecurso());

        JOptionPane.showMessageDialog(this, "Los datos de la reserva fueron completados.\nRevise la información antes de crear la reserva.", "Reserva automática", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarCategoriasIA(List<String> categoriasIA) {
        if (categoriasIA == null || categoriasIA.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La IA no identificó categorías de recursos. Puede agregarlas manualmente.", "Categorías no identificadas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<CategoriaRecurso> categoriasDisponibles = controller.getCategorias();
        ArrayList<SolicitudRecurso> solicitudesIA = new ArrayList<>();
        DefaultListModel<String> modeloIA = new DefaultListModel<>();

        for (String nombreCategoria : categoriasIA) {
            for (CategoriaRecurso categoria : categoriasDisponibles) {
                if (categoria.getDescripcion().equals(nombreCategoria)) {
                    solicitudesIA.add(new SolicitudRecurso(categoria, 1));
                    modeloIA.addElement(categoria.getDescripcion() + " x1");
                    break;
                }
            }
        }

        if (solicitudesIA.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La IA no encontró categorías válidas en el sistema. Puede agregarlas manualmente.", "Categorías no identificadas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        solicitudes.clear();
        solicitudes.addAll(solicitudesIA);
        modeloRecursos.clear();

        for (int i = 0; i < modeloIA.size(); i++) {
            modeloRecursos.addElement(modeloIA.getElementAt(i));
        }

        listRecursos.setModel(modeloRecursos);
    }
}
