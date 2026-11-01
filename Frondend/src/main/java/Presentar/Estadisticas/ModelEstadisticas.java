package Presentar.Estadisticas;

import Presentar.AbstractModel;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModelEstadisticas extends AbstractModel {

    public static final String RECURSOS = "recursos";
    public static final String ACTIVIDADES = "actividades";

    private Map<String, Integer> recursos = new LinkedHashMap<>();
    private Map<String, Integer> actividades = new LinkedHashMap<>();

    public Map<String, Integer> getRecursos() {
        return recursos;
    }

    public Map<String, Integer> getActividades() {
        return actividades;
    }

    public void setRecursos(Map<String, Integer> recursos) {
        this.recursos = new LinkedHashMap<>(recursos);
        firePropertyChange(RECURSOS);
    }

    public void setActividades(Map<String, Integer> actividades) {
        this.actividades = new LinkedHashMap<>(actividades);
        firePropertyChange(ACTIVIDADES);
    }
}