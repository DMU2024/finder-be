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

import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final KakaoService kakaoService;
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final LocationRepository locationRepository;

    @CrossOrigin(origins = "*")
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
    public ResponseEntity<String> kakaoLogout(@RequestBody Map<String, String> request) throws Exception {
        User user = userRepository.findById(Long.parseLong(request.get("userId"))).orElse(null);

        if (user != null) {
            ResponseEntity<String> response = kakaoService.postLogout(user);

            if (response.getStatusCode() == HttpStatus.OK) {
                user.setAccessToken(null);
                user.setRefreshToken(null);
                userRepository.save(user);
                return response;
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.notFound().build();
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
                return response;
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}