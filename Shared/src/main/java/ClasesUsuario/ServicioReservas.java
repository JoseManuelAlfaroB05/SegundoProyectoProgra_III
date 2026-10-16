package ClasesUsuario;

import java.util.List;

public interface ServicioReservas {
    // Usuarios y funcionarios
    List<User> listarUsuarios();
    User buscarUsuarioPorId(String id);
    List<User> buscarFuncionarios(String id, String nombre);
    boolean guardarFuncionario(User funcionario);
    boolean actualizarFuncionario(User funcionario);
    boolean eliminarFuncionario(String id);
    boolean actualizarClave(String id, String claveNueva);

    // Categorías
    List<CategoriaRecurso> listarCategorias();
    CategoriaRecurso buscarCategoriaPorId(String id);
    boolean guardarCategoria(CategoriaRecurso categoria);
    boolean eliminarCategoria(String id);

    // Recursos
    List<Recurso> listarRecursos();
    Recurso buscarRecursoPorId(String id) throws Exception;

    // Reservas
    List<Reserva> listarReservas();
    boolean guardarReserva(Reserva reserva);
    boolean eliminarReserva(String id);
    boolean eliminarReservasPorRecursos(List<String> idsRecursos);
}