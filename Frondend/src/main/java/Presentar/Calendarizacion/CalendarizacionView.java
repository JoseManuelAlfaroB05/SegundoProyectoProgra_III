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

    public CalendarizacionView() {
        setLayout(new BorderLayout());
        add(panelPrincipal, BorderLayout.CENTER);

        controller = new ControllerCalendarizacion();
        controller.getModel().addPropertyChangeListener(this);

        tableModelCalendarizacion = new TableModelCalendarizacion(
                controller.getModel().getReservas()
        );
        table1.setModel(tableModelCalendarizacion);

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
                try {
                    controller.generarPDF();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            CalendarizacionView.this,
                            ex.getMessage() != null
                                    ? ex.getMessage()
                                    : "No se pudo generar el PDF.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }

    private void cargarCategorias() {
        comboBox1.removeAllItems();

        List<CategoriaRecurso> categorias = controller.getCategorias();

        if (categorias == null) {
            return;
        }

        for (CategoriaRecurso categoria : categorias) {
            comboBox1.addItem(categoria);
        }
    }

    public void recargarDatos() {
        controller.recargarDatos();
        cargarCategorias();

        if (getFecha() != null && getCategoria() != null) {
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("reservas".equals(evt.getPropertyName())) {
            tableModelCalendarizacion.setReservas(
                    controller.getModel().getReservas()
            );
        }
    }
}