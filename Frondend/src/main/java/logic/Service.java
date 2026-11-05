package logic;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Protocol;
import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;
import ClasesUsuario.ServicioReservas;
import ClasesUsuario.User;
import java.util.function.Consumer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Service implements ServicioReservas {

    private final String host;
    private final int puerto;

    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String sid;
    // Conexión ASYNC
    private Socket asyncSocket;
    private ObjectOutputStream asyncSalida;
    private ObjectInputStream asyncEntrada;
    private Thread hiloNotificaciones;
    // Control de notificaciones
    private volatile boolean escuchandoNotificaciones;
    private volatile Consumer<String> notificacionListener;

    public Service(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    public void conectar() throws IOException {
        if (socket != null && socket.isConnected() && !socket.isClosed()) {
            return;
        }

        socket = new Socket(host, puerto);

        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();

            entrada = new ObjectInputStream(socket.getInputStream());

            // Identificar esta conexión como SYNC.
            salida.writeInt(Protocol.SYNC);
            salida.flush();

            // Recibir el identificador de sesión asignado por el Backend.
            sid = (String) entrada.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            desconectar();
            throw new IOException(
                    "No fue posible establecer la conexión SYNC: " + ex.getMessage(), ex);
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
        cerrarConexionAsync();
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
        sid = null;
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
                            + ex.getMessage(), ex);
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
    public boolean actualizarReserva(Reserva reserva) {
        return ejecutarBooleano(
                Protocol.RESERVA_UPDATE, reserva);
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
    public boolean actualizarCategoria(CategoriaRecurso categoria) {
        return ejecutarBooleano(Protocol.CATEGORIA_UPDATE, categoria);
    }

    public String getSid() {
        return sid;
    }

    public void conectarNotificaciones(User usuario) throws IOException {
        verificarConexion();

        if (sid == null) {
            throw new IOException("No existe un identificador de sesión.");
        }

        if (asyncSocket != null && asyncSocket.isConnected()
                && !asyncSocket.isClosed()) {
            return;
        }

        try {
            asyncSocket = new Socket(host, puerto);

            asyncSalida = new ObjectOutputStream(asyncSocket.getOutputStream());
            asyncSalida.flush();

            asyncEntrada = new ObjectInputStream(asyncSocket.getInputStream());

            // Identificar la conexión como ASYNC.
            asyncSalida.writeInt(Protocol.ASYNC);

            // Enviar los datos para asociar la conexión con su Worker.
            asyncSalida.writeObject(sid);
            asyncSalida.writeObject(usuario);

            asyncSalida.flush();
            asyncSalida.reset();

            escucharNotificaciones();

        } catch (IOException ex) {
            cerrarConexionAsync();
            throw new IOException(
                    "No fue posible establecer la conexión ASYNC: " + ex.getMessage(), ex);
        }
    }

    private void escucharNotificaciones() {
        escuchandoNotificaciones = true;

        hiloNotificaciones = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (escuchandoNotificaciones) {
                        int operacion = asyncEntrada.readInt();

                        if (operacion == Protocol.DELIVER_NOTIFICACION) {
                            String mensaje = (String) asyncEntrada.readObject();

                            Consumer<String> listener = notificacionListener;

                            if (listener != null) {
                                listener.accept(mensaje);
                            }
                        }
                    }

                } catch (IOException | ClassNotFoundException ex) {
                    if (escuchandoNotificaciones) {
                        System.err.println(
                                "Se perdió la conexión de notificaciones: " + ex.getMessage());
                    }
                } finally {
                    escuchandoNotificaciones = false;
                }
            }
        });

        hiloNotificaciones.setDaemon(true);
        hiloNotificaciones.start();
    }
    public void setNotificacionListener(Consumer<String> listener) {
        this.notificacionListener = listener;
    }

    private void cerrarConexionAsync() {
        escuchandoNotificaciones = false;

        try {
            if (asyncSocket != null && !asyncSocket.isClosed()) {
                asyncSocket.close();
            }
        } catch (IOException ex) {
            System.err.println("Error al cerrar el socket ASYNC: " + ex.getMessage());
        }

        try {
            if (asyncEntrada != null) {
                asyncEntrada.close();
            }
        } catch (IOException ex) {
            System.err.println("Error al cerrar la entrada ASYNC: " + ex.getMessage());
        }

        try {
            if (asyncSalida != null) {
                asyncSalida.close();
            }
        } catch (IOException ex) {
            System.err.println("Error al cerrar la salida ASYNC: " + ex.getMessage());
        }

        asyncSocket = null;
        asyncEntrada = null;
        asyncSalida = null;
        hiloNotificaciones = null;
    }
}