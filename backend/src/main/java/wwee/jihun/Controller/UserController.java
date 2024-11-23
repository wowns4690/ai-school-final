package wwee.jihun.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wwee.jihun.Entity.UserEntity;
import wwee.jihun.Service.UserAuthService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
//@CrossOrigin(origins = {"http://hooniping-front-bucket.s3-website.ap-northeast-2.amazonaws.com"})
@CrossOrigin(origins = {"http://localhost:8081"})
public class UserController {
    private final UserAuthService userAuthService;

    @Autowired
    public UserController(UserAuthService userAuthService){
        this.userAuthService = userAuthService;
    }


    //모든 user 조회
    @GetMapping
    public List<UserEntity> getAllUsers(){
        return userAuthService.getAllUsers();
    }

    //회원가입
    @PostMapping("/create")
    public String CreateUser(@RequestBody UserEntity userEntity){
        return userAuthService.CreateUser(userEntity);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserEntity userEntity) {
        String token = userAuthService.Login(userEntity.getUserId(), userEntity.getUserPassword());

        if (token != null) {
            // 로그인 성공 시 토큰 반환
            return ResponseEntity.status(HttpStatus.OK).body(token);
        } else {
            // 로그인 실패 시 401 상태 코드와 오류 메시지 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }

    @PostMapping("/extendLogin")
    public ResponseEntity<String> extendLogin(@RequestBody Map<String, String> requestBody) {
        String token = requestBody.get("token");
        String newToken = userAuthService.ExtendLogin(token);
        if (newToken != null) {
            return ResponseEntity.status(HttpStatus.OK).body(newToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보가 올바르지 않습니다.");
        }
    }


}
