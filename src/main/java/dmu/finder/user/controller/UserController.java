package dmu.finder.user.controller;

import dmu.finder.kakao.dto.KakaoScopeInfo;
import dmu.finder.lostgoods.domain.entity.LostGoods;
import dmu.finder.lostgoods.service.LostGoodsService;
import dmu.finder.user.domain.repository.UserInfoMapping;
import dmu.finder.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LostGoodsService lostGoodsService;

    @GetMapping("/{id}")
    public UserInfoMapping getUserById(@PathVariable long id) {
        return userService.getUser(id);
    }

    @PostMapping("/{id}")
    public UserInfoMapping postUserSettingById(@PathVariable long id, @RequestBody Map<String, String> request) {
        boolean notifyOnlyBookmarked = request.get("notifyOnlyBookmarked").equals("true");
        return userService.postUserSetting(id, notifyOnlyBookmarked);
    }

    @GetMapping("/{id}/lostgoods")
    public List<LostGoods> getLostGoodsByUserId(@PathVariable long id) {
        return lostGoodsService.getLostGoodsByUserId(id);
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