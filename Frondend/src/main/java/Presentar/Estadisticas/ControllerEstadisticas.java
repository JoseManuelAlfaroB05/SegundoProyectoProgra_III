package Presentar.Estadisticas;

import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;
import logic.ServiceLocator;
import PDF.PdfGenerator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ControllerEstadisticas {

    private final ModelEstadisticas model = new ModelEstadisticas();

    public ModelEstadisticas getModel() {
        return model;
    }

    public void cargarRecursos(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> conteo = new LinkedHashMap<>();

        for (Reserva reserva : reservasEnRango(desde, hasta)) {
            for (Recurso recurso : reserva.getRecursos()) {

                String categoria = recurso.getRecurso() == null
                        ? recurso.getCategoriaId()
                        : recurso.getRecurso().getDescripcion();

                conteo.put(
                        categoria,
                        conteo.getOrDefault(categoria, 0) + 1
                );
            }
        }

        model.setRecursos(conteo);
    }

    public void cargarActividades(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> conteo = new LinkedHashMap<>();

        for (Reserva reserva : reservasEnRango(desde, hasta)) {

            LocalDate semana = reserva.getFecha()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

            String clave = semana.toString();

            conteo.put(
                    clave,
                    conteo.getOrDefault(clave, 0) + 1
            );
        }

        model.setActividades(conteo);
    }

    private List<Reserva> reservasEnRango(LocalDate desde, LocalDate hasta) {
        return ServiceLocator.getService()
                .listarReservas()
                .stream()
                .filter(reserva ->
                        !reserva.getFecha().isBefore(desde)
                                && !reserva.getFecha().isAfter(hasta))
                .toList();
    }

    public void generarPDFRecursos(String ruta, LocalDate desde, LocalDate hasta) throws Exception {
        cargarRecursos(desde, hasta);
        PdfGenerator.generarPDFEstadisticasRecursos(ruta, desde, hasta, model.getRecursos());
    }

    public void generarPDFActividades(String ruta, LocalDate desde, LocalDate hasta) throws Exception {
        cargarActividades(desde, hasta);
        PdfGenerator.generarPDFEstadisticasActividades(ruta, desde, hasta, model.getActividades());
    }
}