
package Presentar.Reserva;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;
import ClasesUsuario.SolicitudRecurso;
import ClasesUsuario.User;
import logic.ServiceLocator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControllerReserva {

    private ModelReserva model;

    public ControllerReserva() {
        model = new ModelReserva();
    }

    public ModelReserva getModel() {
        return model;
    }

    public ArrayList<Reserva> getReservas() {
        return new ArrayList<>(ServiceLocator.getService().listarReservas());
    }

    public List<Recurso> getRecursos() {
        return ServiceLocator.getService().listarRecursos();
    }

    public List<CategoriaRecurso> getCategorias() {
        return ServiceLocator.getService().listarCategorias();
    }

    public void recargarDatos() {
        recargarReservas();
    }

    public void agregarSolicitud(SolicitudRecurso solicitud) {
        model.agregarSolicitud(solicitud);
    }

    public void eliminarSolicitud(int indice) {
        model.eliminarSolicitud(indice);
    }

    /**
     * Calcula cuántos recursos de una categoría están disponibles
     * para una fecha y un horario determinados.
     */
    public int consultarDisponibilidad(
            CategoriaRecurso categoria,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin) {

        return consultarDisponibilidad(
                categoria,
                fecha,
                horaInicio,
                horaFin,
                null
        );
    }

    /**
     * Calcula la disponibilidad ignorando una reserva específica.
     * Esto permite editar una reserva sin contar sus propios recursos
     * como si estuvieran ocupados por otra reserva.
     */
    public int consultarDisponibilidad(
            CategoriaRecurso categoria,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            String idReservaExcluir) {

        if (categoria == null
                || fecha == null
                || horaInicio == null
                || horaFin == null
                || !horaInicio.isBefore(horaFin)) {
            return 0;
        }

        String categoriaId = categoria.getVarId();

        List<Recurso> recursos = getRecursos();

        int cantidadTotal = 0;

        for (Recurso recurso : recursos) {
            if (perteneceCategoria(recurso, categoriaId)) {
                cantidadTotal++;
            }
        }

        Set<String> recursosOcupados = obtenerRecursosOcupados(
                fecha,
                horaInicio,
                horaFin,
                idReservaExcluir
        );

        int cantidadOcupada = 0;

        for (Recurso recurso : recursos) {
            if (perteneceCategoria(recurso, categoriaId)
                    && recursosOcupados.contains(recurso.getId())) {
                cantidadOcupada++;
            }
        }

        return Math.max(0, cantidadTotal - cantidadOcupada);
    }

    /**
     * Obtiene los identificadores de los recursos ocupados
     * en el horario indicado.
     */
    private Set<String> obtenerRecursosOcupados(
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            String idReservaExcluir) {

        Set<String> recursosOcupados = new HashSet<>();

        List<Reserva> reservas = getReservas();

        for (Reserva reserva : reservas) {

            if (reserva == null
                    || reserva.getFecha() == null
                    || reserva.getHoraInicio() == null
                    || reserva.getHoraFin() == null) {
                continue;
            }

            // Ignorar la reserva que se está editando.
            if (idReservaExcluir != null
                    && idReservaExcluir.equals(reserva.getId())) {
                continue;
            }

            if (!reserva.getFecha().equals(fecha)) {
                continue;
            }

            boolean seSuperpone =
                    horaInicio.isBefore(reserva.getHoraFin())
                            && horaFin.isAfter(reserva.getHoraInicio());

            if (!seSuperpone) {
                continue;
            }

            if (reserva.getRecursos() != null) {
                for (Recurso recurso : reserva.getRecursos()) {
                    if (recurso != null && recurso.getId() != null) {
                        recursosOcupados.add(recurso.getId());
                    }
                }
            }
        }

        return recursosOcupados;
    }

    private boolean perteneceCategoria(
            Recurso recurso,
            String categoriaId) {

        if (recurso == null || categoriaId == null) {
            return false;
        }

        if (categoriaId.equals(recurso.getCategoriaId())) {
            return true;
        }

        return recurso.getRecurso() != null
                && categoriaId.equals(
                recurso.getRecurso().getVarId()
        );
    }

    /**
     * Selecciona recursos disponibles según las solicitudes.
     * Si se está editando, ignora la reserva indicada.
     */
    private ArrayList<Recurso> seleccionarRecursosDisponibles(
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            ArrayList<SolicitudRecurso> solicitudes,
            String idReservaExcluir) {

        if (solicitudes == null || solicitudes.isEmpty()) {
            return null;
        }

        List<Recurso> recursosDisponibles = getRecursos();

        Set<String> recursosOcupados = obtenerRecursosOcupados(
                fecha,
                horaInicio,
                horaFin,
                idReservaExcluir
        );

        Set<String> recursosSeleccionadosIds = new HashSet<>();

        ArrayList<Recurso> recursosSeleccionados = new ArrayList<>();

        for (SolicitudRecurso solicitud : solicitudes) {

            if (solicitud == null
                    || solicitud.getCategoria() == null
                    || solicitud.getCantidad() <= 0) {
                return null;
            }

            String categoriaId = solicitud.getCategoria().getVarId();

            int cantidadPendiente = solicitud.getCantidad();

            for (Recurso recurso : recursosDisponibles) {

                if (perteneceCategoria(recurso, categoriaId)
                        && recurso.getId() != null
                        && !recursosOcupados.contains(recurso.getId())
                        && !recursosSeleccionadosIds.contains(recurso.getId())
                        && cantidadPendiente > 0) {

                    recursosSeleccionados.add(recurso);
                    recursosSeleccionadosIds.add(recurso.getId());

                    cantidadPendiente--;
                }
            }

            // No existen suficientes recursos disponibles.
            if (cantidadPendiente > 0) {
                return null;
            }
        }

        if (recursosSeleccionados.isEmpty()) {
            return null;
        }

        return recursosSeleccionados;
    }

    /**
     * Crea una reserva nueva.
     */
    public boolean crearReserva(
            User usuario,
            String actividad,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            ArrayList<SolicitudRecurso> solicitudes) {

        if (!datosValidos(
                usuario,
                actividad,
                fecha,
                horaInicio,
                horaFin,
                solicitudes)) {
            return false;
        }

        try {
            ArrayList<Recurso> recursosSeleccionados =
                    seleccionarRecursosDisponibles(
                            fecha,
                            horaInicio,
                            horaFin,
                            solicitudes,
                            null
                    );

            if (recursosSeleccionados == null) {
                return false;
            }

            model.setUsuario(usuario);
            model.setActividad(actividad.trim());
            model.setFecha(fecha);
            model.setHoraInicio(horaInicio);
            model.setHoraFin(horaFin);
            model.setSolicitudes(new ArrayList<>(solicitudes));

            String id = "RES-" + String.format(
                    "%03d",
                    ServiceLocator.getService().listarReservas().size() + 1
            );

            Reserva reserva = new Reserva(
                    id,
                    usuario,
                    actividad.trim(),
                    fecha,
                    horaInicio,
                    horaFin,
                    recursosSeleccionados
            );

            return ServiceLocator.getService().guardarReserva(reserva);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "No fue posible crear la reserva mediante el Backend.",
                    ex
            );
        }
    }

    /**
     * Actualiza una reserva existente conservando su identificador.
     */
    public boolean actualizarReserva(
            String idReserva,
            User usuario,
            String actividad,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            ArrayList<SolicitudRecurso> solicitudes) {

        if (idReserva == null
                || idReserva.trim().isEmpty()
                || !datosValidos(
                usuario,
                actividad,
                fecha,
                horaInicio,
                horaFin,
                solicitudes)) {
            return false;
        }

        try {
            // Seleccionar recursos sin considerar ocupados los de
            // la reserva que estamos editando.
            ArrayList<Recurso> recursosSeleccionados =
                    seleccionarRecursosDisponibles(
                            fecha,
                            horaInicio,
                            horaFin,
                            solicitudes,
                            idReserva
                    );

            if (recursosSeleccionados == null) {
                return false;
            }

            model.setUsuario(usuario);
            model.setActividad(actividad.trim());
            model.setFecha(fecha);
            model.setHoraInicio(horaInicio);
            model.setHoraFin(horaFin);
            model.setSolicitudes(new ArrayList<>(solicitudes));

            // Se conserva el ID original.
            Reserva reserva = new Reserva(
                    idReserva,
                    usuario,
                    actividad.trim(),
                    fecha,
                    horaInicio,
                    horaFin,
                    recursosSeleccionados
            );

            return ServiceLocator.getService().actualizarReserva(reserva);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "No fue posible actualizar la reserva mediante el Backend.",
                    ex
            );
        }
    }

    /**
     * Comprueba que los datos de la reserva sean válidos.
     */
    private boolean datosValidos(
            User usuario,
            String actividad,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            ArrayList<SolicitudRecurso> solicitudes) {

        return usuario != null
                && actividad != null
                && !actividad.trim().isEmpty()
                && fecha != null
                && horaInicio != null
                && horaFin != null
                && solicitudes != null
                && !solicitudes.isEmpty()
                && horaInicio.isBefore(horaFin);
    }

    public void recargarReservas() {
        ServiceLocator.getService().listarReservas();
    }

    public boolean eliminarReserva(String idReserva) {
        return ServiceLocator.getService().eliminarReserva(idReserva);
    }

    public void limpiarModel() {
        model.limpiar();
    }
}
