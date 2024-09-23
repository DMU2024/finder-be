package DMU.demo.auth.controller;

import DMU.demo.kakao.dto.KakaoToken;
import DMU.demo.kakao.service.KakaoService;
import DMU.demo.user.domain.entity.User;
import DMU.demo.user.domain.repository.UserInfoMapping;
import DMU.demo.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/login")
    public ResponseEntity<UserInfoMapping> kakaoLogin(@RequestBody Map<String, String> request) {
        KakaoToken kakaoToken = kakaoService.postKakaoAuth(request.get("code"));

        try {
            return ResponseEntity.ok(kakaoService.postLogin(kakaoToken));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<String> kakaoLogout(@RequestBody Map<String, String> request) {
        User user = userRepository.findById(Long.parseLong(request.get("userId"))).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (user.getAccessToken() == null) {
            return ResponseEntity.ok("" + user.getUserId());
        }

        try {
            return ResponseEntity.ok(kakaoService.postLogout(user));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/unlink")
    @ResponseBody
    public ResponseEntity<String> kakaoUnlink(@RequestBody Map<String, String> request) {
        User user = userRepository.findById(Long.parseLong(request.get("userId"))).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            return ResponseEntity.ok(kakaoService.postUnlink(user));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
