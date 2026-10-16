
package data;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;
import ClasesUsuario.Rol;
import ClasesUsuario.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReservaMySQLDao {
    private final Database db;

    public ReservaMySQLDao() {
        db = Database.instance();
    }

    public ArrayList<Reserva> cargar() {
        Map<String, Reserva> reservasPorId = new LinkedHashMap<>();

        String sql = """
                SELECT
                    res.id AS reservaId,
                    res.usuario AS usuarioId,
                    usu.clave AS usuarioClave,
                    usu.rol AS usuarioRol,
                    usu.nombre AS usuarioNombre,
                    usu.telefono AS usuarioTelefono,
                    res.actividad,
                    res.fecha,
                    res.horaInicio,
                    res.horaFin,
                    rr.recurso AS recursoId,
                    rec.categoria AS categoriaId,
                    rec.descripcion AS recursoDescripcion,
                    cat.descripcion AS categoriaDescripcion
                FROM Reserva res
                INNER JOIN Usuario usu ON res.usuario = usu.id
                LEFT JOIN ReservaRecurso rr ON res.id = rr.reserva
                LEFT JOIN Recurso rec ON rr.recurso = rec.id
                LEFT JOIN Categoria cat ON rec.categoria = cat.id
                ORDER BY res.fecha, res.horaInicio
                """;

        try (PreparedStatement stm = db.prepareStatement(sql);
             ResultSet rs = db.executeQuery(stm)) {

            while (rs.next()) {
                String idReserva = rs.getString("reservaId");
                Reserva reserva = reservasPorId.get(idReserva);

                if (reserva == null) {
                    User usuario = new User(
                            rs.getString("usuarioId"),
                            rs.getString("usuarioClave"),
                            Rol.valueOf(rs.getString("usuarioRol")),
                            rs.getString("usuarioNombre"),
                            rs.getString("usuarioTelefono")
                    );

                    Date fechaSQL = rs.getDate("fecha");
                    Time inicioSQL = rs.getTime("horaInicio");
                    Time finSQL = rs.getTime("horaFin");

                    reserva = new Reserva(
                            idReserva,
                            usuario,
                            rs.getString("actividad"),
                            fechaSQL.toLocalDate(),
                            inicioSQL.toLocalTime(),
                            finSQL.toLocalTime(),
                            new ArrayList<>()
                    );

                    reservasPorId.put(idReserva, reserva);
                }

                String recursoId = rs.getString("recursoId");

                if (recursoId != null) {
                    CategoriaRecurso categoria = new CategoriaRecurso(
                            rs.getString("categoriaId"),
                            rs.getString("categoriaDescripcion")
                    );

                    Recurso recurso = new Recurso(
                            recursoId,
                            categoria,
                            rs.getString("recursoDescripcion")
                    );

                    reserva.getRecursos().add(recurso);
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al cargar reservas: " + e.getMessage()
            );
        }

        return new ArrayList<>(reservasPorId.values());
    }

    public boolean guardar(Reserva reserva) {
        String sqlReserva = """
                INSERT INTO Reserva
                    (id, usuario, actividad, fecha, horaInicio, horaFin)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String sqlRecurso = """
                INSERT INTO ReservaRecurso (reserva, recurso)
                VALUES (?, ?)
                """;

        Connection conexion = null;
        boolean autoCommitOriginal = true;

        try {
            conexion = db.getConnection();
            autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            try {
                try (PreparedStatement stm =
                             conexion.prepareStatement(sqlReserva)) {

                    stm.setString(1, reserva.getId());
                    stm.setString(2, reserva.getUsuarioId());
                    stm.setString(3, reserva.getActividad());
                    stm.setDate(4, Date.valueOf(reserva.getFecha()));
                    stm.setTime(5, Time.valueOf(reserva.getHoraInicio()));
                    stm.setTime(6, Time.valueOf(reserva.getHoraFin()));

                    stm.executeUpdate();
                }

                List<String> recursosIds = reserva.getRecursosIds();

                if (recursosIds != null && !recursosIds.isEmpty()) {
                    try (PreparedStatement stm =
                                 conexion.prepareStatement(sqlRecurso)) {

                        for (String recursoId : recursosIds) {
                            stm.setString(1, reserva.getId());
                            stm.setString(2, recursoId);
                            stm.addBatch();
                        }

                        stm.executeBatch();
                    }
                }

                conexion.commit();
                return true;

            } catch (SQLException e) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }

                System.err.println(
                        "Error al guardar reserva: " + e.getMessage()
                );
                return false;

            } finally {
                conexion.setAutoCommit(autoCommitOriginal);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error de conexión al guardar reserva: "
                            + e.getMessage()
            );
            return false;
        }
    }

    public boolean eliminar(String idReserva) {
        String sql = "DELETE FROM Reserva WHERE id = ?";

        try (PreparedStatement stm = db.prepareStatement(sql)) {
            stm.setString(1, idReserva);

            return db.executeUpdate(stm) == 1;

        } catch (SQLException e) {
            System.err.println(
                    "Error al eliminar reserva: " + e.getMessage()
            );
            return false;
        }
    }

    public boolean eliminarPorRecursos(List<String> idsRecursos) {
        if (idsRecursos == null || idsRecursos.isEmpty()) {
            return true;
        }

        StringBuilder sql = new StringBuilder(
                "DELETE res FROM Reserva res "
                        + "INNER JOIN ReservaRecurso rr "
                        + "ON res.id = rr.reserva "
                        + "WHERE rr.recurso IN ("
        );

        for (int i = 0; i < idsRecursos.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }

        sql.append(")");

        try (PreparedStatement stm =
                     db.prepareStatement(sql.toString())) {

            for (int i = 0; i < idsRecursos.size(); i++) {
                stm.setString(i + 1, idsRecursos.get(i));
            }

            db.executeUpdate(stm);
            return true;

        } catch (SQLException e) {
            System.err.println(
                    "Error al eliminar reservas relacionadas: "
                            + e.getMessage()
            );
            return false;
        }
    }
}
