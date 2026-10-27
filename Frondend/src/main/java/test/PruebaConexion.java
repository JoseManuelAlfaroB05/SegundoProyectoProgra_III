package test;

import ClasesUsuario.Protocol;
import ClasesUsuario.Reserva;
import logic.Service;

import java.util.List;

public class PruebaConexion {

    public static void main(String[] args) {
        Service service = new Service(
                Protocol.SERVER,
                Protocol.PORT);

        try {
            service.conectar();

            System.out.println("Conectado al Backend.");

            List<Reserva> reservas = service.listarReservas();

            System.out.println("Reservas recibidas:");

            for (Reserva reserva : reservas) {
                System.out.println(
                        "ID: " + reserva.getId()
                                + " | Usuario: " + reserva.getUsuarioId()
                                + " | Actividad: " + reserva.getActividad()
                                + " | Fecha: " + reserva.getFecha()
                                + " | Inicio: " + reserva.getHoraInicio()
                                + " | Fin: " + reserva.getHoraFin()
                                + " | Recursos: " + reserva.getRecursosIds());
            }

        } catch (Exception ex) {
            System.err.println(
                    "Error en la prueba: " + ex.getMessage());
            ex.printStackTrace();

        } finally {
            service.desconectar();
        }
    }
}