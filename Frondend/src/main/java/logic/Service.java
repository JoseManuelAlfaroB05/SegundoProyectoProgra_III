package logic;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Protocol;
import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;
import ClasesUsuario.ServicioReservas;
import ClasesUsuario.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Service implements ServicioReservas {

    private final String host;
    private final int puerto;

    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;

    public Service(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    public void conectar() throws IOException {
        if (socket != null && socket.isConnected()
                && !socket.isClosed()) {
            return;
        }

        socket = new Socket(host, puerto);

        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();

            entrada = new ObjectInputStream(socket.getInputStream());

        } catch (IOException ex) {
            desconectar();
            throw ex;
        }
    }

    private void verificarConexion() throws IOException {
        if (socket == null || socket.isClosed()
                || !socket.isConnected()
                || entrada == null || salida == null) {
            throw new IOException(
                    "No existe una conexión activa con el Backend.");
        }
    }

    private synchronized int enviarOperacion(
            int operacion, Object... parametros)
            throws IOException, ClassNotFoundException {

        verificarConexion();

        salida.writeInt(operacion);

        for (Object parametro : parametros) {
            salida.writeObject(parametro);
        }

        salida.flush();
        salida.reset();

        return entrada.readInt();
    }

    private synchronized Object solicitarObjeto(
            int operacion, Object... parametros)
            throws IOException, ClassNotFoundException {

        int resultado = enviarOperacion(operacion, parametros);

        if (resultado != Protocol.ERROR_NO_ERROR) {
            throw new IOException(
                    "El Backend no pudo completar la operación "
                            + operacion + ".");
        }

        return entrada.readObject();
    }

    private boolean ejecutarBooleano(
            int operacion, Object... parametros) {

        try {
            int resultado = enviarOperacion(operacion, parametros);
            return resultado == Protocol.ERROR_NO_ERROR;

        } catch (IOException | ClassNotFoundException ex) {
            throw new IllegalStateException(
                    "Error de comunicación con el Backend: "
                            + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> solicitarLista(int operacion) {
        try {
            return (List<T>) solicitarObjeto(operacion);

        } catch (IOException | ClassNotFoundException ex) {
            throw new IllegalStateException(
                    "Error al obtener la lista del Backend: "
                            + ex.getMessage(), ex);
        }
    }

    public void desconectar() {
        try {
            if (salida != null && socket != null
                    && !socket.isClosed()) {
                salida.writeInt(Protocol.DISCONNECT);
                salida.flush();
            }
        } catch (IOException ex) {
            System.err.println(
                    "Error al solicitar la desconexión: "
                            + ex.getMessage());
        }

        try {
            if (entrada != null) {
                entrada.close();
            }
        } catch (IOException ex) {
            System.err.println(
                    "Error al cerrar la entrada: " + ex.getMessage());
        }

        try {
            if (salida != null) {
                salida.close();
            }
        } catch (IOException ex) {
            System.err.println(
                    "Error al cerrar la salida: " + ex.getMessage());
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            System.err.println(
                    "Error al cerrar el socket: " + ex.getMessage());
        }

        entrada = null;
        salida = null;
        socket = null;
    }

    // =============== USUARIOS Y FUNCIONARIOS ===============

    @Override
    public List<User> listarUsuarios() {
        return solicitarLista(Protocol.USUARIO_FINDALL);
    }

    @Override
    public User buscarUsuarioPorId(String id) {
        try {
            return (User) solicitarObjeto(
                    Protocol.USUARIO_READ, id);

        } catch (IOException | ClassNotFoundException ex) {
            throw new IllegalStateException(
                    "Error al buscar el usuario en el Backend: "
                            + ex.getMessage(),
                    ex
            );
        }
    }

    @Override
    public List<User> buscarFuncionarios(String id, String nombre) {
        try {
            return (List<User>) solicitarObjeto(
                    Protocol.USUARIO_BUSCAR_FUNCIONARIOS,
                    id, nombre);

        } catch (IOException | ClassNotFoundException ex) {
            throw new IllegalStateException(
                    "Error al buscar funcionarios: " + ex.getMessage(),
                    ex);
        }
    }

    @Override
    public boolean guardarFuncionario(User funcionario) {
        return ejecutarBooleano(
                Protocol.USUARIO_CREATE, funcionario);
    }

    @Override
    public boolean actualizarFuncionario(User funcionario) {
        return ejecutarBooleano(
                Protocol.USUARIO_UPDATE, funcionario);
    }

    @Override
    public boolean eliminarFuncionario(String id) {
        return ejecutarBooleano(
                Protocol.USUARIO_DELETE, id);
    }

    @Override
    public boolean actualizarClave(String id, String claveNueva) {
        return ejecutarBooleano(
                Protocol.USUARIO_ACTUALIZAR_CLAVE,
                id, claveNueva);
    }

    // =============== CATEGORÍAS ===============

    @Override
    public List<CategoriaRecurso> listarCategorias() {
        return solicitarLista(Protocol.CATEGORIA_FINDALL);
    }

    @Override
    public CategoriaRecurso buscarCategoriaPorId(String id) {
        try {
            return (CategoriaRecurso) solicitarObjeto(
                    Protocol.CATEGORIA_READ, id);

        } catch (IOException | ClassNotFoundException ex) {
            return null;
        }
    }

    @Override
    public boolean guardarCategoria(CategoriaRecurso categoria) {
        return ejecutarBooleano(
                Protocol.CATEGORIA_CREATE, categoria);
    }

    @Override
    public boolean eliminarCategoria(String id) {
        return ejecutarBooleano(
                Protocol.CATEGORIA_DELETE, id);
    }

    // =============== RECURSOS ===============

    @Override
    public List<Recurso> listarRecursos() {
        return solicitarLista(Protocol.RECURSO_FINDALL);
    }

    @Override
    public Recurso buscarRecursoPorId(String id) throws Exception {
        return (Recurso) solicitarObjeto(
                Protocol.RECURSO_READ, id);
    }

    // =============== RESERVAS ===============

    @Override
    public List<Reserva> listarReservas() {
        return solicitarLista(Protocol.RESERVA_FINDALL);
    }

    @Override
    public boolean guardarReserva(Reserva reserva) {
        return ejecutarBooleano(
                Protocol.RESERVA_CREATE, reserva);
    }

    @Override
    public boolean eliminarReserva(String id) {
        return ejecutarBooleano(
                Protocol.RESERVA_DELETE, id);
    }

    @Override
    public boolean eliminarReservasPorRecursos(
            List<String> idsRecursos) {

        return ejecutarBooleano(
                Protocol.RESERVA_DELETE_POR_RECURSOS,
                idsRecursos);
    }

    @Override
    public boolean guardarRecurso(Recurso recurso) {
        return ejecutarBooleano(
                Protocol.RECURSO_CREATE, recurso);
    }

    @Override
    public boolean actualizarRecurso(Recurso recurso) {
        return ejecutarBooleano(
                Protocol.RECURSO_UPDATE, recurso);
    }

    @Override
    public boolean eliminarRecurso(String id) {
        return ejecutarBooleano(
                Protocol.RECURSO_DELETE, id);
    }
}