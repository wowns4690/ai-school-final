package wwee.jihun.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import wwee.jihun.Entity.CampaignEntity;
import wwee.jihun.Prompt.TonePrompt;
import wwee.jihun.Service.CampaignService;
import wwee.jihun.Service.S3Service;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/campaign")
//@CrossOrigin(origins = {"http://hooniping-front-bucket.s3-website.ap-northeast-2.amazonaws.com"})
@CrossOrigin(origins = {"http://localhost:8081"})
public class CampaignController {
    private final CampaignService campaignService;
    private final TonePrompt tonePrompt;
    private final S3Service s3Service;

    @Autowired
    public CampaignController(CampaignService campaignService, TonePrompt tonePrompt, S3Service s3Service) {
        this.campaignService = campaignService;
        this.tonePrompt = tonePrompt;
        this.s3Service = s3Service;
    }

    // ***************<로그인 된 사용자(userId)의 캠페인 관리>****************

    //==================전체 캠페인 관리 페이지======================
    //모든 캠페인 조회
    @GetMapping
    public List<CampaignEntity> getAllCampaigns() {
        return campaignService.getAllCampaigns();
    }

    //userId의 전체 캠페인 조회
    @PostMapping("/user-campaign")
    public List<CampaignEntity> getUserAllCampaigns(@RequestBody CampaignEntity campaignEntity) {
        return campaignService.getUserAllCampaigns(campaignEntity.getUserId());
    }

    //=================특정 캠페인(campaignId) 관리 페이지====================
    //campaignId의 내용 조회
    @PostMapping("/content")
    public Optional<CampaignEntity> CampaignContent(@RequestBody CampaignEntity campaignEntity) {
        String userId = campaignEntity.getUserId();
        Long campaignId = campaignEntity.getCampaignId();

        return campaignService.getCampaignContent(userId, campaignId);
    }

    // 새로운 campaignId(max+1) 할당
    @PostMapping("/content/new-campaignId")
    public Long getNewCampaignId(@RequestBody CampaignEntity campaignEntity) {
        String userId = campaignEntity.getUserId();
        return campaignService.getNewCampaignId(userId);
    }
    //새로운 campaignId의 내용(사용자 입력, 생성된 콘텐츠) 저장
    @PostMapping("/content/save")
    public ResponseEntity<CampaignEntity> saveCampaign(@RequestBody CampaignEntity campaignEntity) {
        String userId = campaignEntity.getUserId();
        CampaignEntity savedCampaign = campaignService.saveNewCampaign(userId, campaignEntity);
        return ResponseEntity.ok(savedCampaign);
    }

    @PutMapping("/content/update")
    public ResponseEntity<CampaignEntity> updateCampaign(@RequestBody CampaignEntity campaignEntity) {
        CampaignEntity updatedCampaign = campaignService.updateCampaign(campaignEntity);
        return ResponseEntity.ok(updatedCampaign);
    }

    //userId 와 product 를 이용해 campaign 검색기능
    @PostMapping ("/search")
    public List<CampaignEntity> CampaignSearch(@RequestBody CampaignEntity campaignEntity) {
        String userId = campaignEntity.getUserId();
        String product = campaignEntity.getProduct();

        return campaignService.getCampaignSearch(userId, product);
    }
    // userID 와 campaignId 를 이용한 캠페인 목록 삭제기능
    @Transactional
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteCampaign(@RequestBody CampaignEntity campaignEntity) {
        String userId = campaignEntity.getUserId();
        Long campaignId = campaignEntity.getCampaignId();
        s3Service.deleteFile(userId+"-"+campaignId);

        // 서비스에서 캠페인 삭제 실행
        campaignService.deleteCampaign(userId, campaignId);

        // 삭제 성공 시 204 No Content 반환
        return ResponseEntity.noContent().build();
    }

}