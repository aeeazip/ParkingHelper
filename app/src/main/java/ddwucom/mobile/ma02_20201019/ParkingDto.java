package ddwucom.mobile.ma02_20201019;

import java.io.Serializable;

public class ParkingDto implements Serializable {

    private long id;        // index
    private String name;    // 주차장 이름
    private String address; // 주차장 주소
    private String rating;  // 주차장 별점
    private String image;   // 메모 이미지
    private String memo;    // 메모 텍스트

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "ParkingDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", rating='" + rating + '\'' +
                ", image='" + image + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }
}
