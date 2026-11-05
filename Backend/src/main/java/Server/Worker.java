
package Server;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;
import ClasesUsuario.User;
import ClasesUsuario.Protocol;
import logic.Service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Worker {

    private Socket s;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private volatile boolean continuar;
    private Thread hilo;
    private final Service service;
    private final Server server;
    private final String sid;

    // Conexión asíncrona
    private Socket asyncSocket;
    private ObjectOutputStream asyncOs;
    private ObjectInputStream asyncIs;

    // Usuario que inició sesión
    private volatile User usuario;

    public Worker(Server server, Socket s, ObjectOutputStream os,
                  ObjectInputStream is, String sid, Service service) {

        this.server = server;
        this.s = s;
        this.os = os;
        this.is = is;
        this.sid = sid;
        this.service = service;
    }

    public void start() {
        continuar = true;

        hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                listen();
            }
        });

        hilo.start();
    }

    public void stop() {
        continuar = false;

        cerrarSocket(s);
        cerrarSocket(asyncSocket);

        System.out.println("Conexión cerrada: " + sid);
    }

    public void listen() {
        System.out.println("Worker atendiendo peticiones: " + sid);

        try {
            while (continuar) {
                int operacion = is.readInt();

                System.out.println("Operación: " + operacion);

                try {
                    procesarOperacion(operacion);

                    os.flush();
                    os.reset();

                } catch (IOException ex) {
                    System.err.println(
                            "Error de comunicación al procesar la operación "
                                    + operacion + ": " + ex.getMessage());

                    continuar = false;

                } catch (Exception ex) {
                    System.err.println(
                            "Error al procesar la operación "
                                    + operacion + ": " + ex.getMessage());

                    try {
                        os.writeInt(Protocol.ERROR_ERROR);
                        os.flush();
                    } catch (IOException errorComunicacion) {
                        continuar = false;
                    }
                }
            }

        } catch (IOException ex) {
            if (continuar) {
                System.out.println("El cliente se desconectó: " + sid);
            }

        } finally {
            continuar = false;

            cerrarStreams();
            cerrarSocket(s);
            cerrarStreamsAsync();
            cerrarSocket(asyncSocket);

            server.eliminarWorker(this);

            System.out.println("Worker finalizado: " + sid);
        }
    }

    private void procesarOperacion(int operacion) throws Exception {

        switch (operacion) {

            // USUARIOS

            case Protocol.USUARIO_FINDALL: {
                List<User> usuarios = service.listarUsuarios();

                os.writeInt(Protocol.ERROR_NO_ERROR);
                os.writeObject(usuarios);
                break;
            }

            case Protocol.USUARIO_READ: {
                String id = (String) is.readObject();
                User usuarioEncontrado = service.buscarUsuarioPorId(id);

                if (usuarioEncontrado != null) {
                    os.writeInt(Protocol.ERROR_NO_ERROR);
                    os.writeObject(usuarioEncontrado);
                } else {
                    os.writeInt(Protocol.ERROR_ERROR);
                }
                break;
            }

            case Protocol.USUARIO_CREATE: {
                User usuarioCreado = (User) is.readObject();

                boolean resultado = service.guardarFuncionario(usuarioCreado);

                escribirResultado(resultado);

                if (resultado) {
                    notificarAccion("registró un funcionario");
                }
                break;
            }

            case Protocol.USUARIO_UPDATE: {
                User usuarioActualizado = (User) is.readObject();

                boolean resultado = service.actualizarFuncionario(usuarioActualizado);

                escribirResultado(resultado);
                break;
            }

            case Protocol.USUARIO_DELETE: {
                String id = (String) is.readObject();

                boolean resultado = service.eliminarFuncionario(id);

                escribirResultado(resultado);
                break;
            }

            case Protocol.USUARIO_ACTUALIZAR_CLAVE: {
                String id = (String) is.readObject();
                String claveNueva = (String) is.readObject();

                boolean resultado = service.actualizarClave(id, claveNueva);

                escribirResultado(resultado);
                break;
            }

            case Protocol.USUARIO_BUSCAR_FUNCIONARIOS: {
                String id = (String) is.readObject();
                String nombre = (String) is.readObject();

                List<User> funcionarios = service.buscarFuncionarios(id, nombre);

                os.writeInt(Protocol.ERROR_NO_ERROR);
                os.writeObject(funcionarios);
                break;
            }

            // CATEGORÍAS

            case Protocol.CATEGORIA_FINDALL: {
                List<CategoriaRecurso> categorias = service.listarCategorias();

                os.writeInt(Protocol.ERROR_NO_ERROR);
                os.writeObject(categorias);
                break;
            }

            case Protocol.CATEGORIA_READ: {
                String id = (String) is.readObject();

                CategoriaRecurso categoria = service.buscarCategoriaPorId(id);

                if (categoria != null) {
                    os.writeInt(Protocol.ERROR_NO_ERROR);
                    os.writeObject(categoria);
                } else {
                    os.writeInt(Protocol.ERROR_ERROR);
                }
                break;
            }

            case Protocol.CATEGORIA_CREATE: {
                CategoriaRecurso categoria = (CategoriaRecurso) is.readObject();

                boolean resultado = service.guardarCategoria(categoria);

                escribirResultado(resultado);

                if (resultado) {
                    notificarAccion("creó una categoría");
                }
                break;
            }

            case Protocol.CATEGORIA_UPDATE: {
                CategoriaRecurso categoria = (CategoriaRecurso) is.readObject();

                boolean resultado = service.actualizarCategoria(categoria);

                escribirResultado(resultado);
                break;
            }

            case Protocol.CATEGORIA_DELETE: {
                String id = (String) is.readObject();

                boolean resultado = service.eliminarCategoria(id);

                escribirResultado(resultado);
                break;
            }

            // RECURSOS

            case Protocol.RECURSO_FINDALL: {
                List<Recurso> recursos = service.listarRecursos();

                os.writeInt(Protocol.ERROR_NO_ERROR);
                os.writeObject(recursos);
                break;
            }

            case Protocol.RECURSO_READ: {
                String id = (String) is.readObject();

                Recurso recurso = service.buscarRecursoPorId(id);

                if (recurso != null) {
                    os.writeInt(Protocol.ERROR_NO_ERROR);
                    os.writeObject(recurso);
                } else {
                    os.writeInt(Protocol.ERROR_ERROR);
                }
                break;
            }

            case Protocol.RECURSO_CREATE: {
                Recurso recurso = (Recurso) is.readObject();

                boolean resultado = service.guardarRecurso(recurso);

                escribirResultado(resultado);

                if (resultado) {
                    notificarAccion("registró un recurso");
                }
                break;
            }

            case Protocol.RECURSO_UPDATE: {
                Recurso recurso = (Recurso) is.readObject();

                boolean resultado = service.actualizarRecurso(recurso);

                escribirResultado(resultado);
                break;
            }

            case Protocol.RECURSO_DELETE: {
                String id = (String) is.readObject();

                boolean resultado = service.eliminarRecurso(id);

                escribirResultado(resultado);
                break;
            }

            // RESERVAS

            case Protocol.RESERVA_FINDALL: {
                List<Reserva> reservas = service.listarReservas();

                os.writeInt(Protocol.ERROR_NO_ERROR);
                os.writeObject(reservas);
                break;
            }

            case Protocol.RESERVA_CREATE: {
                Reserva reserva = (Reserva) is.readObject();

                boolean resultado = service.guardarReserva(reserva);

                escribirResultado(resultado);

                if (resultado) {
                    notificarAccion("creó una reserva");
                }
                break;
            }

            case Protocol.RESERVA_UPDATE: {
                Reserva reserva = (Reserva) is.readObject();

                boolean resultado = service.actualizarReserva(reserva);

                escribirResultado(resultado);
                break;
            }

            case Protocol.RESERVA_DELETE: {
                String id = (String) is.readObject();

                boolean resultado = service.eliminarReserva(id);

                escribirResultado(resultado);
                break;
            }

            case Protocol.RESERVA_DELETE_POR_RECURSOS: {
                @SuppressWarnings("unchecked")
                List<String> idsRecursos = (List<String>) is.readObject();

                boolean resultado = service.eliminarReservasPorRecursos(idsRecursos);

                escribirResultado(resultado);
                break;
            }

            // CERRAR CONEXIÓN

            case Protocol.DISCONNECT: {
                continuar = false;
                break;
            }

            default: {
                System.err.println("Operación desconocida: " + operacion);

                os.writeInt(Protocol.ERROR_ERROR);
                break;
            }
        }
    }

    private void escribirResultado(boolean resultado) throws IOException {
        if (resultado) {
            os.writeInt(Protocol.ERROR_NO_ERROR);
        } else {
            os.writeInt(Protocol.ERROR_ERROR);
        }
    }

    // =============== NOTIFICACIONES ===============

    public String getSid() {
        return sid;
    }

    public synchronized void setAsync(Socket asyncSocket,
                                      ObjectOutputStream asyncOs,
                                      ObjectInputStream asyncIs,
                                      User usuario) {

        this.asyncSocket = asyncSocket;
        this.asyncOs = asyncOs;
        this.asyncIs = asyncIs;
        this.usuario = usuario;
    }

    public synchronized void enviarNotificacion(String mensaje) {
        try {
            if (asyncSocket != null && !asyncSocket.isClosed()
                    && asyncOs != null) {

                asyncOs.writeInt(Protocol.DELIVER_NOTIFICACION);
                asyncOs.writeObject(mensaje);
                asyncOs.flush();
                asyncOs.reset();
            }

        } catch (IOException ex) {
            System.err.println(
                    "Error al enviar notificación a " + sid + ": " + ex.getMessage());
        }
    }

    private void notificarAccion(String accion) {
        if (usuario == null) {
            return;
        }

        String nombre = usuario.getVarNombre();

        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = usuario.getVarId();
        }

        String mensaje = nombre + " " + accion + ".";

        server.notificarTodos(mensaje);
    }

    // =============== CIERRE DE CONEXIONES ===============

    private void cerrarStreams() {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException ex) {
            System.err.println("Error al cerrar entrada SYNC: " + ex.getMessage());
        }

        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException ex) {
            System.err.println("Error al cerrar salida SYNC: " + ex.getMessage());
        }
    }

    private void cerrarStreamsAsync() {
        try {
            if (asyncIs != null) {
                asyncIs.close();
            }
        } catch (IOException ex) {
            System.err.println("Error al cerrar entrada ASYNC: " + ex.getMessage());
        }

        try {
            if (asyncOs != null) {
                asyncOs.close();
            }
        } catch (IOException ex) {
            System.err.println("Error al cerrar salida ASYNC: " + ex.getMessage());
        }
    }

    private void cerrarSocket(Socket socket) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            System.err.println("Error al cerrar socket: " + ex.getMessage());
        }
    }
}
