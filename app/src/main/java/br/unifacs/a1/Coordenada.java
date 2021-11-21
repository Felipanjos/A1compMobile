package br.unifacs.a1;

public class Coordenada {

    private double longitude, latitude;
    private String data;

    public Coordenada() {

    }

    public Coordenada(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordenada(double latitude, double longitude, String data) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.data = data;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
