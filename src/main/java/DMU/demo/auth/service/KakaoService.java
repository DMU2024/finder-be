package DMU.demo.auth.service;

import DMU.demo.auth.dto.KakaoToken;
import DMU.demo.auth.dto.KakaoTokenInfo;
import DMU.demo.auth.dto.KakaoUserInfo;
import DMU.demo.auth.dto.KakaoUserProperties;
import DMU.demo.keyword.domain.repository.KeywordRepository;
import DMU.demo.location.domain.repository.LocationRepository;
import DMU.demo.user.domain.entity.User;
import DMU.demo.user.domain.repository.UserInfoMapping;
import DMU.demo.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final LocationRepository locationRepository;

    private final RestClient authClient = RestClient.builder()
            .baseUrl("https://kauth.kakao.com")
            .defaultHeader("Content-type", "application/x-www-form-urlencoded")
            .build();

    private final RestClient apiClient = RestClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .defaultHeader("Content-type", "application/x-www-form-urlencoded")
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
                        user.setRefreshToken(newToken.getRefresh_token());
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
        locationRepository.deleteAll(locationRepository.findByUser(user));
        userRepository.delete(user);

        return "" + user.getUserId();
    }

    private void removeToken(User user) {
        user.setAccessToken(null);
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}
