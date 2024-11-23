package wwee.jihun.Service.Agents;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import wwee.jihun.Entity.CampaignEntity;
import wwee.jihun.Prompt.SystemPrompt;
import wwee.jihun.Service.GptService;

@Service
public class AgentA {
    private final GptService gptService;
    private final SystemPrompt systemPrompt;
    public AgentA(GptService gptService, SystemPrompt systemPrompt) {
        this.gptService = gptService;
        this.systemPrompt = systemPrompt;
    }
    public Mono<String> generateKeywordsAndProductInfo(CampaignEntity campaignEntity, String keywords) {
//        String keywords = campaignEntity.getKeywords();
        StringBuilder promptBuilder = new StringBuilder(String.format(
                "\"%s\" 제품을 위한 광고 문구 3가지를 생성해줘." +
                        "광고 문구에 반드시 들어가야 할 키워드로 \"%s\"가 꼭 들어가야 해." +
                        "제품의 특징이자 장점은 \"%s\"이야.\n",
                campaignEntity.getProduct(),
                keywords,
                campaignEntity.getFeatures()
//                campaignEntity.getKeyword1(),
//                campaignEntity.getKeyword2(),
//                campaignEntity.getKeyword3()
        ));

        // 브랜드명(brand)이 있으면 추가
        if (campaignEntity.getBrand() != null && !campaignEntity.getBrand().isBlank() && !campaignEntity.getBrand().isEmpty()) {
            promptBuilder.append("제품의 브랜드 이름은 [").append(campaignEntity.getBrand()).append("]이야.\n");
        }
        // 광고 모델(brand_model)이 있으면 추가
        if (campaignEntity.getBrand_model() != null && !campaignEntity.getBrand_model().isBlank()) {
            promptBuilder.append("제품의 광고 모델 이름은 [").append(campaignEntity.getBrand_model()).append("]이야.\n");
        }


        String prompt = promptBuilder.toString();
        String systemMessage = systemPrompt.getAgentASystemMessage();

        return gptService.getChatResponse(prompt, systemMessage);
    }
}