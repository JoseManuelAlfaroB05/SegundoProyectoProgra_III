package Presentar.Reserva;

import ClasesUsuario.Reserva;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TableModelReserva extends AbstractTableModel {

    private ArrayList<Reserva> reservas;

    private final String[] columnas = {
            "ID",
            "Actividad",
            "Fecha",
            "Hora inicio",
            "Hora fin",
            "Recursos"
    };

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DateTimeFormatter formatoHora =
            DateTimeFormatter.ofPattern("HH:mm");

    public TableModelReserva() {
        reservas = new ArrayList<>();
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
                return reserva.getId();

            case 1:
                return reserva.getActividad();

            case 2:
                return reserva.getFecha() == null
                        ? ""
                        : reserva.getFecha().format(formatoFecha);

            case 3:
                return reserva.getHoraInicio() == null
                        ? ""
                        : reserva.getHoraInicio().format(formatoHora);

            case 4:
                return reserva.getHoraFin() == null
                        ? ""
                        : reserva.getHoraFin().format(formatoHora);

            case 5:
                return reserva.getRecursos() == null
                        ? 0
                        : reserva.getRecursos().size();

            default:
                return null;
        }
    }

    public void setReservas(ArrayList<Reserva> reservas) {
        this.reservas = reservas == null
                ? new ArrayList<>()
                : new ArrayList<>(reservas);

        fireTableDataChanged();
    }

    public Reserva getReserva(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= reservas.size()) {
            return null;
        }

        return reservas.get(rowIndex);
    }
}