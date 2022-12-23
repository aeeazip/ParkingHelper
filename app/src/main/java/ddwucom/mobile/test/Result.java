package ddwucom.mobile.test;

import java.io.Serializable;

public class Result implements Serializable {
    String name;
    Double lat;
    Double lng;
    String address;
    float far;

    public Result(String name, Double lat, Double lng, float far) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.far = far;
    }

    public Result(String name, Double lat, Double lng, String address, float far) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.far = far;
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
