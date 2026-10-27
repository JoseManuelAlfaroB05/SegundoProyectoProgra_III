package Presentar.Actividades;

import ClasesUsuario.Reserva;
import Presentar.AbstractModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ModelActividades extends AbstractModel {
    public static final String SEMANA = "semana";

    private LocalDate lunes;
    private List<Reserva> reservas = new ArrayList<>();

    public LocalDate getLunes() {
        return lunes;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setSemana(LocalDate lunes, List<Reserva> reservas) {
        this.lunes = lunes;
        this.reservas = reservas == null
                ? new ArrayList<>()
                : new ArrayList<>(reservas);

        firePropertyChange(SEMANA);
    }
}