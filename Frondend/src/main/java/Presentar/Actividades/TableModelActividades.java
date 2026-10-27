package Presentar.Actividades;

import ClasesUsuario.Reserva;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TableModelActividades extends AbstractTableModel {
    private final ModelActividades model;
    private final DateTimeFormatter formatoHora =
            DateTimeFormatter.ofPattern("HH:mm");

    public TableModelActividades(ModelActividades model) {
        this.model = model;
    }

    @Override
    public int getRowCount() {
        return 24;
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Hora";
        }

        LocalDate lunes = model.getLunes();

        if (lunes == null) {
            return "";
        }

        return lunes.plusDays(column - 1).toString();
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return formatoHora.format(LocalTime.of(row, 0));
        }

        LocalDate lunes = model.getLunes();

        if (lunes == null) {
            return "";
        }

        LocalDate fecha = lunes.plusDays(column - 1);

        return actividadEn(model.getReservas(), fecha, row);
    }

    private String actividadEn(
            List<Reserva> reservas,
            LocalDate fecha,
            int hora) {

        LocalTime inicioIntervalo = LocalTime.of(hora, 0);
        LocalTime finIntervalo = hora == 23
                ? LocalTime.MAX
                : LocalTime.of(hora + 1, 0);

        for (Reserva reserva : reservas) {
            if (reserva == null
                    || reserva.getFecha() == null
                    || reserva.getHoraInicio() == null
                    || reserva.getHoraFin() == null) {
                continue;
            }

            if (!fecha.equals(reserva.getFecha())) {
                continue;
            }

            boolean seCruza = reserva.getHoraInicio()
                    .isBefore(finIntervalo)
                    && reserva.getHoraFin()
                    .isAfter(inicioIntervalo);

            if (!seCruza) {
                continue;
            }

            String usuario = reserva.getUsuarioId() == null
                    ? ""
                    : reserva.getUsuarioId();

            String actividad = reserva.getActividad() == null
                    ? ""
                    : reserva.getActividad();

            return actividad + " (" + usuario + ")";
        }

        return "";
    }
}