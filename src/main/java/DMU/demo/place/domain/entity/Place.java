package DMU.demo.place.domain.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "place")
public class Place {
    @Id
    private String _id;
    private String name;
    private String address;
    private String category;
    private String info;
    private float lat;
    private float lng;
}
