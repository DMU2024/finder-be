package dmu.finder.lostfound.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LostFoundDetail {
    private String atcId;
    private String csteSteNm;
    private String depPlace;
    private String fdFilePathImg;
    private String fdHor;
    private String fdPlace;
    private String fdPrdtNm;
    private String fdSn;
    private String fdYmd;
    private String fndKeepOrgnSeNm;
    private String orgId;
    private String orgNm;
    private String prdtClNm;
    private String tel;
    private String uniq;

    private float lat;
    private float lng;
}
