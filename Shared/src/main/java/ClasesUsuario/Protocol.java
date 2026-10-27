package ClasesUsuario;

public class Protocol {
    public static final String SERVER = "localhost";
    public static final int PORT = 20000;

    // USUARIOS
    public static final int USUARIO_FINDALL = 101;
    public static final int USUARIO_READ = 102;
    public static final int USUARIO_CREATE = 103;
    public static final int USUARIO_UPDATE = 104;
    public static final int USUARIO_DELETE = 105;
    public static final int USUARIO_ACTUALIZAR_CLAVE = 106;
    public static final int USUARIO_BUSCAR_FUNCIONARIOS = 107;

    // CATEGORÍAS
    public static final int CATEGORIA_FINDALL = 201;
    public static final int CATEGORIA_READ = 202;
    public static final int CATEGORIA_CREATE = 203;
    public static final int CATEGORIA_DELETE = 204;

    // RECURSOS
    public static final int RECURSO_FINDALL = 301;
    public static final int RECURSO_READ = 302;
    public static final int RECURSO_CREATE = 303;
    public static final int RECURSO_UPDATE = 304;
    public static final int RECURSO_DELETE = 305;

    // RESERVAS
    public static final int RESERVA_FINDALL = 401;
    public static final int RESERVA_CREATE = 402;
    public static final int RESERVA_DELETE = 403;
    public static final int RESERVA_DELETE_POR_RECURSOS = 404;

    // RESULTADOS
    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_ERROR = 1;

    // CONEXIÓN
    public static final int DISCONNECT = 99;
}
