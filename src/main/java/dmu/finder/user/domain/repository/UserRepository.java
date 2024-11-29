package dmu.finder.user.domain.repository;

import dmu.finder.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    UserInfoMapping findByUserId(long userId);

    List<UserInfoMapping> findAllBy();

    User findByAccessToken(String accessToken);
}