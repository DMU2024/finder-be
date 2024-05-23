package dmu.demo.DTO;

/**
 * DTO (Data Transfer Object) 클래스
 * 분실물 정보를 담는 클래스입니다.
 */
public class LostItem {
    private String id;
    private String location;
    private String description;
    private String date;
    private String type;

    public LostItem(String id, String location, String description, String date, String type) {
        this.id = id;
        this.location = location;
        this.description = description;
        this.date = date;
        this.type = type;
    }

    // Getter and Setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
