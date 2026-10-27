package Presentar.Recursos;

import Presentar.AbstractModel;
import ClasesUsuario.Recurso;
import java.util.ArrayList;
import java.util.List;

public class ModelRecurso extends AbstractModel {
    public static final String CURRENT = "current";
    public static final String LIST = "list";

    private Recurso current;
    private List<Recurso> list = new ArrayList<>();

    public ModelRecurso() {
        current = new Recurso();
    }

    @Override
    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        firePropertyChange(CURRENT);
        firePropertyChange(LIST);
    }

    public Recurso getCurrent() {
        return current;
    }

    public void setCurrent(Recurso current) {
        this.current = current;
        firePropertyChange(CURRENT);
    }

    public List<Recurso> getList() {
        return list;
    }

    public void setList(List<Recurso> list) {
        this.list = list;
        firePropertyChange(LIST);
    }
}
