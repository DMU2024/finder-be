package DMU.demo.auth.controller;

import DMU.demo.auth.dto.KakaoToken;
import DMU.demo.auth.dto.KakaoUserInfo;
import DMU.demo.auth.dto.KakaoUserProperties;
import DMU.demo.auth.service.KakaoService;
import DMU.demo.keyword.domain.repository.KeywordRepository;
import DMU.demo.location.domain.repository.LocationRepository;
import DMU.demo.user.domain.entity.User;
import DMU.demo.user.domain.repository.UserInfoMapping;
import DMU.demo.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final KakaoService kakaoService;
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final LocationRepository locationRepository;

    @PostMapping("/login")
    public ResponseEntity<UserInfoMapping> kakaoLogin(@RequestBody Map<String, String> request) throws Exception {
        KakaoToken kakaoToken = kakaoService.postKakaoAuth(request.get("code"));
        KakaoUserInfo kakaoDTO = kakaoService.postLogin(kakaoToken);
        KakaoUserProperties properties = kakaoDTO.getProperties();

        try {
            userRepository.save(User.builder()
                    .userId(kakaoDTO.getId())
                    .username(properties.getNickname())
                    .profileImage(properties.getProfile_image())
                    .thumbnailImage(properties.getThumbnail_image())
                    .accessToken(kakaoToken.getAccess_token())
                    .refreshToken(kakaoToken.getRefresh_token())
                    .build());

            return ResponseEntity.ok(userRepository.findByUserId(kakaoDTO.getId()));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<String> kakaoLogout(@RequestBody Map<String, String> request) {
        User user = userRepository.findById(Long.parseLong(request.get("userId"))).orElse(null);

        try {
            if (user != null) {
                if (user.getAccessToken() == null) {
                    return ResponseEntity.ok("" + user.getUserId());
                }
                return ResponseEntity.ok(kakaoService.postLogout(user));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (HttpClientErrorException e) {
            try {
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    KakaoToken newToken = kakaoService.postRefreshToken(user.getRefreshToken());

                    user.setAccessToken(newToken.getAccess_token());
                    user.setRefreshToken(newToken.getRefresh_token());
                    userRepository.save(user);

                    return ResponseEntity.ok(kakaoService.postLogout(user));
                } else {
                    return ResponseEntity.internalServerError().build();
                }
            } catch (Exception exception) {
                kakaoService.revokeToken(user);
                return ResponseEntity.ok("" + user.getUserId());
            }
        }
    }

    @PostMapping("/unlink")
    @ResponseBody
    public ResponseEntity<String> kakaoUnlink(@RequestBody Map<String, String> request) throws Exception {
        User user = userRepository.findById(Long.parseLong(request.get("userId"))).orElse(null);

        if (user != null) {
            ResponseEntity<String> response = kakaoService.postUnlink(user);
            if (response.getStatusCode() == HttpStatus.OK) {
                keywordRepository.deleteAll(keywordRepository.findByUser(user));
                locationRepository.deleteAll(locationRepository.findByUser(user));
                userRepository.delete(user);
                return ResponseEntity.ok("" + user.getUserId());
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
