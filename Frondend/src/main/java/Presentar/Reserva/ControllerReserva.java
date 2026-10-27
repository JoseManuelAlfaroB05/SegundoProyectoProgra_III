package Presentar.Reserva;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;
import ClasesUsuario.SolicitudRecurso;
import ClasesUsuario.User;
import logic.ServiceLocator;


// import Service.PDFService;
// import Service.ReservaIA.ReservaExtraccion;
// import Service.ReservaIA.ReservaExtractor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ControllerReserva {

    private ModelReserva model;

    // private final ReservaExtractor extractor;

    public ControllerReserva() {
        model = new ModelReserva();

        // extractor = new ReservaExtractor();
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

    public boolean crearReserva(
            User usuario,
            String actividad,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            ArrayList<SolicitudRecurso> solicitudes) {

        if (usuario == null
                || actividad == null
                || actividad.trim().isEmpty()
                || fecha == null
                || horaInicio == null
                || horaFin == null
                || solicitudes == null
                || solicitudes.isEmpty()
                || !horaInicio.isBefore(horaFin)) {
            return false;
        }

        try {
            ArrayList<Recurso> recursosSeleccionados = new ArrayList<>();

            List<Recurso> recursosDisponibles =
                    ServiceLocator.getService().listarRecursos();

            for (SolicitudRecurso solicitud : solicitudes) {

                if (solicitud == null
                        || solicitud.getCategoria() == null
                        || solicitud.getCantidad() <= 0) {
                    return false;
                }

                String categoriaId = solicitud.getCategoria().getVarId();
                int cantidadPendiente = solicitud.getCantidad();

                for (Recurso recurso : recursosDisponibles) {

                    if (recurso.getCategoriaId() != null
                            && recurso.getCategoriaId().equals(categoriaId)
                            && cantidadPendiente > 0) {

                        recursosSeleccionados.add(recurso);
                        cantidadPendiente--;
                    }
                }

                if (cantidadPendiente > 0) {
                    return false;
                }
            }

            if (recursosSeleccionados.isEmpty()) {
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

    public void recargarReservas() {
        ServiceLocator.getService().listarReservas();
    }

    public boolean eliminarReserva(String idReserva) {
        return ServiceLocator.getService().eliminarReserva(idReserva);
    }

    public void limpiarModel() {
        model.limpiar();
    }

    // PDF: pendiente de incorporar al proyecto.
    // public void generarPDF() throws Exception {
    //     PDFService.generarPDFReservas(getReservas());
    // }

    // Extracción de reservas mediante IA: pendiente de incorporar.
    // public ReservaExtraccion extraerReserva(String frase) {
    //     return extractor.extraerReserva(
    //             frase,
    //             ServiceLocator.getService().listarCategorias()
    //     );
    // }
}