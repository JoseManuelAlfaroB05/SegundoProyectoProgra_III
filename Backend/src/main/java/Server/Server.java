package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private boolean continuar;

    public void start() {
        try {
            serverSocket = new ServerSocket(20000);
            continuar = true;

            System.out.println("Servidor iniciado en el puerto 20000...");

            while (continuar) {
                Socket socket = serverSocket.accept();

                System.out.println("Cliente conectado: "
                        + socket.getInetAddress());

                Worker worker = new Worker(socket);
                worker.start();
            }
        } catch (IOException ex) {
            if (continuar) {
                System.err.println("Error en el servidor: "
                        + ex.getMessage());
            }
        }
    }

    public void stop() {
        continuar = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            System.err.println("Error al cerrar el servidor: "
                    + ex.getMessage());
        }

        System.out.println("Servidor detenido.");
    }

    public static void main(String[] args) {
        Server servidor = new Server();
        servidor.start();
    }
}