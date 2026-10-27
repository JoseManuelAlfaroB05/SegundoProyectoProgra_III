package logic;

import ClasesUsuario.Protocol;

import java.io.IOException;

public class ServiceLocator {

    private static Service servicio;

    private ServiceLocator() {
    }

    public static synchronized Service getService() {
        if (servicio == null) {
            servicio = new Service(
                    Protocol.SERVER,
                    Protocol.PORT
            );
        }

        return servicio;
    }

    public static synchronized void conectar() throws IOException {
        getService().conectar();
    }

    public static synchronized void desconectar() {
        if (servicio != null) {
            servicio.desconectar();
            servicio = null;
        }
    }
}