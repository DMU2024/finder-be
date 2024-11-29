package dmu.finder.user.service;

import dmu.finder.kakao.dto.KakaoScopeInfo;
import dmu.finder.kakao.service.KakaoService;
import dmu.finder.user.domain.entity.User;
import dmu.finder.user.domain.repository.UserInfoMapping;
import dmu.finder.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final KakaoService kakaoService;

    public UserInfoMapping getUser(long id) {
        UserInfoMapping user = userRepository.findByUserId(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return user;
    }

    public UserInfoMapping postUserSetting(long id, boolean notifyOnlyBookmarked) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        user.setNotifyOnlyBookmarked(notifyOnlyBookmarked);
        userRepository.save(user);

        return userRepository.findByUserId(user.getUserId());
    }

    public KakaoScopeInfo getKakaoScopes(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return kakaoService.getScopeInfo(user);
    }

    public KakaoScopeInfo postRevokeKakaoScopes(long id, String[] scopes) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return kakaoService.postRevokeScope(user, scopes);
    }
}
