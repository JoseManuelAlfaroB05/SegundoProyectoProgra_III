package ClasesUsuario;

import java.util.ArrayList;

public class Reservas {
    private ArrayList<Reserva> reservas;

    public Reservas() {
        reservas = new ArrayList<>();
    }

    public ArrayList<Reserva> getReservas() {
        return reservas;
    }

    public void agregar(Reserva reserva) {
        reservas.add(reserva);
    }
}