package Presentar.Actividades;

import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ActividadesView extends JPanel
        implements PropertyChangeListener {

    private DatePicker fechaReferencia = new DatePicker();;
    private JTable tabla = new JTable();
    private final ControllerActividades controller =
            new ControllerActividades();
    private final ModelActividades model = controller.getModel();

    public ActividadesView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        fechaReferencia.setDate(LocalDate.now());

        model.addPropertyChangeListener(this);

        add(crearFiltro(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);

        controller.cargarSemana(fechaReferencia.getDate());
    }

    private JPanel crearFiltro() {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 8)
        );

        panel.setBorder(BorderFactory.createTitledBorder("Semana"));
        panel.add(new JLabel("Fecha de referencia"));
        panel.add(fechaReferencia);

        JButton cargar = new JButton("Cargar");
        cargar.addActionListener(e -> cargarSemana());
        panel.add(cargar);

        JButton pdf = new JButton("PDF");
        pdf.setEnabled(false);
        pdf.setToolTipText("Generación de PDF pendiente");
        panel.add(pdf);

        return panel;
    }

    private JScrollPane crearTabla() {
        tabla.setRowHeight(54);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setGridColor(new Color(180, 180, 180));

        return new JScrollPane(tabla);
    }

    private void cargarSemana() {
        LocalDate fecha = fechaReferencia.getDate();

        if (fecha == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una fecha válida.",
                    "Actividades",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            controller.cargarSemana(fecha);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible cargar las actividades. "
                            + "Verifique la conexión con el Backend.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void recargarDatos() {
        try {
            controller.cargarSemana(fechaReferencia.getDate());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible actualizar las actividades.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void actualizarTabla() {
        tabla.setModel(new TableModelActividades(model));

        tabla.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(70);

        for (int i = 1; i < 8; i++) {
            tabla.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(145);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ModelActividades.SEMANA.equals(evt.getPropertyName())) {
            actualizarTabla();
        }
    }
}