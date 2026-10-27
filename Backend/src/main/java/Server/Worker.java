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
    private boolean continuar;
    private Thread hilo;
    private final Service service;

    public Worker(Socket s, Service service) {
        try {
            this.s = s;
            this.service = service;

            os = new ObjectOutputStream(s.getOutputStream());
            os.flush();

            is = new ObjectInputStream(s.getInputStream());

        } catch (IOException ex) {
            throw new RuntimeException(
                    "Error al crear el Worker: " + ex.getMessage(), ex);
        }
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

        try {
            if (s != null && !s.isClosed()) {
                s.close();
            }
        } catch (IOException ex) {
            System.err.println(
                    "Error al cerrar la conexión: " + ex.getMessage());
        }

        System.out.println("Conexión cerrada...");
    }

    public void listen() {
        System.out.println("Worker atendiendo peticiones...");

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
                System.out.println("El cliente se desconectó.");
            }

        } finally {
            continuar = false;

            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                System.err.println(
                        "Error al cerrar la entrada: " + ex.getMessage());
            }

            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ex) {
                System.err.println(
                        "Error al cerrar la salida: " + ex.getMessage());
            }

            try {
                if (s != null && !s.isClosed()) {
                    s.close();
                }
            } catch (IOException ex) {
                System.err.println(
                        "Error al cerrar el socket: " + ex.getMessage());
            }

            System.out.println("Conexión cerrada...");
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
                User usuario = service.buscarUsuarioPorId(id);

                if (usuario != null) {
                    os.writeInt(Protocol.ERROR_NO_ERROR);
                    os.writeObject(usuario);
                } else {
                    os.writeInt(Protocol.ERROR_ERROR);
                }
                break;
            }

            case Protocol.USUARIO_CREATE: {
                User usuario = (User) is.readObject();
                boolean resultado =
                        service.guardarFuncionario(usuario);

                escribirResultado(resultado);
                break;
            }

            case Protocol.USUARIO_UPDATE: {
                User usuario = (User) is.readObject();
                boolean resultado =
                        service.actualizarFuncionario(usuario);

                escribirResultado(resultado);
                break;
            }

            case Protocol.USUARIO_DELETE: {
                String id = (String) is.readObject();
                boolean resultado =
                        service.eliminarFuncionario(id);

                escribirResultado(resultado);
                break;
            }

            case Protocol.USUARIO_ACTUALIZAR_CLAVE: {
                String id = (String) is.readObject();
                String claveNueva = (String) is.readObject();

                boolean resultado =
                        service.actualizarClave(id, claveNueva);

                escribirResultado(resultado);
                break;
            }

            case Protocol.USUARIO_BUSCAR_FUNCIONARIOS: {
                String id = (String) is.readObject();
                String nombre = (String) is.readObject();

                List<User> funcionarios =
                        service.buscarFuncionarios(id, nombre);

                os.writeInt(Protocol.ERROR_NO_ERROR);
                os.writeObject(funcionarios);
                break;
            }

            // CATEGORÍAS

            case Protocol.CATEGORIA_FINDALL: {
                List<CategoriaRecurso> categorias =
                        service.listarCategorias();

                os.writeInt(Protocol.ERROR_NO_ERROR);
                os.writeObject(categorias);
                break;
            }

            case Protocol.CATEGORIA_READ: {
                String id = (String) is.readObject();

                CategoriaRecurso categoria =
                        service.buscarCategoriaPorId(id);

                if (categoria != null) {
                    os.writeInt(Protocol.ERROR_NO_ERROR);
                    os.writeObject(categoria);
                } else {
                    os.writeInt(Protocol.ERROR_ERROR);
                }
                break;
            }

            case Protocol.CATEGORIA_CREATE: {
                CategoriaRecurso categoria =
                        (CategoriaRecurso) is.readObject();

                boolean resultado =
                        service.guardarCategoria(categoria);

                escribirResultado(resultado);
                break;
            }

            case Protocol.CATEGORIA_DELETE: {
                String id = (String) is.readObject();

                boolean resultado =
                        service.eliminarCategoria(id);

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

                boolean resultado =
                        service.guardarReserva(reserva);

                escribirResultado(resultado);
                break;
            }

            case Protocol.RESERVA_DELETE: {
                String id = (String) is.readObject();

                boolean resultado =
                        service.eliminarReserva(id);

                escribirResultado(resultado);
                break;
            }

            case Protocol.RESERVA_DELETE_POR_RECURSOS: {
                @SuppressWarnings("unchecked")
                List<String> idsRecursos =
                        (List<String>) is.readObject();

                boolean resultado =
                        service.eliminarReservasPorRecursos(idsRecursos);

                escribirResultado(resultado);
                break;
            }

            // CERRAR CONEXIÓN

            case Protocol.DISCONNECT: {
                continuar = false;
                break;
            }

            default: {
                System.err.println(
                        "Operación desconocida: " + operacion);

                os.writeInt(Protocol.ERROR_ERROR);
                break;
            }
        }
    }

    private void escribirResultado(boolean resultado)
            throws IOException {
        if (resultado) {
            os.writeInt(Protocol.ERROR_NO_ERROR);
        } else {
            os.writeInt(Protocol.ERROR_ERROR);
        }
    }
}