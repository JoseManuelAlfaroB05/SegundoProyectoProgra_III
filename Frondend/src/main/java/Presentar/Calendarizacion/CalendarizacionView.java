
package Presentar.Calendarizacion;

import ClasesUsuario.CategoriaRecurso;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class CalendarizacionView extends JPanel implements PropertyChangeListener {

    private JPanel panelPrincipal;
    private JPanel ContentedPanel;
    private JPanel CalendarizacionPanel;
    private JPanel panelFecha;
    private DatePicker datePicker;
    private JComboBox<CategoriaRecurso> comboBox1;
    private JPanel panelCategoria;
    private JButton buttonOK;
    private JButton buttonPDF;
    private JTable table1;
    private JScrollBar scrollBar1;
    private JPanel panelTaba;

    private ControllerCalendarizacion controller;
    private TableModelCalendarizacion tableModelCalendarizacion;

    private static final String TODAS_LAS_CATEGORIAS = "Todas las categorías";

    public CalendarizacionView() {
        setLayout(new BorderLayout());
        add(panelPrincipal, BorderLayout.CENTER);

        controller = new ControllerCalendarizacion();
        controller.getModel().addPropertyChangeListener(this);

        tableModelCalendarizacion = new TableModelCalendarizacion(
                controller.getModel().getReservas()
        );
        table1.setModel(tableModelCalendarizacion);

        configurarComboBox();
        cargarCategorias();

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.buscarFechaYCategoria(
                        getFecha(),
                        getCategoria()
                );
            }
        });

        buttonPDF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarPDF();
            }
        });    }

    private void configurarComboBox() {

        comboBox1.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

                if (value == null) {
                    setText(TODAS_LAS_CATEGORIAS);
                }

                return this;
            }
        });
    }

    private void cargarCategorias() {
        comboBox1.removeAllItems();

        // La opción null representa todas las categorías.
        comboBox1.addItem(null);

        List<CategoriaRecurso> categorias = controller.getCategorias();

        if (categorias == null) {
            return;
        }

        for (CategoriaRecurso categoria : categorias) {
            comboBox1.addItem(categoria);
        }

        // Seleccionar todas las categorías inicialmente.
        comboBox1.setSelectedIndex(0);
    }

    public void recargarDatos() {
        controller.recargarDatos();
        cargarCategorias();

        if (getFecha() != null) {
            controller.buscarFechaYCategoria(
                    getFecha(),
                    getCategoria()
            );
        } else {
            controller.getModel().setReservas(new ArrayList<>());
        }
    }

    public LocalDate getFecha() {
        return datePicker.getDate();
    }

    public CategoriaRecurso getCategoria() {
        return (CategoriaRecurso) comboBox1.getSelectedItem();
    }

    private void generarPDF() {

        if (getFecha() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar una fecha.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar PDF de Calendarización");
        selector.setSelectedFile(
                new File("Calendarizacion.pdf")
        );

        int resultado = selector.showSaveDialog(this);

        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();

        String ruta = archivo.getAbsolutePath();

        if (!ruta.toLowerCase().endsWith(".pdf")) {
            ruta += ".pdf";
        }

        try {
            controller.generarPDF(ruta);

            JOptionPane.showMessageDialog(
                    this,
                    "El PDF se generó correctamente.",
                    "PDF generado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage() != null
                            ? ex.getMessage()
                            : "No se pudo generar el PDF.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("reservas".equals(evt.getPropertyName())) {
            tableModelCalendarizacion.setReservas(
                    controller.getModel().getReservas()
            );
        }
    }
}
