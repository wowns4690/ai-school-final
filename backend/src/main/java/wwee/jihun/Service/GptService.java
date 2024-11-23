package wwee.jihun.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import wwee.jihun.Entity.CampaignEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GptService {
    private final WebClient webClient;

    @Value("${gpt.api.key}")
    private String apiKey;

    public GptService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
    }

    public Mono<String> getChatResponse(String prompt, String systemMessage) {
        Map<String, Object> requestBody = Map.of(

                "model", "gpt-4o-mini",
//                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "messages", List.of(
                        Map.of("role", "system", "content", systemMessage),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 3000
        );

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    // 응답에서 텍스트 추출
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content"); // 텍스트 추출
                });
    }
}
