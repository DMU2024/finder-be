package DMU.demo.lostgoods.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document(collection = "lostgoods")
public class LostGoods {
    @Id
    private String _id;
    private String name;
    private String date;
    private String address;
    private String category;
    private String info;
    private float lat;
    private float lng;
}

