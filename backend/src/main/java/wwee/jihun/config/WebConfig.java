package wwee.jihun.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 엔드포인트에 대해 CORS를 설정
                .allowedOrigins("http://hooniping-bucket.s3-website.ap-northeast-2.amazonaws.com") // 허용할 클라이언트 URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메소드
                .allowedHeaders("*") // 허용할 요청 헤더
                .allowCredentials(true); // 자격 증명(쿠키) 허용
    }

    // WebClient Bean 등록
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://graph.facebook.com/v17.0") // Instagram Graph API 기본 URL
                .build();
    }
}