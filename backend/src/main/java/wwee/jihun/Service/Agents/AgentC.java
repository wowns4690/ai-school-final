package wwee.jihun.Service.Agents;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import wwee.jihun.Entity.CampaignEntity;
import wwee.jihun.Prompt.SystemPrompt;
import wwee.jihun.Prompt.TonePrompt;
import wwee.jihun.Service.GptService;

@Service
public class AgentC {
    private final GptService gptService;
    private final TonePrompt tonePrompt;
    private final SystemPrompt systemPrompt;
    public AgentC(GptService gptService, TonePrompt tonePrompt, SystemPrompt systemPrompt) {
        this.gptService = gptService;
        this.tonePrompt = tonePrompt;
        this.systemPrompt = systemPrompt;
    }
    public Mono<String> convertToCasualTone(CampaignEntity campaignEntity, String adFormat) {
        // Tone에 따른 프롬프트 예시 텍스트 선택
        String toneExample = switch (campaignEntity.getTone()) {
            case "럭키비키체" -> tonePrompt.getLuckviki();
            case "해요체" -> tonePrompt.getHaeyo();
            case "반말" -> tonePrompt.getBanmal();
            case "신조어" -> tonePrompt.getNeologism();
            default -> tonePrompt.getSeubnida();  // 습니다체
        };

        // 광고 문구의 형식 설정
        String prompt = String.format(
                "[광고문구]\n%s\n" +
                        "해당 광고 문구의 어조를 %s 형식으로 작성해줘. " +
                        "어조의 예시는 다음과 같아:\n%s",
                adFormat,
                campaignEntity.getTone(),
                toneExample
        );

        // 최종 프롬프트 생성
//        String prompt = String.format(promptTemplate, campaignEntity.getTone(), toneExample);

        String systemMessage = systemPrompt.getAgentCSystemMessage();
        // GPT 서비스 호출
        return gptService.getChatResponse(prompt, systemMessage);
    }
}