package dmu.finder.lostfound.domain.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document(collection = "lostfound")
public class LostFound {
    @Id
    private String _id;
    private String atcId;
    private String clrNm;
    private String depPlace;
    private String fdFilePathImg;
    private String fdPrdtNm;
    private String fdSbjt;
    private String fdSn;
    private String fdYmd;
    private String prdtClNm;
    private String rnum;
    private LocalDateTime updatedAt;
}

