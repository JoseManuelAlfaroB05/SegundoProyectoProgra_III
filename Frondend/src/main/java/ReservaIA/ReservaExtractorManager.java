package ReservaIA;

import ClasesUsuario.CategoriaRecurso;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReservaExtractorManager {

    private final ReservaExtractor aiService;

    public ReservaExtractorManager() {
        OpenAiChatModel aiModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini")
                .build();

        aiService = AiServices.create(ReservaExtractor.class, aiModel);
    }

    public ReservaExtraccion extraerReserva(String frase, List<CategoriaRecurso> categorias) {

        String listaCategorias = categorias.stream()
                .map(CategoriaRecurso::getDescripcion)
                .collect(Collectors.joining(", "));

        return aiService.extraer(
                frase,
                listaCategorias,
                LocalDate.now().toString()
        );
    }
}