package org.niitp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductData {

    private List<MyFileName> fileName = new ArrayList<>();
    private List<MetaData> metaData = new ArrayList<>();
    private List<MetaData> quickLook = new ArrayList<>();
    private List<MetaData> shapeFiles = new ArrayList<>();
    private List<MetaData> geo = new ArrayList<>();

    private List<ChannelRange> channelRange = new ArrayList<>();
    private List<ChannelAngle> channelAngle = new ArrayList<>();
    private List<Rpc> RPC = new ArrayList<>();
    private List<AddInfo> addInfo = new ArrayList<>();
    private List<AccumulationLine> accumulationLines = new ArrayList<>();

    //постоянные
    private String level = "";
    private String typeV = "";
    private String equipment = "";
    private String pixelSize = "";
    private String range = "";
    private String sunHeight = "";
    private String maxAngle = "";
    private String alignAccuracy = "";
    private String resolution = "";
    private String compressionType = "";
    private String compressionRate = "";

    //переменные
    private String numberV = "";
    private String numberCoil = "";
    private String numberTurnOn = "";
    private String sceneDate = "";
    private String sceneTime = "";
    private String deltaTime = "";
    private String format = "";
    private String sideSize = "";
    private String angleOptSys = "";
    private String alpha = "";//крен
    private String omega = "";//тангаж
    private String kappa = "";//рыскание
    private String sunHeightDuring = "";
    private String coordinateSystem = "";
    private String focalLength = "";
    private String orbitAltitude = "";
    private String slantRange = "";

    private String errormess = "";
    private String customer = "";
    private String region = "";
    private String applicationNumber = "";
    private String taskNumber = "";

    private String boss = "";
    private String controller = "";
    private String operator = "";

    private String filesPath = "";
    private String pdfpath = "";
    private Long xmlid;
    private String idV = "";

    private String pdfName;

}


