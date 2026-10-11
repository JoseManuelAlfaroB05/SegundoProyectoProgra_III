package ClasesUsuario;

import java.util.ArrayList;

public class Categorias {
    private ArrayList<CategoriaRecurso> categorias;

    public Categorias() {
        categorias = new ArrayList<>();
    }

    public ArrayList<CategoriaRecurso> getCategorias() {
        return categorias;
    }

    public void agregar(CategoriaRecurso categoria) {
        categorias.add(categoria);
    }
}