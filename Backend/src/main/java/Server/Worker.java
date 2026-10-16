package Server;

import logic.Solicitud;
import logic.Respuesta;
import logic.Service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Worker {
    private Socket s;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private boolean continuar;
    private Thread hilo;
    private final Service service;

    public Worker(Socket s) {
        try {
            this.s = s;
            os = new ObjectOutputStream(s.getOutputStream());
            os.flush();
            is = new ObjectInputStream(s.getInputStream());
            service = Service.instance();
        } catch (IOException ex) {
            throw new RuntimeException("Error al crear el Worker: " + ex.getMessage(), ex);
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
            System.err.println("Error al cerrar la conexión: " + ex.getMessage());
        }

        System.out.println("Conexión cerrada...");
    }

    public void listen() {
        System.out.println("Worker atendiendo peticiones...");

        try {
            while (continuar) {
                Solicitud solicitud = (Solicitud) is.readObject();

                Respuesta respuesta = procesarSolicitud(solicitud);

                os.writeObject(respuesta);
                os.flush();
                os.reset();

                if ("SALIR".equals(solicitud.getOperacion())) {
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println("El cliente se desconectó.");
        } catch (ClassNotFoundException ex) {
            System.err.println("No se pudo reconocer el objeto recibido: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Error atendiendo al cliente: " + ex.getMessage());
        } finally {
            continuar = false;

            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                System.err.println("Error al cerrar la entrada: " + ex.getMessage());
            }

            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ex) {
                System.err.println("Error al cerrar la salida: " + ex.getMessage());
            }

            try {
                if (s != null && !s.isClosed()) {
                    s.close();
                }
            } catch (IOException ex) {
                System.err.println("Error al cerrar el socket: " + ex.getMessage());
            }

            service.stop();
        }
    }

    private Respuesta procesarSolicitud(Solicitud solicitud) {
        if (solicitud == null) {
            return new Respuesta(false, null, "La solicitud recibida es nula.");
        }

        String operacion = solicitud.getOperacion();

        if ("SALIR".equals(operacion)) {
            return new Respuesta(true, "Conexión finalizada.", null);
        }

        return new Respuesta(
                false,
                null,
                "Operación todavía no implementada: " + operacion
        );
    }
}