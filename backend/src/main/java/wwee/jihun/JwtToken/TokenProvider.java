package wwee.jihun.JwtToken;

import ch.qos.logback.classic.Logger;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;


@RequiredArgsConstructor
@Component
public class TokenProvider {

    @Value("${jwt.secret}")
    private String SecretKey;

    public String CreateAccessToken(String userId, String userName){
        return createToken(userId , userName);
    }

    private String createToken(String userId, String userName) {
        //토큰에 사용할 claims 작성
        //Claims : Jwt에서 페이로드라고 불리는 부분에 저장되는 정보.
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("userName", userName);
        ZonedDateTime now = ZonedDateTime.now();
        long tokenValidTime = 60 * 30L; // 60초 * 30번 토큰 유효기간 30분
        ZonedDateTime tokenValidity = now.plusSeconds(tokenValidTime);
        //토큰을 생성후 토큰 반환
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(SignatureAlgorithm.HS256, SecretKey)
                .compact();
    }
    
    public String getUserId(String token){
        //CustomUserDetalilsService의 유저 확인에 사용할 userId를 받아옴
        return parseClaims(token).get("userId", String.class);
    }

    public boolean validateToken(String token) {
        //유효한 토큰인지 검증
        Logger log = (Logger) LoggerFactory.getLogger(TokenProvider.class);
        try {
            Jwts.parserBuilder().setSigningKey(SecretKey).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("토큰 정보가 부족합니다.");
        }
        return false;
    }

    public Claims parseClaims(String accessToken) {
        //토큰에서 Claims(사용자 정보)를 파싱하는 메서드
        //만약 토큰이 만료되었으면 catch문이 작동. ExpiredJwtException 예외가 발생하고, 그 예외에서 Claims를 추출하여 반환.
        //토큰이 유효한다면 getbody를 통해 Claims를 반환
        try {
            return Jwts.parserBuilder().setSigningKey(SecretKey).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
