package DMU.demo.auth.service;

import DMU.demo.auth.dto.KakaoUserInfo;
import DMU.demo.auth.dto.KakaoToken;
import DMU.demo.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoService {
    private final static String AUTH_URI = "https://kauth.kakao.com";
    private final static String API_URI = "https://kapi.kakao.com";

    @Value("${kakao.client_id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.callback}")
    private String KAKAO_CALLBACK;

    public KakaoToken postKakaoAuth(String code) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", KAKAO_CLIENT_ID);
            params.add("code", code);
            params.add("redirect_uri", KAKAO_CALLBACK);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            ResponseEntity<KakaoToken> response = restTemplate.exchange(
                    AUTH_URI + "/oauth/token",
                    HttpMethod.POST,
                    httpEntity,
                    KakaoToken.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public KakaoUserInfo postLogin(KakaoToken token) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token.getAccess_token());
            headers.add("Content-type", "application/x-www-form-urlencoded");

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                    API_URI + "/v2/user/me",
                    HttpMethod.POST,
                    httpEntity,
                    KakaoUserInfo.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<String> postLogout(User user) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + user.getAccessToken());
            headers.add("Content-type", "application/x-www-form-urlencoded");

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

            return restTemplate.exchange(
                    API_URI + "/v1/user/logout",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<String> postUnlink(User user) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + user.getAccessToken());
            headers.add("Content-type", "application/x-www-form-urlencoded");

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

            return restTemplate.exchange(
                    API_URI + "/v1/user/unlink",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
