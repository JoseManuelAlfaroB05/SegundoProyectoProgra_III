package ReservaIA;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ReservaExtractor {

    @SystemMessage("""
        Eres un asistente especializado en extraer informacion de reservas de espacios y recursos.
        Reglas estrictas:
        1. La fecha siempre debe ser en formato ISO yyyy-MM-dd. Si el usuario dice "proximo 12 de agosto"
           y el ano no esta especificado, usa el ano actual o el siguiente si la fecha ya paso.
        2. Las horas deben estar en formato HH:mm de 24 horas (ej. 8am = "08:00", 10pm = "22:00").
        3. Para 'categoriasRecurso' SOLO puedes usar nombres que aparezcan EXACTAMENTE en la Lista
           de categorias disponibles proporcionada. No inventes categorias nuevas.
        4. Si algun campo no esta en la frase, devuelve null para ese campo.
        5. 'actividad' debe contener el nombre o descripcion de la actividad que el usuario desea realizar.
           Por ejemplo, si dice "para una reunion", la actividad es "Reunion".
           Si dice "para una clase de programacion", la actividad es "Clase de programacion".
        """)
    @UserMessage("""
        Fecha de referencia (hoy): {{hoy}}

        Categorias de recurso disponibles en el sistema:
        {{categorias}}

        Frase del usuario:
        "{{frase}}"

        Extrae los datos de la reserva seleccionando las categorias que mejor correspondan
        a lo que el usuario describe.
        """)
    ReservaExtraccion extraer(
            @V("frase") String frase,
            @V("categorias") String categoriasList,
            @V("hoy") String fechaHoy
    );
}