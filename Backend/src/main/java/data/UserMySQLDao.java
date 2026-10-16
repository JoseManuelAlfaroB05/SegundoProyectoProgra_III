
package data;

import ClasesUsuario.Rol;
import ClasesUsuario.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserMySQLDao {
    private final Database db;

    public UserMySQLDao() {
        db = Database.instance();
    }

    public ArrayList<User> listarTodos() {
        ArrayList<User> usuarios = new ArrayList<>();

        String sql = """
                SELECT id, clave, rol, nombre, telefono
                FROM Usuario
                """;

        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = db.executeQuery(stm)) {

            while (rs.next()) {
                usuarios.add(convertirUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al listar usuarios: " + e.getMessage()
            );
        }

        return usuarios;
    }

    public User buscarPorId(String id) {
        String sql = """
                SELECT id, clave, rol, nombre, telefono
                FROM Usuario
                WHERE id = ?
                """;

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, id);

            try (ResultSet rs = db.executeQuery(stm)) {
                if (rs.next()) {
                    return convertirUsuario(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al buscar usuario: " + e.getMessage()
            );
        }

        return null;
    }

    public ArrayList<User> buscarFuncionarios(String id, String nombre) {
        ArrayList<User> funcionarios = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT id, clave, rol, nombre, telefono
                FROM Usuario
                WHERE rol = ?
                """);

        boolean filtrarId = id != null && !id.trim().isEmpty();
        boolean filtrarNombre = nombre != null && !nombre.trim().isEmpty();

        if (filtrarId) {
            sql.append(" AND id = ?");
        }

        if (filtrarNombre) {
            sql.append(" AND LOWER(nombre) LIKE ?");
        }

        try (PreparedStatement stm = db.prepareStatement(sql.toString())) {
            int parametro = 1;

            stm.setString(parametro++, Rol.FUNCIONARIO.name());

            if (filtrarId) {
                stm.setString(parametro++, id.trim());
            }

            if (filtrarNombre) {
                stm.setString(
                        parametro,
                        "%" + nombre.trim().toLowerCase() + "%"
                );
            }

            try (ResultSet rs = db.executeQuery(stm)) {
                while (rs.next()) {
                    funcionarios.add(convertirUsuario(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al buscar funcionarios: " + e.getMessage()
            );
        }

        return funcionarios;
    }

    public boolean guardarFuncionario(User funcionario) {
        String sql = """
                INSERT INTO Usuario
                    (id, clave, rol, nombre, telefono)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, funcionario.getVarId());
            stm.setString(2, funcionario.getVarClave());
            stm.setString(3, funcionario.getVarRol().name());
            stm.setString(4, funcionario.getVarNombre());
            stm.setString(5, funcionario.getVarTelefono());

            return db.executeUpdate(stm) == 1;

        } catch (SQLException e) {
            System.err.println(
                    "Error al guardar funcionario: " + e.getMessage()
            );
            return false;
        }
    }

    public boolean actualizarFuncionario(User funcionario) {
        String sql = """
                UPDATE Usuario
                SET clave = ?, rol = ?, nombre = ?, telefono = ?
                WHERE id = ?
                """;

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, funcionario.getVarClave());
            stm.setString(2, funcionario.getVarRol().name());
            stm.setString(3, funcionario.getVarNombre());
            stm.setString(4, funcionario.getVarTelefono());
            stm.setString(5, funcionario.getVarId());

            return db.executeUpdate(stm) == 1;

        } catch (SQLException e) {
            System.err.println(
                    "Error al actualizar funcionario: " + e.getMessage()
            );
            return false;
        }
    }

    public boolean eliminarFuncionario(String id) {
        String sql = """
                DELETE FROM Usuario
                WHERE id = ? AND rol = ?
                """;

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, id);
            stm.setString(2, Rol.FUNCIONARIO.name());

            return db.executeUpdate(stm) == 1;

        } catch (SQLException e) {
            System.err.println(
                    "Error al eliminar funcionario: " + e.getMessage()
            );
            return false;
        }
    }

    public boolean actualizarClave(String id, String claveNueva) {
        String sql = """
                UPDATE Usuario
                SET clave = ?
                WHERE id = ?
                """;

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, claveNueva);
            stm.setString(2, id);

            return db.executeUpdate(stm) == 1;

        } catch (SQLException e) {
            System.err.println(
                    "Error al actualizar clave: " + e.getMessage()
            );
            return false;
        }
    }

    private User convertirUsuario(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("id"),
                rs.getString("clave"),
                Rol.valueOf(rs.getString("rol")),
                rs.getString("nombre"),
                rs.getString("telefono")
        );
    }
}
