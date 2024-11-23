package wwee.jihun.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import wwee.jihun.Entity.CampaignEntity;
import wwee.jihun.Repository.CampaignMapper;
import wwee.jihun.Repository.CampaignRepository;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CampaignService {
    @PersistenceContext
    private EntityManager entityManager;
    private final DatabaseService databaseService;
    private final CampaignRepository campaignRepository;
    private final S3Service s3Service;
    public CampaignService(DatabaseService databaseService, CampaignRepository campaignRepository, S3Service s3Service) {
        this.databaseService = databaseService;
        this.campaignRepository = campaignRepository;
        this.s3Service = s3Service;
    }

    //db에 있는 모든 캠페인 반환
    public List<CampaignEntity> getAllCampaigns() {
        return databaseService.getAllCampaigns();
    }

    //userId의 모든 캠페인 반환
    public List<CampaignEntity> getUserAllCampaigns(String userId) {
        return databaseService.getUserAllCampaigns(userId).stream()
                .sorted(Comparator.comparing(CampaignEntity::getCampaignId).reversed())
                .collect(Collectors.toList());

    }

    //campaignId의 캠페인 내용 가져오기
    public Optional<CampaignEntity> getCampaignContent(String userId, Long campaignId) {
        return databaseService.getCampaignContentByUserIdAndCampaignId(userId, campaignId);
    }

    // 새로운 캠패인에 campgaignId 할당: max+1
    public Long getNewCampaignId(String userId) {
        Long maxCampaignId = databaseService.getMaxCampaignIdForUser(userId);
        return maxCampaignId + 1;
    }
    //새로운 캠페인 내용 저장
    public CampaignEntity saveNewCampaign(String userId, CampaignEntity newCampaign) {
        // 프론트에서 받아온 campaignId
        Long newCampaignId = newCampaign.getCampaignId();

        // userId에게 newCampaignId가 있는지 확인
        boolean exists = campaignRepository.existsByUserIdAndCampaignId(userId, newCampaignId);
        // newCampaignId가 있다면 200 ok 응답 반환
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Campaign ID already exists for this user.");
        }
        // newCampaignId가 없으면 새로운 캠페인으로 저장
        newCampaign.setCampaignId(newCampaignId);

        try {
            newCampaign.setImage_url(s3Service.uploadFromUrl(newCampaign.getImage_url(), newCampaign.getUserId()+"-"+newCampaign.getCampaignId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return campaignRepository.save(newCampaign);
    }

    //userId, campaignId인 캠페인 내용 업데이트
    @Transactional
    public CampaignEntity updateCampaign(CampaignEntity campaignEntity) {
        // 기존 엔티티 조회
        CampaignEntity existingCampaign = entityManager.createQuery(
                        "SELECT c FROM CampaignEntity c WHERE c.userId = :userId AND c.campaignId = :campaignId", CampaignEntity.class)
                .setParameter("userId", campaignEntity.getUserId())
                .setParameter("campaignId", campaignEntity.getCampaignId())
                .getSingleResult();

        // 엔티티가 존재하지 않으면 예외 발생
        if (existingCampaign == null || !existingCampaign.getUserId().equals(campaignEntity.getUserId())) {
            throw new RuntimeException("Campaign not found or userId does not match");
        }
        try {
            campaignEntity.setImage_url(s3Service.uploadFromUrl(campaignEntity.getImage_url(), campaignEntity.getUserId()+"-"+campaignEntity.getCampaignId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 필요한 필드만 수동으로 설정
        existingCampaign.setProduct(campaignEntity.getProduct());
        existingCampaign.setKeywords(campaignEntity.getKeywords());
        existingCampaign.setBrand(campaignEntity.getBrand());
        existingCampaign.setBrand_model(campaignEntity.getBrand_model());
        existingCampaign.setTone(campaignEntity.getTone());
        existingCampaign.setFeatures(campaignEntity.getFeatures());
        existingCampaign.setAd_text(campaignEntity.getAd_text());
        existingCampaign.setImage_url(campaignEntity.getImage_url());

        // 변경된 엔티티를 저장하고 반환
        return existingCampaign;
    }

    //userId 와 product 를 이용해 campaign 검색기능
    public List<CampaignEntity> getCampaignSearch(String userId, String product) {
        return databaseService.getCampaignSearch(userId, product);
    }

    // userID 와 campaignId 를 이용한 캠페인 목록 삭제기능 추가
    public void deleteCampaign(String userId, Long campaignId) {
        databaseService.deleteByUserIdAndCampaignId(userId, campaignId);
    }
}
