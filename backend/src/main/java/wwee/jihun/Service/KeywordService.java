package wwee.jihun.Service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import wwee.jihun.Entity.CampaignEntity;
import wwee.jihun.Prompt.SystemPrompt;

@Service
public class KeywordService {
    private final GptService gptService;
    private final SystemPrompt systemPrompt;
    public KeywordService(GptService gptService, SystemPrompt systemPrompt) {
        this.gptService = gptService;
        this.systemPrompt = systemPrompt;
    }

    public Mono<String> suggestKeywords(CampaignEntity campaignEntity) {

        String prompt = String.format(
                "\"%s\"에 대한 광고 문구를 생성하기 위한 키워드 10가지를 알려줘.",
                campaignEntity.getProduct()
        );
        String systemMessage = systemPrompt.getKeywordSystemMessage();

        return gptService.getChatResponse(prompt, systemMessage);
    }

}
