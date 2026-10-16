package data;

import ClasesUsuario.CategoriaRecurso;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CategoriaMySQLDao {
    private final Database db;

    public CategoriaMySQLDao() {
        db = Database.instance();
    }

    public ArrayList<CategoriaRecurso> listarTodas() {
        ArrayList<CategoriaRecurso> categorias = new ArrayList<>();

        String sql = """
                SELECT id, descripcion
                FROM Categoria
                ORDER BY descripcion
                """;

        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = db.executeQuery(stm)) {

            while (rs.next()) {
                categorias.add(convertirCategoria(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar categorías: " + e.getMessage());
        }

        return categorias;
    }

    public boolean guardar(CategoriaRecurso categoria) {
        String sql = """
                INSERT INTO Categoria (id, descripcion)
                VALUES (?, ?)
                """;

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, categoria.getVarId());
            stm.setString(2, categoria.getDescripcion());

            return db.executeUpdate(stm) == 1;

        } catch (SQLException e) {
            System.err.println("Error al guardar categoría: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarCategoria(String id) {
        String sql = """
                DELETE FROM Categoria
                WHERE id = ?
                """;

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, id);

            return db.executeUpdate(stm) == 1;

        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }

    public CategoriaRecurso buscarPorId(String id) {
        String sql = """
                SELECT id, descripcion
                FROM Categoria
                WHERE id = ?
                """;

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, id);

            try (ResultSet rs = db.executeQuery(stm)) {
                if (rs.next()) {
                    return convertirCategoria(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar categoría: " + e.getMessage());
        }

        return null;
    }

    private CategoriaRecurso convertirCategoria(ResultSet rs)
            throws SQLException {
        return new CategoriaRecurso(
                rs.getString("id"),
                rs.getString("descripcion")
        );
    }
}