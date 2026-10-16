package logic;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;
import ClasesUsuario.ServicioReservas;
import ClasesUsuario.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Proxi implements ServicioReservas {

    private final String host;
    private final int puerto;

    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;

    public Proxi(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    public void conectar() throws IOException {
        socket = new Socket(host, puerto);

        salida = new ObjectOutputStream(socket.getOutputStream());
        salida.flush();

        entrada = new ObjectInputStream(socket.getInputStream());
    }

    private synchronized Object enviarSolicitud(Object solicitud)
            throws IOException, ClassNotFoundException {
        if (socket == null || socket.isClosed() || !socket.isConnected()) {
            throw new IOException("No existe una conexión activa con el Backend.");
        }

        salida.writeObject(solicitud);
        salida.flush();
        salida.reset();

        return entrada.readObject();
    }

    public void desconectar() {
        try {
            if (entrada != null) {
                entrada.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar la entrada: " + e.getMessage());
        }

        try {
            if (salida != null) {
                salida.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar la salida: " + e.getMessage());
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el socket: " + e.getMessage());
        }
    }

    @Override
    public List<User> listarUsuarios() {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public User buscarUsuarioPorId(String id) {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public List<User> buscarFuncionarios(String id, String nombre) {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public boolean guardarFuncionario(User funcionario) {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public boolean actualizarFuncionario(User funcionario) {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public boolean eliminarFuncionario(String id) {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public boolean actualizarClave(String id, String claveNueva) {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public List<CategoriaRecurso> listarCategorias() {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public CategoriaRecurso buscarCategoriaPorId(String id) {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public boolean guardarCategoria(CategoriaRecurso categoria) {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public boolean eliminarCategoria(String id) {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public List<Recurso> listarRecursos() {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public Recurso buscarRecursoPorId(String id) throws Exception {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public List<Reserva> listarReservas() {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public boolean guardarReserva(Reserva reserva) {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public boolean eliminarReserva(String id) {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }

    @Override
    public boolean eliminarReservasPorRecursos(List<String> idsRecursos) {
        throw new UnsupportedOperationException("Falta definir el protocolo de solicitudes.");
    }
}