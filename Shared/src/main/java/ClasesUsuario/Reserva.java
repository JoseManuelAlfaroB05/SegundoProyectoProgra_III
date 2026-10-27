package ClasesUsuario;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private User usuario;
    private String usuarioId;
    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private ArrayList<Recurso> recursos;
    private ArrayList<String> recursosIds;

    public Reserva() {
        recursos = new ArrayList<>();
        recursosIds = new ArrayList<>();
    }

    public Reserva(String id, User usuario, String actividad,
                   LocalDate fecha, LocalTime horaInicio,
                   LocalTime horaFin, ArrayList<Recurso> recursos) {
        this.id = id;
        this.usuario = usuario;
        this.usuarioId = usuario != null ? usuario.getVarId() : null;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.recursos = recursos;
        this.recursosIds = new ArrayList<>();

        if (recursos != null) {
            for (Recurso recurso : recursos) {
                this.recursosIds.add(recurso.getId());
            }
        }
    }

    public String getId() {
        return id;
    }

    public User getUsuario() {
        return usuario;
    }

    public String getActividad() {
        return actividad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public ArrayList<Recurso> getRecursos() {
        return recursos;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public ArrayList<String> getRecursosIds() {
        return recursosIds;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
        this.usuarioId = usuario != null ? usuario.getVarId() : null;
    }

    public void setRecursos(ArrayList<Recurso> recursos) {
        this.recursos = recursos;
        this.recursosIds = new ArrayList<>();

        if (recursos != null) {
            for (Recurso recurso : recursos) {
                this.recursosIds.add(recurso.getId());
            }
        }
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }
}