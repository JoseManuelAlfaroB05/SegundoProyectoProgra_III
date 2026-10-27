package Presentar.Calendarizacion;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Reserva;
import Presentar.AbstractModel;

import java.time.LocalDate;
import java.util.ArrayList;

public class ModelCalendarizacion extends AbstractModel {
    private LocalDate fecha;
    private CategoriaRecurso categoria;
    private ArrayList<Reserva> reservas;

    public ModelCalendarizacion() {
        super();
        reservas = new ArrayList<>();
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
        firePropertyChange("fecha");
    }

    public CategoriaRecurso getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaRecurso categoria) {
        this.categoria = categoria;
        firePropertyChange("categoria");
    }

    public ArrayList<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(ArrayList<Reserva> reservas) {
        this.reservas = reservas == null
                ? new ArrayList<>()
                : reservas;
        firePropertyChange("reservas");
    }
}