package DMU.demo.bookmark.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookmarkDto {
    private int id;
    private String location;
    private String address;
    private float lat;
    private float lng;
}
