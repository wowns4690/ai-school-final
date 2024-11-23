package wwee.jihun.Repository;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import wwee.jihun.Entity.UserEntity;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, String> {
    //동일한 id를 사용하는 유저가 있는지 확인
    Optional<UserEntity> findByUserId(String userId);
    Optional<UserEntity> findByUserIdAndUserPassword(String userId, String userPassword);
}
