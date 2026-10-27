package Presentar.Calendarizacion;

import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class TableModelCalendarizacion extends AbstractTableModel {
    private ArrayList<Reserva> reservas;

    private final String[] columnas = {
            "Hora inicio",
            "Hora fin",
            "Actividad",
            "Usuario",
            "Recurso"
    };

    public TableModelCalendarizacion(ArrayList<Reserva> reservas) {
        this.reservas = reservas == null
                ? new ArrayList<>()
                : reservas;
    }

    @Override
    public int getRowCount() {
        return reservas.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Reserva reserva = reservas.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return reserva.getHoraInicio();

            case 1:
                return reserva.getHoraFin();

            case 2:
                return reserva.getActividad();

            case 3:
                return reserva.getUsuarioId() != null
                        ? reserva.getUsuarioId()
                        : "";

            case 4:
                return obtenerRecursos(reserva);

            default:
                return null;
        }
    }

    private String obtenerRecursos(Reserva reserva) {
        if (reserva.getRecursos() == null) {
            return "";
        }

        StringBuilder recursos = new StringBuilder();

        for (Recurso recurso : reserva.getRecursos()) {
            if (recurso == null) {
                continue;
            }

            if (recursos.length() > 0) {
                recursos.append(", ");
            }

            recursos.append(recurso.getDescripcion());
        }

        return recursos.toString();
    }

    public void setReservas(ArrayList<Reserva> reservas) {
        this.reservas = reservas == null
                ? new ArrayList<>()
                : reservas;
        fireTableDataChanged();
    }

    public Reserva getReserva(int rowIndex) {
        return reservas.get(rowIndex);
    }
}