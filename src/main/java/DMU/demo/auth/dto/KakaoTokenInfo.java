package DMU.demo.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoTokenInfo {
    private long id;
    private int expires_in;
    private int app_id;
}
