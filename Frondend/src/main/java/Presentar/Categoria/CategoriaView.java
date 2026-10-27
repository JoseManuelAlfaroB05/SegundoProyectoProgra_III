package Presentar.Categoria;

import ClasesUsuario.CategoriaRecurso;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CategoriaView extends JPanel implements PropertyChangeListener {
    private JPanel PrincipalPanel;
    private JPanel panelBusqueda;
    private JLabel labelBusqueda;
    private JTextField textFieldBusqueda;
    private JButton buttonBuscar;
    private JButton buttonPDF;
    private JPanel panelDatosCategoria;
    private JPanel panelBotonesCategoria;
    private JPanel panelCategoria;
    private JLabel labelTituloCategoria;
    private JLabel labelId;
    private JTextField textFieldId;
    private JLabel labelDescripcion;
    private JTextField textFieldDescripcion;
    private JButton buttonAceptar;
    private JButton buttonBorrar;
    private JButton buttonLimpiar;
    private JPanel panelTabla;
    private JLabel labelTabla;
    private JTable tableCategorias;
    private JScrollPane scrollTabla;

    private ControllerCategoria controller;
    private ModelCategoria model;

    public CategoriaView() {
        controller = new ControllerCategoria();
        model = controller.getModel();

        setLayout(new BorderLayout());
        add(PrincipalPanel, BorderLayout.CENTER);

        model.addPropertyChangeListener(this);

        buttonPDF.setEnabled(false);

        buttonLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.limpiar();
                textFieldBusqueda.setText("");
                tableCategorias.clearSelection();
            }
        });

        buttonBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String descripcion =
                        textFieldBusqueda.getText().trim();

                if (descripcion.isEmpty()) {
                    controller.limpiar();
                    return;
                }

                CategoriaRecurso categoria =
                        controller.buscarCategoria(descripcion);

                if (categoria == null) {
                    controller.limpiar();

                    JOptionPane.showMessageDialog(
                            CategoriaView.this,
                            "No se encontró ninguna categoría con esa descripción.",
                            "Búsqueda",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        buttonBorrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = textFieldId.getText().trim();

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            CategoriaView.this,
                            "Debe seleccionar una categoría para eliminar.",
                            "Eliminar categoría",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                int respuesta = JOptionPane.showConfirmDialog(
                        CategoriaView.this,
                        "¿Está seguro de que desea eliminar esta categoría?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION
                );

                if (respuesta == JOptionPane.YES_OPTION) {
                    boolean eliminado =
                            controller.eliminarCategoria(id);

                    if (eliminado) {
                        JOptionPane.showMessageDialog(
                                CategoriaView.this,
                                "Categoría eliminada correctamente.",
                                "Eliminar categoría",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        textFieldBusqueda.setText("");
                        tableCategorias.clearSelection();
                    } else {
                        JOptionPane.showMessageDialog(
                                CategoriaView.this,
                                "No se pudo eliminar la categoría. "
                                        + "Puede estar asociada a recursos.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });

        buttonAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = textFieldId.getText().trim();
                String descripcion =
                        textFieldDescripcion.getText().trim();

                if (id.isEmpty() || descripcion.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            CategoriaView.this,
                            "Debe completar todos los campos.",
                            "Agregar categoría",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                boolean agregada =
                        controller.agregarCategoria(id, descripcion);

                if (agregada) {
                    JOptionPane.showMessageDialog(
                            CategoriaView.this,
                            "Categoría agregada correctamente.",
                            "Agregar categoría",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    controller.limpiar();
                    textFieldBusqueda.setText("");
                    tableCategorias.clearSelection();
                } else {
                    JOptionPane.showMessageDialog(
                            CategoriaView.this,
                            "No se pudo agregar la categoría. "
                                    + "Verifique que el ID no esté repetido "
                                    + "y que los datos sean válidos.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        tableCategorias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tableCategorias.getSelectedRow();

                if (fila >= 0) {
                    int filaModelo =
                            tableCategorias.convertRowIndexToModel(fila);

                    TableModelCategoria tableModel =
                            (TableModelCategoria) tableCategorias.getModel();

                    CategoriaRecurso categoria =
                            tableModel.getCategoria(filaModelo);

                    model.setCurrent(categoria);
                }
            }
        });
    }

    public void recargarDatos() {
        controller.recargarDatos();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ModelCategoria.LIST.equals(evt.getPropertyName())) {
            int[] cols = {
                    TableModelCategoria.ID,
                    TableModelCategoria.DESCRIPCION
            };

            tableCategorias.setModel(
                    new TableModelCategoria(cols, model.getList())
            );
        }

        if (ModelCategoria.CURRENT.equals(evt.getPropertyName())) {
            CategoriaRecurso categoria = model.getCurrent();

            if (categoria != null) {
                textFieldId.setText(categoria.getVarId());
                textFieldDescripcion.setText(
                        categoria.getDescripcion()
                );
            }
        }
    }
}