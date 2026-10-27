package Presentar.Calendarizacion;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;
import ClasesUsuario.ServicioReservas;
import logic.ServiceLocator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControllerCalendarizacion {
    private final ServicioReservas servicio;
    private final ModelCalendarizacion model;

    public ControllerCalendarizacion() {
        servicio = ServiceLocator.getService();
        model = new ModelCalendarizacion();
    }

    public ModelCalendarizacion getModel() {
        return model;
    }

    public List<CategoriaRecurso> getCategorias() {
        return servicio.listarCategorias();
    }

    public void recargarDatos() {
        // Los datos se consultan al Backend cuando se necesitan.
    }

    public void buscarFechaYCategoria(
            LocalDate fecha,
            CategoriaRecurso categoriaRecurso) {

        model.setFecha(fecha);
        model.setCategoria(categoriaRecurso);

        if (fecha == null || categoriaRecurso == null) {
            model.setReservas(new ArrayList<>());
            return;
        }

        List<Reserva> todasLasReservas = servicio.listarReservas();
        ArrayList<Reserva> reservasFiltradas = new ArrayList<>();

        if (todasLasReservas == null) {
            model.setReservas(reservasFiltradas);
            return;
        }

        String categoriaId = categoriaRecurso.getVarId();

        for (Reserva reserva : todasLasReservas) {
            if (reserva == null || reserva.getFecha() == null) {
                continue;
            }

            if (!reserva.getFecha().equals(fecha)) {
                continue;
            }

            if (contieneCategoria(reserva, categoriaId)) {
                reservasFiltradas.add(reserva);
            }
        }

        model.setReservas(reservasFiltradas);
    }

    private boolean contieneCategoria(Reserva reserva, String categoriaId) {
        if (reserva.getRecursos() == null) {
            return false;
        }

        for (Recurso recurso : reserva.getRecursos()) {
            if (recurso == null) {
                continue;
            }

            if (categoriaId.equalsIgnoreCase(recurso.getCategoriaId())) {
                return true;
            }
        }

        return false;
    }

    public void generarPDF() throws Exception {
        throw new UnsupportedOperationException(
                "La generación de PDF de Calendarización todavía no está adaptada al Proyecto 2.");
    }
}