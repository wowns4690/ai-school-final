package wwee.jihun.Entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.annotation.processing.Generated;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@IdClass(CampaignEntityId.class)
@Table(name = "tb_campaign")
public class CampaignEntity {

    @Id
    @Column(name = "campaign_id", updatable = true)  // 캠페인 id
    private Long campaignId;

    @Id
    @Column(name = "user_id")  // 사용자 id
    private String userId;

    //=============입력 필수==============
    @Column(name = "product")  // 제품명
    private String product;

    @Column(name = "keywords")  // 선택한 키워드 (","로 구분)
    private String keywords;

    @Column(name = "features")  // 제품 특장점(필수)
    private String features;

    //=============입력 선택(옵션)==============
    @Column(name = "tone")  // 어조(옵션, default='습니다체')
    private String tone;

    @Column(name = "brand")  // 브랜드명(옵션)
    private String brand;

    @Column(name = "brand_model")  // 광고 모델 이름(옵션)
    private String brand_model;

    // 생성된 광고 문구 & 이미지
    @Column(name = "ad_text")  // 광고 문구
    private String ad_text;

    @Column(name = "image_url")  // 이미지 url
    private String image_url;


}

