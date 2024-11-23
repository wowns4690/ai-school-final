package wwee.jihun.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class DalleService {

    private final WebClient webClient;

    public DalleService(@Value("${gpt.api.key}") String apiKey, WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public Mono<String> generateImage(String prompt){
        Map<String, Object> requestBody = Map.of(
                "model", "dall-e-3",
                "prompt", prompt,
                "n", 1 // 이미지 수
        );

        return webClient.post()
                .uri("/images/generations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    // 응답에서 이미지 URL 추출
                    Map<String, Object> data = (Map<String, Object>) ((List<?>) response.get("data")).get(0);
                    return (String) data.get("url");
                });
    }
}
