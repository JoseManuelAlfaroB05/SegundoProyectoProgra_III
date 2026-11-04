package ClasesUsuario;

import java.util.List;

public interface ServicioReservas {
    // USUARIOS
    List<User> listarUsuarios();
    User buscarUsuarioPorId(String id);
    List<User> buscarFuncionarios(String id, String nombre);
    boolean guardarFuncionario(User funcionario);
    boolean actualizarFuncionario(User funcionario);
    boolean eliminarFuncionario(String id);
    boolean actualizarClave(String id, String claveNueva);

    // CATEGORÍAS
    List<CategoriaRecurso> listarCategorias();
    CategoriaRecurso buscarCategoriaPorId(String id);
    boolean guardarCategoria(CategoriaRecurso categoria);
    boolean actualizarCategoria(CategoriaRecurso categoria);
    boolean eliminarCategoria(String id);

    // RECURSOS
    List<Recurso> listarRecursos();
    Recurso buscarRecursoPorId(String id) throws Exception;
    boolean guardarRecurso(Recurso recurso);
    boolean actualizarRecurso(Recurso recurso);
    boolean eliminarRecurso(String id);

    // RESERVAS
    List<Reserva> listarReservas();
    boolean guardarReserva(Reserva reserva);
    boolean actualizarReserva(Reserva reserva);
    boolean eliminarReserva(String id);
    boolean eliminarReservasPorRecursos(List<String> idsRecursos);
}