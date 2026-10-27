package Presentar.Funcionarios;

import ClasesUsuario.Rol;
import ClasesUsuario.User;
import Presentar.AbstractModel;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ModelFuncionario extends AbstractModel {

    private User current;
    private List<User> list;

    public static final String CURRENT = "current";
    public static final String LIST = "list";

    public ModelFuncionario() {
        current = new User("", "", Rol.FUNCIONARIO, "", "");
        list = new ArrayList<>();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
        firePropertyChange(CURRENT);
        firePropertyChange(LIST);
    }

    public User getCurrent() {
        return current;
    }

    public void setCurrent(User current) {
        this.current = current;
        firePropertyChange(CURRENT);
    }

    public List<User> getList() {
        return list;
    }

    public void setList(List<User> list) {
        this.list = list;
        firePropertyChange(LIST);
    }
}