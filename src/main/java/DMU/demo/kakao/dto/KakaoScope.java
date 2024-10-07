package DMU.demo.kakao.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoScope {
    private String id;
    private String display_name;
    private String type;
    private boolean using;
    private boolean agreed;
    private boolean revocable;
}
