package Presentar.Categoria;

import ClasesUsuario.CategoriaRecurso;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModelCategoria extends AbstractTableModel {
    private List<CategoriaRecurso> rows;
    private final int[] cols;

    private final String[] colNames = {
            "ID",
            "Descripción"
    };

    public static final int ID = 0;
    public static final int DESCRIPCION = 1;

    public TableModelCategoria(
            int[] cols,
            List<CategoriaRecurso> rows) {

        this.cols = cols;
        this.rows = rows == null
                ? new ArrayList<>()
                : rows;
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public String getColumnName(int col) {
        return colNames[cols[col]];
    }

    @Override
    public Object getValueAt(int row, int col) {
        CategoriaRecurso categoria = rows.get(row);

        switch (cols[col]) {
            case ID:
                return categoria.getVarId();

            case DESCRIPCION:
                return categoria.getDescripcion();

            default:
                return "";
        }
    }

    public CategoriaRecurso getCategoria(int rowIndex) {
        return rows.get(rowIndex);
    }
}