package data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private static final String PROPERTIES_FILE_NAME = "/database.properties";

    private static class Holder {
        private static final Database INSTANCE = new Database();
    }

    private final String url;
    private final String usuario;
    private final String clave;

    private final ThreadLocal<Connection> conexiones = new ThreadLocal<>();

    private Database() {
        Properties propiedades = new Properties();

        try (InputStream archivo = getClass().getResourceAsStream(
                PROPERTIES_FILE_NAME)) {

            if (archivo == null) {
                throw new IllegalStateException("No se encontro database.properties");
            }

            propiedades.load(archivo);

            String driver = propiedades.getProperty("database_driver");
            String servidor = propiedades.getProperty("database_server");
            String puerto = propiedades.getProperty("database_port");
            String baseDatos = propiedades.getProperty("database_name");

            usuario = propiedades.getProperty("database_user");
            clave = propiedades.getProperty("database_password");

            if (driver == null || servidor == null || puerto == null
                    || baseDatos == null || usuario == null || clave == null) {
                throw new IllegalStateException(
                        "Faltan propiedades en database.properties"
                );
            }

            Class.forName(driver);

            url = "jdbc:mysql://" + servidor + ":" + puerto + "/"
                    + baseDatos + "?serverTimezone=UTC";

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Error al cargar la configuracion de MySQL", e
            );
        }
    }

    public static Database instance() {
        return Holder.INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        Connection conexion = conexiones.get();

        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, clave);
            conexiones.set(conexion);
        }

        return conexion;
    }

    public PreparedStatement prepareStatement(String sentencia)
            throws SQLException {
        return getConnection().prepareStatement(sentencia);
    }

    public int executeUpdate(PreparedStatement sentencia)
            throws SQLException {
        return sentencia.executeUpdate();
    }

    public ResultSet executeQuery(PreparedStatement sentencia)
            throws SQLException {
        return sentencia.executeQuery();
    }

    public void close() throws SQLException {
        Connection conexion = conexiones.get();

        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } finally {
            conexiones.remove();
        }
    }
}
