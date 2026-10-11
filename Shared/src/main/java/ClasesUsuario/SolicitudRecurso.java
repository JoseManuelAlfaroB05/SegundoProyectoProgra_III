package ClasesUsuario;

public class SolicitudRecurso {
    private CategoriaRecurso categoria;
    private int cantidad;

    public SolicitudRecurso(CategoriaRecurso categoria, int cantidad) {
        this.categoria = categoria;
        this.cantidad = cantidad;
    }

    public CategoriaRecurso getCategoria() {
        return categoria;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}