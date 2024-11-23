package wwee.jihun.Service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import wwee.jihun.Entity.CampaignEntity;
import wwee.jihun.Service.Agents.AgentA;
import wwee.jihun.Service.Agents.AgentB;
import wwee.jihun.Service.Agents.AgentC;

@Service
public class SwarmService {
    private final AgentA agentA;
    private final AgentB agentB;
    private final AgentC agentC;

    public SwarmService(AgentA agentA, AgentB agentB, AgentC agentC) {
        this.agentA = agentA;
        this.agentB = agentB;
        this.agentC = agentC;
    }

    public Mono<String> generateCasualInstagramAd(CampaignEntity campaignEntity, String keywords) {
        // Step 1: agentA를 통해 키워드, 제품 특장점, 브랜드명, 광고 모델 정보를 포함한 광고 문구 생성
        return agentA.generateKeywordsAndProductInfo(campaignEntity, keywords)
                .flatMap(keywordsAndInfo ->
                        // Step 2: agentB를 통해 인스타그램 피드 광고 형식으로 수정
                        agentB.generateAdFormat(campaignEntity, keywordsAndInfo)
                                .flatMap(adFormat ->
                                    // Step 3: agentC를 통해 특정 어조로 변환
                                    agentC.convertToCasualTone(campaignEntity, adFormat)
                                            .map(casualAd ->
                                                casualAd
                                    )
                        )
                );
    }

}