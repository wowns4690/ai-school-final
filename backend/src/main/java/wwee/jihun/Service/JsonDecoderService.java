package wwee.jihun.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class JsonDecoderService {
    private final ObjectMapper objectMapper;
    public  JsonDecoderService(){
        this.objectMapper = new ObjectMapper();
    }

    public String DecodeAndFormat(String Answer) throws Exception{
        JsonNode rootNode = objectMapper.readTree(Answer);
        String body = rootNode.get("body").asText();
        JsonNode bodyNode = objectMapper.readTree(body);

        return bodyNode.get("result").asText();
    }

    public Mono<String[]> DecodeAndFormatGpt(Mono<String> responseMono) {
        // Mono 로부터 비동기 응답을 받아서 처리
        return responseMono.map(response -> {
            try {
                // JSON 문자열에서 choices의 content 필드를 추출
                JsonNode rootNode = objectMapper.readTree(response);
                String content = rootNode.path("choices").get(0).path("message").path("content").asText();

                // content 내부의 JSON을 다시 파싱하여 keyword1, keyword2, keyword3 추출
                JsonNode contentNode = objectMapper.readTree(content);
                String keyword1 = contentNode.path("keyword1").asText();
                String keyword2 = contentNode.path("keyword2").asText();
                String keyword3 = contentNode.path("keyword3").asText();

                // 추출된 키워드를 배열로 반환
                return new String[] { keyword1, keyword2, keyword3 };

            } catch (Exception e) {
                e.printStackTrace();
                return new String[] {}; // 에러가 발생하면 빈 배열 반환
            }
        });
    }
}
