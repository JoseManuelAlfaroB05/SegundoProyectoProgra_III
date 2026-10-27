package Presentar.Funcionarios;

import ClasesUsuario.User;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModelFuncionario extends AbstractTableModel {

    private List<User> rows;
    private final int[] cols;

    public static final int ID = 0;
    public static final int NOMBRE = 1;
    public static final int TELEFONO = 2;

    private final String[] colNames = {
            "ID",
            "Nombre",
            "Teléfono"
    };

    public TableModelFuncionario(int[] cols, List<User> rows) {
        this.cols = cols;
        this.rows = rows;
    }

    public void setLista(List<User> lista) {
        this.rows = lista == null ? new ArrayList<>() : lista;
        fireTableDataChanged();
    }

    public User getFuncionario(int fila) {
        if (fila < 0 || fila >= rows.size()) {
            return null;
        }

        return rows.get(fila);
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
        User funcionario = rows.get(row);

        switch (cols[col]) {
            case ID:
                return funcionario.getVarId();

            case NOMBRE:
                return funcionario.getVarNombre();

            case TELEFONO:
                return funcionario.getVarTelefono();

            default:
                return "";
        }
    }
}