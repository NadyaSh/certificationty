package org.niitp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CertificateData {
    private List<MyFileName> fileName = new ArrayList<>();
    private List<MetaData> metaData = new ArrayList<>();
    private List<MetaData> quickLook = new ArrayList<>();
    private List<MetaData> shapeFiles = new ArrayList<>();
    private List<MetaData> geo = new ArrayList<>();

    private List<ChannelRange> channelRange = new ArrayList<>();
    private List<ChannelAngle> channelAngle = new ArrayList<>();
    private List<Rpc> rpc = new ArrayList<>();
    private List<AccumulationLine> accumulationLines = new ArrayList<>();

    //постоянные
    private String level;
    private String typeV;
    private String equipment;
    private String pixelSize;
    private String range;
    private String sunHeight;
    private String maxAngle;
    private String alignAccuracy;
    private String resolution;
    private String compressionType;
    private String compressionRate;

    //переменные
    private String numberV;
    private String numberCoil;
    private String numberTurnOn;
    private String sceneDate;
    private String sceneTime;
    private String deltaTime;
    private String format;
    private String sideSize;
    private String angleOptSys;
    private String alpha;
    private String omega;
    private String kappa;
    private String sunHeightDuring;
    private String coordinateSystem;
    private String focalLength;
    private String orbitAltitude;
    private String slantRange;

    private String customer;
    private String region;
    private String applicationNumber;
    private String taskNumber;

    private String boss;
    private String controller;
    private String operator;

    private String idV;

    private String pdfName;

    public CertificateData(Input input) {
        this.level = "";
        this.typeV = "";
        this.equipment = "";
        this.pixelSize = "";
        this.range = "";
        this.sunHeight = "";
        this.maxAngle = "";
        this.alignAccuracy = "";
        this.resolution = "";
        this.compressionType = "";
        this.compressionRate = "";

        this.numberV = "";
        this.numberCoil = "";
        this.numberTurnOn = "";
        this.sceneDate = "";
        this.sceneTime = "";
        this.deltaTime = "";
        this.format = "";
        this.sideSize = "";
        this.angleOptSys = "";
        this.alpha = "";
        this.omega = "";
        this.kappa = "";
        this.sunHeightDuring = "";
        this.coordinateSystem = "";
        this.focalLength = "";
        this.orbitAltitude = "";
        this.slantRange = "";

        this.customer = input.getCustomer();
        this.region = input.getRegion();
        this.applicationNumber = input.getApplicationNumber();
        this.taskNumber = input.getTaskNumber();
        this.boss = input.getBoss();
        this.controller = input.getController();
        this.operator = input.getOperator();
        this.pdfName = "";
    }
}


