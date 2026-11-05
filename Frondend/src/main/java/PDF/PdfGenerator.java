
package PDF;

import ClasesUsuario.CategoriaRecurso;
import ClasesUsuario.Recurso;
import ClasesUsuario.Reserva;
import ClasesUsuario.Rol;
import ClasesUsuario.User;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PdfGenerator {

    // Generar PDF de reservas.
    public static void generarPDFReservas(String ruta, ArrayList<Reserva> reservas) throws IOException {

        PdfWriter writer = new PdfWriter(ruta);
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf);

        PdfFont fuente = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont negrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        documento.setFont(fuente);
        agregarTitulo(documento, "Registro de reservas", negrita);

        Table tabla = crearTablaReservas(negrita);
        agregarReservas(tabla, reservas);

        documento.add(tabla);
        documento.close();
    }

    // Agregar el título del documento.
    private static void agregarTitulo(Document documento, String titulo, PdfFont negrita) {

        Paragraph parrafo = new Paragraph(titulo)
                .setFont(negrita)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER);

        documento.add(parrafo);
        documento.add(new Paragraph(" "));
    }

    // Crear la estructura de la tabla de reservas.
    private static Table crearTablaReservas(PdfFont negrita) {

        float[] anchos = {1, 2, 2, 1.5f, 1.5f, 1, 1.5f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos));
        tabla.setWidth(UnitValue.createPercentValue(100));

        String[] columnas = {"ID", "Actividad", "Fecha", "Hora inicio", "Hora fin", "Recursos", "Funcionario"};

        for (String columna : columnas) {
            tabla.addHeaderCell(new Cell().add(new Paragraph(columna).setFont(negrita)));
        }

        return tabla;
    }

    // Agregar todas las reservas a la tabla.
    private static void agregarReservas(Table tabla, ArrayList<Reserva> reservas) {

        if (reservas == null) return;

        for (Reserva reserva : reservas) {

            if (reserva == null) continue;

            tabla.addCell(valorSeguro(reserva.getId()));
            tabla.addCell(valorSeguro(reserva.getActividad()));
            tabla.addCell(reserva.getFecha() == null ? "" : reserva.getFecha().toString());
            tabla.addCell(reserva.getHoraInicio() == null ? "" : reserva.getHoraInicio().toString());
            tabla.addCell(reserva.getHoraFin() == null ? "" : reserva.getHoraFin().toString());
            tabla.addCell(String.valueOf(reserva.getRecursos() == null ? 0 : reserva.getRecursos().size()));
            tabla.addCell(valorSeguro(reserva.getUsuarioId()));
        }
    }

    // Generar PDF del calendario semanal de actividades.
    public static void generarPDFActividades(String ruta, LocalDate lunes, List<Reserva> reservas) throws IOException {

        if (lunes == null) {
            throw new IllegalArgumentException("La fecha de inicio de la semana no puede ser nula.");
        }

        PdfWriter writer = new PdfWriter(ruta);
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf, PageSize.A4.rotate());

        documento.setMargins(25, 20, 25, 20);

        PdfFont fuente = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont negrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        documento.setFont(fuente);
        agregarTitulo(documento, "Calendario semanal de actividades", negrita);

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String periodo = "Semana del " + lunes.format(formatoFecha)
                + " al " + lunes.plusDays(6).format(formatoFecha);

        documento.add(new Paragraph(periodo)
                .setFont(fuente)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));

        documento.add(new Paragraph(" "));

        Table tabla = crearTablaActividades(lunes, negrita);
        agregarActividades(tabla, lunes, reservas, fuente);

        documento.add(tabla);
        documento.close();
    }

    // Crear las columnas del calendario semanal.
    @NotNull
    private static Table crearTablaActividades(LocalDate lunes, PdfFont negrita) {

        float[] anchos = {0.8f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f};

        Table tabla = new Table(UnitValue.createPercentArray(anchos));
        tabla.setWidth(UnitValue.createPercentValue(100));

        tabla.addHeaderCell(crearCelda("Hora", negrita, 8));

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int dia = 0; dia < 7; dia++) {

            LocalDate fecha = lunes.plusDays(dia);

            String nombreDia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "CR"));
            nombreDia = nombreDia.substring(0, 1).toUpperCase() + nombreDia.substring(1);

            String encabezado = nombreDia + "\n" + fecha.format(formatoFecha);

            tabla.addHeaderCell(crearCelda(encabezado, negrita, 8));
        }

        return tabla;
    }

    // Agregar las 24 horas y las actividades de cada día.
    private static void agregarActividades(Table tabla, LocalDate lunes, List<Reserva> reservas, PdfFont fuente) {

        for (int hora = 0; hora < 24; hora++) {

            String intervalo = String.format("%02d:00 - %02d:00", hora, (hora + 1) % 24);
            tabla.addCell(crearCelda(intervalo, fuente, 7));

            for (int dia = 0; dia < 7; dia++) {

                LocalDate fecha = lunes.plusDays(dia);
                String actividad = actividadEn(reservas, fecha, hora);

                tabla.addCell(crearCelda(actividad, fuente, 7));
            }
        }
    }

    // Buscar una actividad que coincida con la fecha y hora.
    @NotNull
    private static String actividadEn(List<Reserva> reservas, LocalDate fecha, int hora) {

        if (reservas == null) return "";

        LocalTime inicioIntervalo = LocalTime.of(hora, 0);
        LocalTime finIntervalo = hora == 23 ? LocalTime.MAX : LocalTime.of(hora + 1, 0);

        StringBuilder actividades = new StringBuilder();

        for (Reserva reserva : reservas) {

            if (reserva == null || reserva.getFecha() == null
                    || reserva.getHoraInicio() == null || reserva.getHoraFin() == null) {
                continue;
            }

            if (!fecha.equals(reserva.getFecha())) continue;

            boolean seCruza = reserva.getHoraInicio().isBefore(finIntervalo)
                    && reserva.getHoraFin().isAfter(inicioIntervalo);

            if (!seCruza) continue;

            String actividad = valorSeguro(reserva.getActividad());
            String usuario = valorSeguro(reserva.getUsuarioId());

            if (actividades.length() > 0) actividades.append("\n\n");

            actividades.append(actividad).append("\n(").append(usuario).append(")");
        }

        return actividades.toString();
    }

    // Crear una celda con el formato indicado.
    private static Cell crearCelda(String contenido, PdfFont fuente, float tamano) {

        return new Cell()
                .add(new Paragraph(contenido).setFont(fuente).setFontSize(tamano))
                .setPadding(3);
    }

    // Evitar valores nulos en las celdas.
    private static String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    // Generar PDF de calendarización.
    public static void generarPDFCalendarizacion(String ruta, LocalDate fecha, CategoriaRecurso categoria, ArrayList<Reserva> reservas) throws Exception {

        if (fecha == null) {
            throw new IllegalArgumentException("Debe seleccionar una fecha para generar el PDF.");
        }

        PdfWriter writer = new PdfWriter(ruta);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());

        try {
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            document.add(new Paragraph("Calendarización de reservas")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Fecha: " + fecha.format(formatoFecha)));
            document.add(new Paragraph("Categoría: " + (categoria == null ? "Todas las categorías" : categoria.getDescripcion())));
            document.add(new Paragraph(" "));

            float[] anchos = {2, 2, 4, 2, 5};
            Table tabla = new Table(UnitValue.createPercentArray(anchos)).useAllAvailableWidth();

            String[] encabezados = {"Hora inicio", "Hora fin", "Actividad", "Usuario", "Recursos"};

            for (String encabezado : encabezados) {
                tabla.addHeaderCell(new Cell().add(new Paragraph(encabezado).setBold()));
            }

            int cantidadReservas = 0;

            if (reservas != null) {
                for (Reserva reserva : reservas) {

                    if (reserva == null) continue;

                    tabla.addCell(valorSeguro(reserva.getHoraInicio() == null ? null : reserva.getHoraInicio().toString()));
                    tabla.addCell(valorSeguro(reserva.getHoraFin() == null ? null : reserva.getHoraFin().toString()));
                    tabla.addCell(valorSeguro(reserva.getActividad()));
                    tabla.addCell(valorSeguro(reserva.getUsuarioId()));
                    tabla.addCell(obtenerRecursosPDF(reserva.getRecursos()));

                    cantidadReservas++;
                }
            }

            if (cantidadReservas == 0) {
                document.add(new Paragraph("No hay reservas para los criterios seleccionados."));
            } else {
                document.add(tabla);
            }

        } finally {
            document.close();
        }
    }

    // Obtener los recursos de una reserva para el PDF.
    private static String obtenerRecursosPDF(ArrayList<Recurso> recursos) {

        if (recursos == null || recursos.isEmpty()) return "";

        StringBuilder resultado = new StringBuilder();

        for (Recurso recurso : recursos) {

            if (recurso == null) continue;

            if (resultado.length() > 0) resultado.append(", ");

            resultado.append(valorSeguro(recurso.getDescripcion()));
        }

        return resultado.toString();
    }

    // Generar PDF de categorías.
    public static void generarPDFCategorias(String ruta, List<CategoriaRecurso> categorias) throws IOException {

        PdfWriter writer = new PdfWriter(ruta);
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf);

        try {
            PdfFont fuente = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont negrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            documento.setFont(fuente);
            agregarTitulo(documento, "Registro de categorías", negrita);

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{2, 5}));
            tabla.setWidth(UnitValue.createPercentValue(100));

            tabla.addHeaderCell(crearCelda("ID", negrita, 11));
            tabla.addHeaderCell(crearCelda("Descripción", negrita, 11));

            int cantidad = 0;

            if (categorias != null) {
                for (CategoriaRecurso categoria : categorias) {

                    if (categoria == null) continue;

                    tabla.addCell(crearCelda(valorSeguro(categoria.getVarId()), fuente, 10));
                    tabla.addCell(crearCelda(valorSeguro(categoria.getDescripcion()), fuente, 10));

                    cantidad++;
                }
            }

            if (cantidad == 0) {
                documento.add(new Paragraph("No hay categorías registradas."));
            } else {
                documento.add(tabla);
            }

        } finally {
            documento.close();
        }
    }

    // Generar PDF de recursos.
    public static void generarPDFRecursos(String ruta, List<Recurso> recursos) throws IOException {

        PdfWriter writer = new PdfWriter(ruta);
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf);

        try {
            PdfFont fuente = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont negrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            documento.setFont(fuente);
            agregarTitulo(documento, "Registro de recursos", negrita);

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{2, 3, 5}));
            tabla.setWidth(UnitValue.createPercentValue(100));

            tabla.addHeaderCell(crearCelda("ID", negrita, 11));
            tabla.addHeaderCell(crearCelda("Categoría", negrita, 11));
            tabla.addHeaderCell(crearCelda("Descripción", negrita, 11));

            int cantidad = 0;

            if (recursos != null) {
                for (Recurso recurso : recursos) {

                    if (recurso == null) continue;

                    String categoria = recurso.getRecurso() != null
                            ? recurso.getRecurso().getDescripcion()
                            : recurso.getCategoriaId();

                    tabla.addCell(crearCelda(valorSeguro(recurso.getId()), fuente, 10));
                    tabla.addCell(crearCelda(valorSeguro(categoria), fuente, 10));
                    tabla.addCell(crearCelda(valorSeguro(recurso.getDescripcion()), fuente, 10));

                    cantidad++;
                }
            }

            if (cantidad == 0) {
                documento.add(new Paragraph("No hay recursos registrados."));
            } else {
                documento.add(tabla);
            }

        } finally {
            documento.close();
        }
    }

    // Generar PDF de funcionarios.
    public static void generarPDFFuncionarios(String ruta, List<User> funcionarios) throws IOException {

        PdfWriter writer = new PdfWriter(ruta);
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf);

        try {
            PdfFont fuente = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont negrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            documento.setFont(fuente);
            agregarTitulo(documento, "Registro de funcionarios", negrita);

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{2, 5, 3}));
            tabla.setWidth(UnitValue.createPercentValue(100));

            tabla.addHeaderCell(crearCelda("ID", negrita, 11));
            tabla.addHeaderCell(crearCelda("Nombre", negrita, 11));
            tabla.addHeaderCell(crearCelda("Teléfono", negrita, 11));

            int cantidad = 0;

            if (funcionarios != null) {
                for (User funcionario : funcionarios) {

                    if (funcionario == null || funcionario.getVarRol() != Rol.FUNCIONARIO) continue;

                    tabla.addCell(crearCelda(valorSeguro(funcionario.getVarId()), fuente, 10));
                    tabla.addCell(crearCelda(valorSeguro(funcionario.getVarNombre()), fuente, 10));
                    tabla.addCell(crearCelda(valorSeguro(funcionario.getVarTelefono()), fuente, 10));

                    cantidad++;
                }
            }

            if (cantidad == 0) {
                documento.add(new Paragraph("No hay funcionarios registrados."));
            } else {
                documento.add(tabla);
            }

        } finally {
            documento.close();
        }
    }

    // Generar PDF de estadísticas de recursos.
    public static void generarPDFEstadisticasRecursos(String ruta, LocalDate desde, LocalDate hasta, Map<String, Integer> datos) throws IOException {
        generarPDFEstadisticas(ruta, "Estadísticas de recursos", "Categoría", desde, hasta, datos);
    }

    // Generar PDF de estadísticas de actividades.
    public static void generarPDFEstadisticasActividades(String ruta, LocalDate desde, LocalDate hasta, Map<String, Integer> datos) throws IOException {
        generarPDFEstadisticas(ruta, "Estadísticas de actividades", "Semana", desde, hasta, datos);
    }

    // Método común para generar los PDF de estadísticas.
    private static void generarPDFEstadisticas(String ruta, String titulo, String primeraColumna, LocalDate desde, LocalDate hasta, Map<String, Integer> datos) throws IOException {

        PdfWriter writer = new PdfWriter(ruta);
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf);

        try {
            PdfFont fuente = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont negrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            documento.setFont(fuente);
            agregarTitulo(documento, titulo, negrita);

            documento.add(new Paragraph("Periodo: " + desde + " al " + hasta));

            Table tabla = new Table(UnitValue.createPercentArray(new float[]{4, 2}));
            tabla.setWidth(UnitValue.createPercentValue(100));

            tabla.addHeaderCell(crearCelda(primeraColumna, negrita, 11));
            tabla.addHeaderCell(crearCelda("Cantidad", negrita, 11));

            int cantidad = 0;

            if (datos != null) {
                for (Map.Entry<String, Integer> entrada : datos.entrySet()) {

                    tabla.addCell(crearCelda(valorSeguro(entrada.getKey()), fuente, 10));
                    tabla.addCell(crearCelda(String.valueOf(entrada.getValue()), fuente, 10));

                    cantidad++;
                }
            }

            if (cantidad == 0) {
                documento.add(new Paragraph("No hay datos registrados para el periodo seleccionado."));
            } else {
                documento.add(tabla);
            }

        } finally {
            documento.close();
        }
    }
}
