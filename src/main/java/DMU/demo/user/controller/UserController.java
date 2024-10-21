package DMU.demo.user.controller;

import DMU.demo.kakao.dto.KakaoScopeInfo;
import DMU.demo.user.domain.repository.UserInfoMapping;
import DMU.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserInfoMapping getUserById(@PathVariable long id) {
        return userService.getUser(id);
    }

    @GetMapping("/scopes/{id}")
    public KakaoScopeInfo getScopesById(@PathVariable long id) {
        return userService.getKakaoScopes(id);
    }

    @PostMapping("/scopes/{id}")
    public KakaoScopeInfo postRevokeScopesById(@PathVariable long id, @RequestBody Map<String, String> request) {
        String[] scopes = request.get("scopes").split(",");
        return userService.postRevokeKakaoScopes(id, scopes);
    }
}