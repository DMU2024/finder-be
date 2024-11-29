package dmu.finder.kakao.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class KakaoUserProperties {
    private String nickname;
    private String profile_image;
    private String thumbnail_image;
}
