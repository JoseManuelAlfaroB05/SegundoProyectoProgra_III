package data;

import ClasesUsuario.Recurso;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {
    Database db;

    public RecursoDAO() {
        db = Database.instance();
    }

    public Recurso read(String id) throws Exception {
        String sql = "select * from Recurso r where id=?";

        PreparedStatement stm = db.prepareStatement(sql);
        stm.setString(1, id);

        ResultSet rs = db.executeQuery(stm);

        if (rs.next()) {
            return from(rs, "r");
        } else {
            throw new Exception("Recurso no existe");
        }
    }

    public List<Recurso> findAll() {
        List<Recurso> recursos = new ArrayList<Recurso>();

        try {
            String sql = "select * from Recurso r";

            PreparedStatement stm = db.prepareStatement(sql);
            ResultSet rs = db.executeQuery(stm);

            while (rs.next()) {
                recursos.add(from(rs, "r"));
            }
        } catch (SQLException ex) {
        }

        return recursos;
    }

    public Recurso from(ResultSet rs, String alias) {
        try {
            Recurso recurso = new Recurso();

            recurso.setId(rs.getString(alias + ".id"));
            recurso.setDescripcion(rs.getString(alias + ".descripcion"));
            recurso.setCategoriaId(rs.getString(alias + ".categoria"));

            return recurso;
        } catch (SQLException ex) {
            return null;
        }
    }
}