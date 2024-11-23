package wwee.jihun.Repository;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import wwee.jihun.Entity.CampaignEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-05T16:52:11+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Ubuntu)"
)
@Component
public class CampaignMapperImpl implements CampaignMapper {

    @Override
    public void updateCampaign(CampaignEntity source, CampaignEntity target) {
        if ( source == null ) {
            return;
        }

        target.setProduct( source.getProduct() );
        target.setKeywords( source.getKeywords() );
        target.setFeatures( source.getFeatures() );
        target.setTone( source.getTone() );
        target.setBrand( source.getBrand() );
        target.setBrand_model( source.getBrand_model() );
        target.setAd_text( source.getAd_text() );
        target.setImage_url( source.getImage_url() );
    }
}
