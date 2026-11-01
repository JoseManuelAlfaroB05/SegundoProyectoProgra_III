package Presentar.Estadisticas;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableModelEstadisticas extends AbstractTableModel {

    private final List<Map.Entry<String, Integer>> rows;
    private final String[] columns;

    public TableModelEstadisticas(Map<String, Integer> data, String firstColumn) {
        rows = new ArrayList<>(data.entrySet());
        columns = new String[]{firstColumn, "Cantidad"};
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
        return columns[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        return column == 0
                ? rows.get(row).getKey()
                : rows.get(row).getValue();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}