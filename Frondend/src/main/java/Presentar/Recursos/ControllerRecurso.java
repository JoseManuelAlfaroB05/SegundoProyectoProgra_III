package Presentar.Recursos;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Recurso;
import ClasesUsuario.ServicioReservas;
import logic.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

public class ControllerRecurso {
    private final ServicioReservas servicio;
    private final ModelRecurso model;

    public ControllerRecurso() {
        servicio = ServiceLocator.getService();
        model = new ModelRecurso();
        cargarLista("", "");
    }

    public ModelRecurso getModel() {
        return model;
    }

    public List<CategoriaRecurso> getCategorias() {
        return servicio.listarCategorias();
    }

    public void cargarLista(String categoriaId, String descripcion) {
        List<Recurso> recursos = servicio.listarRecursos();
        List<Recurso> filtrados = new ArrayList<>();

        String filtroCategoria = categoriaId == null ? "" : categoriaId.trim();
        String filtroDescripcion = descripcion == null ? "" : descripcion.trim();

        for (Recurso recurso : recursos) {
            boolean coincideCategoria = filtroCategoria.isEmpty()
                    || (recurso.getCategoriaId() != null
                    && recurso.getCategoriaId().equalsIgnoreCase(filtroCategoria));

            boolean coincideDescripcion = filtroDescripcion.isEmpty()
                    || (recurso.getDescripcion() != null
                    && recurso.getDescripcion().toLowerCase()
                    .contains(filtroDescripcion.toLowerCase()));

            if (coincideCategoria && coincideDescripcion) {
                filtrados.add(recurso);
            }
        }

        model.setList(filtrados);
    }

    public boolean guardar(String id, CategoriaRecurso categoria, String descripcion) {
        if (id == null || categoria == null || descripcion == null) {
            return false;
        }

        Recurso recurso = new Recurso(id.trim(), categoria, descripcion.trim());
        boolean guardado = servicio.guardarRecurso(recurso);

        if (guardado) {
            model.setCurrent(recurso);
            cargarLista("", "");
        }

        return guardado;
    }

    public boolean actualizar(String id, CategoriaRecurso categoria, String descripcion) {
        if (id == null || categoria == null || descripcion == null) {
            return false;
        }

        Recurso recurso = new Recurso(id.trim(), categoria, descripcion.trim());
        boolean actualizado = servicio.actualizarRecurso(recurso);

        if (actualizado) {
            model.setCurrent(recurso);
            cargarLista("", "");
        }

        return actualizado;
    }

    public boolean eliminar(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        boolean eliminado = servicio.eliminarRecurso(id.trim());

        if (eliminado) {
            limpiar();
            cargarLista("", "");
        }

        return eliminado;
    }

    public void seleccionar(String id) {
        if (id == null || id.trim().isEmpty()) {
            return;
        }

        try {
            Recurso recurso = servicio.buscarRecursoPorId(id.trim());

            if (recurso != null) {
                model.setCurrent(recurso);
            }
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "No fue posible consultar el recurso en el Backend.",
                    ex
            );
        }
    }

    public void limpiar() {
        model.setCurrent(new Recurso());
    }

    public void generarPDF() throws Exception {
        throw new UnsupportedOperationException(
                "La generación de PDF todavía no está adaptada al Proyecto 2."
        );
    }
}