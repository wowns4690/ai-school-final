package wwee.jihun.Service;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wwee.jihun.Entity.UserEntity;
import wwee.jihun.JwtToken.TokenProvider;
import wwee.jihun.Repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserAuthService {
    private final DatabaseService databaseService;
    private final TokenProvider tokenProvider;

    public UserAuthService(DatabaseService databaseService, TokenProvider tokenProvider) {
        this.databaseService = databaseService;
        this.tokenProvider = tokenProvider;
    }

    // db에 있는 모든 유저를 반환
    public List<UserEntity> getAllUsers() {
        return databaseService.getAllUsers();
    }

    // 회원가입
    public String CreateUser(UserEntity userEntity) {
        String userVerification = databaseService.getUserName(userEntity.getUserId());
        if (userVerification.equals("Available")) {
            return databaseService.CreateUser(userEntity);
        } else {
            return "Unavailable";
        }
    }

    // 로그인
    public String Login(String userId, String userPassword) {
        Optional<UserEntity> userEntity = databaseService.Login(userId, userPassword);
        String tokenId = userEntity.map(UserEntity::getUserId).orElse(null);
        String tokenName = userEntity.map(UserEntity::getUserName).orElse(null);

        if (tokenId == null || tokenName == null) {
            // 사용자 검증 실패 시 null 반환
            return null;
        } else {
            // 검증 성공 시 토큰 반환
            return tokenProvider.CreateAccessToken(tokenId, tokenName);
        }
    }

    //로그인 시간 연장
    public String ExtendLogin(String token) {
        boolean validateToken = tokenProvider.validateToken(token);
        if (validateToken) {
            Claims claims = tokenProvider.parseClaims(token);
            String userId = claims.get("userId", String.class);
            String userName = claims.get("userName", String.class);
            return tokenProvider.CreateAccessToken(userId, userName);
        }else{
            return null;
        }
    }
}
