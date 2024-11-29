package dmu.finder.kakao.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class KakaoScopeInfo {
    private long id;
    private List<KakaoScope> scopes;
}