package dmu.finder.kakao.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoToken {
    private String token_type;
    private String access_token;
    private int expires_in;
    private String refresh_token;
    private int refresh_token_expires_in;
    private String scope;
}
