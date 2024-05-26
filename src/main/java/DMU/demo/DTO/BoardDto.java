package DMU.demo.DTO;

import DMU.demo.domain.entity.Board;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardDto {
    private Integer lost_id;
    private String user_id;
    private String lost_name;
    private String category;
    private String field;
    private Date lost_date;
    private String lost_location;
    private String lost_status;
    private byte[] lost_img;
    private String imageBase64;

    public Board toEntity() {
        return Board.builder()
                .lost_id(lost_id)
                .user_id(user_id)
                .lost_name(lost_name)
                .category(category)
                .field(field)
                .lost_date(lost_date)
                .lost_location(lost_location)
                .lost_status(lost_status)
                .lost_img(lost_img)
                .build();
    }

    @Builder
    public BoardDto(Integer lost_id, String user_id, String lost_name, String category, String field, Date lost_date, String lost_location, String lost_status, byte[] lost_img) {
        this.lost_id = lost_id;
        this.user_id = user_id;
        this.lost_name = lost_name;
        this.category = category;
        this.field = field;
        this.lost_date = lost_date;
        this.lost_location = lost_location;
        this.lost_status = lost_status;
        this.lost_img = lost_img;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }


}