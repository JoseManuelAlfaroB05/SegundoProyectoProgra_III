package Presentar.Reserva;

import ClasesUsuario.SolicitudRecurso;
import ClasesUsuario.User;
import Presentar.AbstractModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class ModelReserva extends AbstractModel {

    private User usuario;
    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private ArrayList<SolicitudRecurso> solicitudes;

    public ModelReserva() {
        super();
        solicitudes = new ArrayList<>();
        actividad = "";
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
        firePropertyChange("usuario");
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
        firePropertyChange("actividad");
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
        firePropertyChange("fecha");
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
        firePropertyChange("horaInicio");
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
        firePropertyChange("horaFin");
    }

    public ArrayList<SolicitudRecurso> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(ArrayList<SolicitudRecurso> solicitudes) {
        this.solicitudes = solicitudes;
        firePropertyChange("solicitudes");
    }

    public void agregarSolicitud(SolicitudRecurso solicitud) {
        solicitudes.add(solicitud);
        firePropertyChange("solicitudes");
    }

    public void eliminarSolicitud(int indice) {
        if (indice >= 0 && indice < solicitudes.size()) {
            solicitudes.remove(indice);
            firePropertyChange("solicitudes");
        }
    }

    public void limpiar() {
        usuario = null;
        actividad = "";
        fecha = null;
        horaInicio = null;
        horaFin = null;
        solicitudes.clear();

        firePropertyChange("limpiar");
    }
}