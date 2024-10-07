package DMU.demo.kakao.service;

import DMU.demo.bookmark.domain.repository.BookmarkRepository;
import DMU.demo.kakao.dto.*;
import DMU.demo.keyword.domain.repository.KeywordRepository;
import DMU.demo.user.domain.entity.User;
import DMU.demo.user.domain.repository.UserInfoMapping;
import DMU.demo.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final BookmarkRepository bookmarkRepository;

    private final RestClient authClient = RestClient.builder()
            .baseUrl("https://kauth.kakao.com")
            .defaultHeaders(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
            .build();

    private final RestClient apiClient = RestClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .defaultHeaders(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
            .requestInterceptor(new RequestInterceptor())
            .build();

    private class RequestInterceptor implements ClientHttpRequestInterceptor {
        private final List<String> ignorePath = List.of("/v2/user/me", "/v1/user/access_token_info");

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            String path = request.getURI().getPath();

            if (!ignorePath.contains(path)) {
                HttpHeaders httpHeaders = request.getHeaders();
                String authHeader = Objects.requireNonNull(httpHeaders.get("Authorization")).get(0);
                String token = authHeader.split(" ")[1];

                try {
                    KakaoTokenInfo tokenInfo = getTokenInfo(token);
                    log.info(tokenInfo.toString());
                } catch (HttpClientErrorException e) {
                    if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        User user = userRepository.findByAccessToken(token);
                        KakaoToken newToken = postRefreshToken(user.getRefreshToken());

                        user.setAccessToken(newToken.getAccess_token());
                        if (newToken.getRefresh_token() != null) {
                            user.setRefreshToken(newToken.getRefresh_token());
                        }
                        userRepository.save(user);

                        request.getHeaders().set("Authorization", "Bearer " + newToken.getAccess_token());
                    }
                }
            }

            return execution.execute(request, body);
        }
    }

    @Value("${kakao.client_id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.callback}")
    private String KAKAO_CALLBACK;

    @Value("${kakao.web_url}")
    private String KAKAO_WEB_URL;

    public KakaoToken postKakaoAuth(String code) {
        ResponseEntity<KakaoToken> response = authClient.post()
                .uri(uriBuilder -> uriBuilder.path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", KAKAO_CLIENT_ID)
                        .queryParam("code", code)
                        .queryParam("redirect_uri", KAKAO_CALLBACK)
                        .build())
                .retrieve()
                .toEntity(KakaoToken.class);

        return response.getBody();
    }

    public KakaoToken postRefreshToken(String refreshToken) {
        ResponseEntity<KakaoToken> response = authClient.post()
                .uri(uriBuilder -> uriBuilder.path("/oauth/token")
                        .queryParam("grant_type", "refresh_token")
                        .queryParam("client_id", KAKAO_CLIENT_ID)
                        .queryParam("refresh_token", refreshToken)
                        .build())
                .retrieve()
                .toEntity(KakaoToken.class);

        return response.getBody();
    }

    public UserInfoMapping postLogin(KakaoToken token) {
        ResponseEntity<KakaoUserInfo> response = apiClient.post()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + token.getAccess_token())
                .retrieve()
                .toEntity(KakaoUserInfo.class);

        KakaoUserInfo userInfo = response.getBody();
        KakaoUserProperties userProperties = userInfo.getProperties();

        userRepository.save(User.builder()
                .userId(userInfo.getId())
                .username(userProperties.getNickname())
                .profileImage(userProperties.getProfile_image())
                .thumbnailImage(userProperties.getThumbnail_image())
                .accessToken(token.getAccess_token())
                .refreshToken(token.getRefresh_token())
                .build());

        return userRepository.findByUserId(userInfo.getId());
    }

    public KakaoTokenInfo getTokenInfo(String accessToken) {
        ResponseEntity<KakaoTokenInfo> response = apiClient.get()
                .uri("/v1/user/access_token_info")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toEntity(KakaoTokenInfo.class);

        return response.getBody();
    }

    public String postLogout(User user) {
        ResponseEntity<KakaoUserInfo> response = apiClient.post()
                .uri("/v1/user/logout")
                .header("Authorization", "Bearer " + user.getAccessToken())
                .retrieve()
                .toEntity(KakaoUserInfo.class);

        removeToken(user);

        return "" + user.getUserId();
    }

    public String postUnlink(User user) {
        ResponseEntity<String> response = apiClient.post()
                .uri("/v1/user/unlink")
                .header("Authorization", "Bearer " + user.getAccessToken())
                .retrieve()
                .toEntity(String.class);

        keywordRepository.deleteAll(keywordRepository.findByUser(user));
        bookmarkRepository.deleteAll(bookmarkRepository.findByUser(user));
        userRepository.delete(user);

        return "" + user.getUserId();
    }

    private void removeToken(User user) {
        user.setAccessToken(null);
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    public KakaoScopeInfo getScopeInfo(User user) {
        ResponseEntity<KakaoScopeInfo> response = apiClient.get()
                .uri("/v2/user/scopes")
                .header("Authorization", "Bearer " + user.getAccessToken())
                .retrieve()
                .toEntity(KakaoScopeInfo.class);

        return response.getBody();
    }

    public KakaoScopeInfo postRevokeScope(User user, String[] scopes) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("scopes", "[" + Arrays.stream(scopes)
                .map(scope -> "\"" + scope + "\"").collect(Collectors.joining(",")) + "]");

        ResponseEntity<KakaoScopeInfo> response = apiClient.post()
                .uri("/v2/user/revoke/scopes")
                .header("Authorization", "Bearer " + user.getAccessToken())
                .body(formData)
                .retrieve()
                .toEntity(KakaoScopeInfo.class);

        return response.getBody();
    }

    public String postSendMessage(User user, String message, String image, String id) {
        JSONObject templateObject = new JSONObject(Map.of(
                "object_type", "feed",
                "content", new JSONObject(Map.of(
                        "title", message,
                        "image_url", image,
                        "image_width", 640,
                        "image_height", 640,
                        "link", new JSONObject(Map.of("web_url", KAKAO_WEB_URL + "/detail/" + id))
                ))
        ));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("template_object", templateObject.toString());

        ResponseEntity<String> response = apiClient.post()
                .uri("/v2/api/talk/memo/default/send")
                .header("Authorization", "Bearer " + user.getAccessToken())
                .body(formData)
                .retrieve()
                .toEntity(String.class);

        return response.getBody();
    }
}
