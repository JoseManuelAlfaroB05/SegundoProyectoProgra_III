package Presentar.Recursos;

import ClasesUsuario.Recurso;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModelRecurso extends AbstractTableModel {
    public static final int ID = 0;
    public static final int CATEGORIA = 1;
    public static final int DESCRIPCION = 2;

    private final int[] columns;
    private final List<Recurso> rows;

    public TableModelRecurso(int[] columns, List<Recurso> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return switch (columns[column]) {
            case ID -> "Id";
            case CATEGORIA -> "Categoría";
            default -> "Descripción";
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        Recurso recurso = rows.get(row);

        return switch (columns[column]) {
            case ID -> recurso.getId();
            case CATEGORIA -> recurso.getRecurso() == null
                    ? recurso.getCategoriaId()
                    : recurso.getRecurso().getDescripcion();
            default -> recurso.getDescripcion();
        };
    }
}