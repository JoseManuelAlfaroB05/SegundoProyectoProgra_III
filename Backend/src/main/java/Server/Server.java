
package Server;

import ClasesUsuario.Protocol;
import ClasesUsuario.User;
import logic.Service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Server {

    private ServerSocket serverSocket;
    private boolean continuar;
    private Service service;
    private final List<Worker> workers;

    public Server() {
        workers = new ArrayList<>();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(Protocol.PORT);
            continuar = true;
            service = Service.instance();

            System.out.println("Servidor iniciado en el puerto " + Protocol.PORT + "...");

            while (continuar) {
                Socket socket = serverSocket.accept();

                System.out.println("Cliente conectado: " + socket.getInetAddress());

                atenderConexion(socket);
            }

        } catch (IOException ex) {
            if (continuar) {
                System.err.println("Error en el servidor: " + ex.getMessage());
            }
        } finally {
            continuar = false;

            synchronized (workers) {
                for (Worker worker : workers) {
                    worker.stop();
                }
                workers.clear();
            }

            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException ex) {
                System.err.println("Error al cerrar el servidor: " + ex.getMessage());
            }
        }
    }

    private void atenderConexion(Socket socket) {
        try {
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            os.flush();

            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());

            int tipoConexion = is.readInt();

            switch (tipoConexion) {

                case Protocol.SYNC: {
                    String sid = UUID.randomUUID().toString();

                    Worker worker = new Worker(this, socket, os, is, sid, service);

                    synchronized (workers) {
                        workers.add(worker);
                    }

                    os.writeObject(sid);
                    os.flush();

                    worker.start();

                    System.out.println("Conexión SYNC registrada: " + sid);
                    break;
                }

                case Protocol.ASYNC: {
                    String sid = (String) is.readObject();
                    User usuario = (User) is.readObject();

                    Worker worker = buscarWorker(sid);

                    if (worker != null) {
                        worker.setAsync(socket, os, is, usuario);
                        System.out.println("Conexión ASYNC asociada: " + sid);
                    } else {
                        socket.close();
                        System.err.println("No se encontró la sesión: " + sid);
                    }
                    break;
                }

                default:
                    socket.close();
                    System.err.println("Tipo de conexión desconocido: " + tipoConexion);
                    break;
            }

        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Error al establecer conexión: " + ex.getMessage());

            try {
                socket.close();
            } catch (IOException error) {
                System.err.println("Error al cerrar socket: " + error.getMessage());
            }
        }
    }

    private Worker buscarWorker(String sid) {
        synchronized (workers) {
            for (Worker worker : workers) {
                if (worker.getSid().equals(sid)) {
                    return worker;
                }
            }
        }
        return null;
    }

    public void notificarTodos(String mensaje) {
        synchronized (workers) {
            for (Worker worker : workers) {
                worker.enviarNotificacion(mensaje);
            }
        }
    }

    public void eliminarWorker(Worker worker) {
        synchronized (workers) {
            workers.remove(worker);
        }
    }

    public void stop() {
        continuar = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            System.err.println("Error al cerrar el servidor: " + ex.getMessage());
        }

        System.out.println("Servidor detenido.");
    }

    public static void main(String[] args) {
        Server servidor = new Server();
        servidor.start();
    }
}
