package wwee.jihun.Service.Agents;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import wwee.jihun.Entity.CampaignEntity;
import wwee.jihun.Prompt.FormPrompt;
import wwee.jihun.Prompt.SystemPrompt;
import wwee.jihun.Prompt.TonePrompt;
import wwee.jihun.Service.GptService;

@Service
public class AgentB {
    private final GptService gptService;
    private final SystemPrompt systemPrompt;
    private final FormPrompt formPrompt;

    public AgentB(GptService gptService, SystemPrompt systemPrompt, FormPrompt formPrompt) {
        this.gptService = gptService;
        this.systemPrompt = systemPrompt;
        this.formPrompt = formPrompt;
    }

    public Mono<String> generateAdFormat(CampaignEntity campaignEntity, String keywordsAndInfo) {
        // 광고 형식 예시
        String formExample = formPrompt.getSns();

        // 프롬프트 구성
        String prompt = String.format(
                "%s 해당 광고 문구를 인스타그램 피드 광고에 어울리는 형식으로 작성해줘.\n" +
                        "형식의 예시는 다음과 같아.\n" +
                        "[예시]\n" +
                        "%s",
                keywordsAndInfo,
                formExample
        );

        // AgentB 시스템 메시지
        String systemMessage = systemPrompt.getAgentBSystemMessage();

        return gptService.getChatResponse(prompt, systemMessage);
    }
}