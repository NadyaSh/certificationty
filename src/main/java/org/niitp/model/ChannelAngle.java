package org.niitp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelAngle implements Serializable {
    String name;
    String aNWLat;
    String aNWLong;
    String aNELat;
    String aNELong;
    String aSELat;
    String aSELong;
    String aSWLat;
    String aSWLong;
    String aMidLat;
    String aMidLong;

    public ChannelAngle(@JsonProperty(value = "name") String name,
                        @JsonProperty(value = "aNWLat") String aNWLat,
                        @JsonProperty(value = "aNWLong") String aNWLong,
                        @JsonProperty(value = "aNELat") String aNELat,
                        @JsonProperty(value = "aNELong") String aNELong,
                        @JsonProperty(value = "aSELat") String aSELat,
                        @JsonProperty(value = "aSELong") String aSELong,
                        @JsonProperty(value = "aSWLat") String aSWLat,
                        @JsonProperty(value = "aSWLong") String aSWLong,
                        @JsonProperty(value = "aMidLat") String aMidLat,
                        @JsonProperty(value = "aMidLong") String aMidLong) {
        this.name = name;
        this.aNWLat = aNWLat;
        this.aNWLong = aNWLong;
        this.aNELat = aNELat;
        this.aNELong = aNELong;
        this.aSELat = aSELat;
        this.aSELong = aSELong;
        this.aSWLat = aSWLat;
        this.aSWLong = aSWLong;
        this.aMidLat = aMidLat;
        this.aMidLong = aMidLong;
    }
}
