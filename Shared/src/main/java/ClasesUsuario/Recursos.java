package ClasesUsuario;

import java.util.ArrayList;

public class Recursos {
    private ArrayList<Recurso> recursos;

    public Recursos() {
        recursos = new ArrayList<>();
    }

    public ArrayList<Recurso> getRecursos() {
        return recursos;
    }

    public void agregar(Recurso recurso) {
        recursos.add(recurso);
    }
}