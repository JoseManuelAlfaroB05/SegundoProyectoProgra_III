package Presentar.Actividades;

import ClasesUsuario.Reserva;
import ClasesUsuario.ServicioReservas;
import logic.ServiceLocator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class ControllerActividades {
    private final ServicioReservas servicio;
    private final ModelActividades model;

    public ControllerActividades() {
        servicio = ServiceLocator.getService();
        model = new ModelActividades();
    }

    public ModelActividades getModel() {
        return model;
    }

    public void cargarSemana(LocalDate referencia) {
        if (referencia == null) {
            return;
        }

        LocalDate lunes = referencia.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        );

        List<Reserva> reservas = servicio.listarReservas();
        List<Reserva> reservasSemana = new ArrayList<>();

        if (reservas != null) {
            for (Reserva reserva : reservas) {
                if (reserva == null || reserva.getFecha() == null) {
                    continue;
                }

                if (!reserva.getFecha().isBefore(lunes)
                        && reserva.getFecha().isBefore(lunes.plusDays(7))) {
                    reservasSemana.add(reserva);
                }
            }
        }

        model.setSemana(lunes, reservasSemana);
    }

    public void recargarDatos() {
        if (model.getLunes() != null) {
            cargarSemana(model.getLunes());
        }
    }
}