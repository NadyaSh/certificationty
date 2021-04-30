package org.niitp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Rpc implements Serializable {
    String nLineOff;
    String nSampOff;
    String nLatOff;
    String nLongOff;
    String nHeightOff;
    String nLineScale;
    String nSampScale;
    String nLatScale;
    String nLongScale;
    String nHeightScale;
    String bLineNum;
    String bLineDen;
    String bSampNum;
    String bSampDen;

    public Rpc(@JsonProperty(value = "nLineOff") String nLineOff,
               @JsonProperty(value = "nSampOff") String nSampOff,
               @JsonProperty(value = "nLatOff") String nLatOff,
               @JsonProperty(value = "nLongOff") String nLongOff,
               @JsonProperty(value = "nHeightOff") String nHeightOff,
               @JsonProperty(value = "nLineScale") String nLineScale,
               @JsonProperty(value = "nSampScale") String nSampScale,
               @JsonProperty(value = "nLatScale") String nLatScale,
               @JsonProperty(value = "nLongScale") String nLongScale,
               @JsonProperty(value = "nHeightScale") String nHeightScale,
               @JsonProperty(value = "bLineNum") String bLineNum,
               @JsonProperty(value = "bLineDen") String bLineDen,
               @JsonProperty(value = "bSampNum") String bSampNum,
               @JsonProperty(value = "bSampDen") String bSampDen) {
        this.nLineOff = nLineOff;
        this.nSampOff = nSampOff;
        this.nLatOff = nLatOff;
        this.nLongOff = nLongOff;
        this.nHeightOff = nHeightOff;
        this.nLineScale = nLineScale;
        this.nSampScale = nSampScale;
        this.nLatScale = nLatScale;
        this.nLongScale = nLongScale;
        this.nHeightScale = nHeightScale;
        this.bLineNum = bLineNum;
        this.bLineDen = bLineDen;
        this.bSampNum = bSampNum;
        this.bSampDen = bSampDen;
    }
}
