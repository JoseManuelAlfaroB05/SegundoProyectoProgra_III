package Client;

import logic.Respuesta;
import logic.Solicitud;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TcpClientPrueba {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("127.0.0.1", 20000);
                ObjectOutputStream os =
                        new ObjectOutputStream(socket.getOutputStream())
        ) {
            os.flush();

            try (ObjectInputStream is =
                         new ObjectInputStream(socket.getInputStream())) {

                Solicitud solicitud = new Solicitud("SALIR");

                os.writeObject(solicitud);
                os.flush();

                Respuesta respuesta = (Respuesta) is.readObject();

                System.out.println("Respuesta del servidor: "
                        + respuesta.getResultado());

                System.out.println("Operación exitosa: "
                        + respuesta.isExitoso());
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Error al probar la conexión: "
                    + ex.getMessage());
        }
    }
}