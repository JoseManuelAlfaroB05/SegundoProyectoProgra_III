package Presentar.Categoria;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.ServicioReservas;
import logic.ServiceLocator;

import java.util.List;

public class ControllerCategoria {
    private final ServicioReservas servicio;
    private final ModelCategoria model;

    public ControllerCategoria() {
        servicio = ServiceLocator.getService();
        model = new ModelCategoria();
        cargarCategorias();
    }

    public ModelCategoria getModel() {
        return model;
    }

    public void cargarCategorias() {
        List<CategoriaRecurso> categorias = servicio.listarCategorias();
        model.setList(categorias);
    }

    public boolean agregarCategoria(String id, String descripcion) {
        if (id == null || descripcion == null) {
            return false;
        }

        id = id.trim();
        descripcion = descripcion.trim();

        if (id.isEmpty() || descripcion.isEmpty()) {
            return false;
        }

        CategoriaRecurso categoria =
                new CategoriaRecurso(id, descripcion);

        boolean resultado = servicio.guardarCategoria(categoria);

        if (resultado) {
            cargarCategorias();
            model.setCurrent(categoria);
        }

        return resultado;
    }

    public CategoriaRecurso buscarCategoria(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return null;
        }

        String filtro = descripcion.trim().toLowerCase();
        List<CategoriaRecurso> categorias = servicio.listarCategorias();

        if (categorias == null) {
            return null;
        }

        for (CategoriaRecurso categoria : categorias) {
            if (categoria != null
                    && categoria.getDescripcion() != null
                    && categoria.getDescripcion()
                    .toLowerCase()
                    .contains(filtro)) {

                model.setCurrent(categoria);
                return categoria;
            }
        }

        return null;
    }

    public boolean eliminarCategoria(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        boolean resultado = servicio.eliminarCategoria(id.trim());

        if (resultado) {
            cargarCategorias();
            limpiar();
        }

        return resultado;
    }

    public void limpiar() {
        model.setCurrent(new CategoriaRecurso("", ""));
    }

    public void recargarDatos() {
        cargarCategorias();
    }
}