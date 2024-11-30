package dmu.finder.auth.controller;

import dmu.finder.kakao.dto.KakaoToken;
import dmu.finder.kakao.service.KakaoService;
import dmu.finder.user.domain.entity.User;
import dmu.finder.user.domain.repository.UserInfoMapping;
import dmu.finder.user.domain.repository.UserRepository;
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

    @GetMapping("/login")
    public ResponseEntity<String> getLoginURL(@RequestParam(required = false) boolean isDev) {
        return ResponseEntity.ok(kakaoService.GetLoginURL(isDev));
    }

    @PostMapping("/login")
    public ResponseEntity<UserInfoMapping> kakaoLogin(@RequestBody Map<String, String> request) {
        boolean isDev = Boolean.parseBoolean(request.get("isDev"));
        KakaoToken kakaoToken = kakaoService.postKakaoAuth(request.get("code"), isDev);

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
