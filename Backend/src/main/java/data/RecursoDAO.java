package data;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Recurso;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {
    private final Database db;

    public RecursoDAO() {
        db = Database.instance();
    }

    public Recurso read(String id) throws Exception {
        String sql = "SELECT r.id AS recurso_id, " +
                "r.descripcion AS recurso_descripcion, " +
                "r.categoria AS categoria_id, " +
                "c.descripcion AS categoria_descripcion " +
                "FROM Recurso r " +
                "LEFT JOIN Categoria c ON r.categoria = c.id " +
                "WHERE r.id = ?";

        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, id);

        try (ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return from(rs);
            }
        }

        throw new Exception("Recurso no existe");
    }

    public List<Recurso> findAll() {
        List<Recurso> recursos = new ArrayList<>();

        String sql = "SELECT r.id AS recurso_id, " +
                "r.descripcion AS recurso_descripcion, " +
                "r.categoria AS categoria_id, " +
                "c.descripcion AS categoria_descripcion " +
                "FROM Recurso r " +
                "LEFT JOIN Categoria c ON r.categoria = c.id " +
                "ORDER BY r.id";

        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {

            while (rs.next()) {
                recursos.add(from(rs));
            }
        } catch (SQLException ex) {
            System.err.println(
                    "Error al listar recursos: " + ex.getMessage());
        }

        return recursos;
    }

    public boolean guardar(Recurso recurso) {
        String sql = "INSERT INTO Recurso (id, categoria, descripcion) " +
                "VALUES (?, ?, ?)";

        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, recurso.getId());
            stm.setString(2, recurso.getCategoriaId());
            stm.setString(3, recurso.getDescripcion());

            return stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println(
                    "Error al guardar recurso: " + ex.getMessage());
            return false;
        }
    }

    public boolean actualizar(Recurso recurso) {
        String sql = "UPDATE Recurso " +
                "SET categoria = ?, descripcion = ? WHERE id = ?";

        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, recurso.getCategoriaId());
            stm.setString(2, recurso.getDescripcion());
            stm.setString(3, recurso.getId());

            return stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println(
                    "Error al actualizar recurso: " + ex.getMessage());
            return false;
        }
    }

    public boolean eliminar(String id) {
        String sql = "DELETE FROM Recurso WHERE id = ?";

        try {
            PreparedStatement stm = db.prepareStatement(sql);
            stm.setString(1, id);

            return stm.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println(
                    "Error al eliminar recurso: " + ex.getMessage());
            return false;
        }
    }

    private Recurso from(ResultSet rs) throws SQLException {
        String id = rs.getString("recurso_id");
        String descripcion = rs.getString("recurso_descripcion");
        String categoriaId = rs.getString("categoria_id");
        String categoriaDescripcion =
                rs.getString("categoria_descripcion");

        CategoriaRecurso categoria = null;

        if (categoriaId != null) {
            categoria = new CategoriaRecurso(
                    categoriaId, categoriaDescripcion);
        }

        Recurso recurso = new Recurso(id, categoria, descripcion);
        recurso.setCategoriaId(categoriaId);

        return recurso;
    }
}