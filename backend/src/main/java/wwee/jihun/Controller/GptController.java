package wwee.jihun.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wwee.jihun.Entity.CampaignEntity;
import wwee.jihun.Service.*;

import java.io.IOException;

import wwee.jihun.Service.GptService;
import wwee.jihun.Service.JsonDecoderService;
import wwee.jihun.Service.KeywordService;
import wwee.jihun.Service.SwarmService;

// 스프링의 REST 컨트롤러, HTTP 요청을 처리하는 역할
@RestController
// /api/gpt 경로로 들어오는 요청 처리
@RequestMapping("/api/gpt")
// 다른 도메인에서 요청(CORS) 허용을 http://localhost:8081 로 설정
//@CrossOrigin(origins = "http://hooniping-front-bucket.s3-website.ap-northeast-2.amazonaws.com")
@CrossOrigin(origins = {"http://localhost:8081"})
public class GptController {
    // JsonDecoderService 클래스를 new 키워드를 통해 jsonDecoderService 객체를 만듬
    JsonDecoderService jsonDecoderService = new JsonDecoderService();
    private final GptService gptService;
    private final SwarmService swarmService;
    private final DalleService dalleService;
    private final S3Service s3Service;
    private final KeywordService keywordService;
    public GptController(GptService gptService, SwarmService swarmService, DalleService dalleService, S3Service s3Service, KeywordService keywordService) {
        this.gptService = gptService;
        this.swarmService = swarmService;
        this.dalleService = dalleService;
        this.s3Service = s3Service;
        this.keywordService = keywordService;
    }
    //광고 문구 출력
    @PostMapping("/adtext")
    public String AdText(@RequestBody CampaignEntity campaignEntity) {
        String keywords = campaignEntity.getKeywords();
        return swarmService.generateCasualInstagramAd(campaignEntity, keywords).block();
    }
    //image생성후 s3 bucket에 저장 하고 이미지 url을 반환
    @PostMapping("/image")
    public String Image(@RequestParam String prompt, String userId, String campaignId) {
        String url = dalleService.generateImage(prompt).block();
        try{
            return s3Service.uploadFromUrl(url, userId+"-"+campaignId);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //image를 bucket에서 삭제하는 메소드
    @PostMapping("/image-dlt")
    public ResponseEntity<String> ImageDlt(@RequestParam String fileName){
        return s3Service.deleteFile(fileName);
    }
    //image url을 가져오는 메소드
    @PostMapping("/image-get")
    public String ImageGet(@RequestParam String fileName){
        return s3Service.getFileUrl(fileName);
    }

    @PostMapping("/keyword")
    public String Chat(@RequestBody CampaignEntity campaignEntity) {
        return keywordService.suggestKeywords(campaignEntity).block();
    }

    @PostMapping("/onlyImage")
    public String onlyImage(@RequestParam String prompt){
        return dalleService.generateImage(prompt).block();
    }

}