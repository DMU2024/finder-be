package DMU.demo.user.service;

import DMU.demo.kakao.dto.KakaoScopeInfo;
import DMU.demo.kakao.service.KakaoService;
import DMU.demo.user.domain.entity.User;
import DMU.demo.user.domain.repository.UserInfoMapping;
import DMU.demo.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final KakaoService kakaoService;

    public UserInfoMapping getUser(long id) {
        return userRepository.findByUserId(id);
    }

    public List<UserInfoMapping> getUsers() {
        return userRepository.findAllBy();
    }

    public KakaoScopeInfo getKakaoScopes(long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return kakaoService.getScopeInfo(user);
    }

    public KakaoScopeInfo postRevokeKakaoScopes(long id, String[] scopes) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return kakaoService.postRevokeScope(user, scopes);
    }
}
