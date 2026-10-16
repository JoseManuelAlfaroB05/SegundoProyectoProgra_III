package logic;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;
import ClasesUsuario.ServicioReservas;
import ClasesUsuario.User;
import data.Database;
import data.UserMySQLDao;
import data.CategoriaMySQLDao;
import data.RecursoDAO;
import data.ReservaMySQLDao;

import java.util.List;

public class Service implements ServicioReservas {

    private static class Holder {
        private static final Service INSTANCE = new Service();
    }

    private final UserMySQLDao userDao;
    private final CategoriaMySQLDao categoriaDao;
        private final RecursoDAO recursoDao;
    private final ReservaMySQLDao reservaDao;

    private Service() {
        userDao = new UserMySQLDao();
        categoriaDao = new CategoriaMySQLDao();
        recursoDao = new RecursoDAO();
        reservaDao = new ReservaMySQLDao();
    }

    public static Service instance() {
        return Holder.INSTANCE;
    }

    public void stop() {
        try {
            Database.instance().close();
        } catch (Exception e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    // =============== USUARIOS Y FUNCIONARIOS ===============

    @Override
    public List<User> listarUsuarios() {
        return userDao.listarTodos();
    }

    @Override
    public User buscarUsuarioPorId(String id) {
        return userDao.buscarPorId(id);
    }

    @Override
    public List<User> buscarFuncionarios(String id, String nombre) {
        return userDao.buscarFuncionarios(id, nombre);
    }

    @Override
    public boolean guardarFuncionario(User funcionario) {
        return userDao.guardarFuncionario(funcionario);
    }

    @Override
    public boolean actualizarFuncionario(User funcionario) {
        return userDao.actualizarFuncionario(funcionario);
    }

    @Override
    public boolean eliminarFuncionario(String id) {
        return userDao.eliminarFuncionario(id);
    }

    @Override
    public boolean actualizarClave(String id, String claveNueva) {
        return userDao.actualizarClave(id, claveNueva);
    }

    // =============== CATEGORÍAS ===============

    @Override
    public List<CategoriaRecurso> listarCategorias() {
        return categoriaDao.listarTodas();
    }

    @Override
    public CategoriaRecurso buscarCategoriaPorId(String id) {
        return categoriaDao.buscarPorId(id);
    }

    @Override
    public boolean guardarCategoria(CategoriaRecurso categoria) {
        return categoriaDao.guardar(categoria);
    }

    @Override
    public boolean eliminarCategoria(String id) {
        return categoriaDao.eliminarCategoria(id);
    }

    // =============== RECURSOS ===============

    @Override
    public List<Recurso> listarRecursos() {
        return recursoDao.findAll();
    }

    @Override
    public Recurso buscarRecursoPorId(String id) throws Exception {
        return recursoDao.read(id);
    }

    // =============== RESERVAS ===============

    @Override
    public List<Reserva> listarReservas() {
        return reservaDao.cargar();
    }

    @Override
    public boolean guardarReserva(Reserva reserva) {
        return reservaDao.guardar(reserva);
    }

    @Override
    public boolean eliminarReserva(String id) {
        return reservaDao.eliminar(id);
    }

    @Override
    public boolean eliminarReservasPorRecursos(List<String> idsRecursos) {
        return reservaDao.eliminarPorRecursos(idsRecursos);
    }
}