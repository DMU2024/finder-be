package dmu.finder.kakao.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class KakaoUserInfo {
    private long id;
    private KakaoUserProperties properties;
}

