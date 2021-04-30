package org.niitp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "params")
public class ParamsDocument {
    @Id
    private String id;
    private String typeV;
    private String pixelSizeKv;
    private String pixelSizeRpM;
    private String rangeRpM;
    private String pixelSizeRpP;
    private String rangeRpP;
    private String sunHeight;
    private String maxAngle;
    private String alignAccuracy;
    private String resolution;
    private String compressionType;
    private String compressionRate;

    public ParamsDocument(String typeV,
                          String pixelSizeKv,
                          String pixelSizeRpM,
                          String rangeRpM,
                          String pixelSizeRpP,
                          String rangeRpP,
                          String sunHeight,
                          String maxAngle,
                          String alignAccuracy,
                          String resolution,
                          String compressionType,
                          String compressionRate) {
        this.typeV = typeV;
        this.pixelSizeKv = pixelSizeKv;
        this.pixelSizeRpM = pixelSizeRpM;
        this.rangeRpM = rangeRpM;
        this.pixelSizeRpP = pixelSizeRpP;
        this.rangeRpP = rangeRpP;
        this.sunHeight = sunHeight;
        this.maxAngle = maxAngle;
        this.alignAccuracy = alignAccuracy;
        this.resolution = resolution;
        this.compressionType = compressionType;
        this.compressionRate = compressionRate;
    }
}
