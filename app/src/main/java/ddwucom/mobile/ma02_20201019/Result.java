package ddwucom.mobile.ma02_20201019;

import java.io.Serializable;

public class Result implements Serializable {
    String name;
    Double lat;
    Double lng;
    String address;
    float far;
    String rating;

    public Result(String name, Double lat, Double lng, float far, String rating) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.far = far;
        this.rating = rating;
    }

    public Result(String name, Double lat, Double lng, String address, float far, String rating) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.far = far;
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
