package org.niitp.component.realclient;

import lombok.extern.slf4j.Slf4j;
import org.niitp.model.*;
import org.niitp.mongo.ParamsDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ParseResursXml {

    @Autowired
    private ParamsDocumentRepository paramsDocumentRepository;

    private ProductData fdata;
    private boolean geographic = false;
    private String level = "";
    private String channel = "";
    private String xmlName;
    List<ChannelAngle> channelAngleList;
    List<ChannelRange> channelRangeList;
    List<AccumulationLine> accumulationList;

    public void setElement(Element element, ProductData productData, String fileName,
                           List<ChannelAngle> channelAngleList,
                           List<ChannelRange> channelRangeList,
                           List<AccumulationLine> accumulationList) {
        this.fdata = productData;
        this.xmlName = fileName;
        this.channelAngleList = channelAngleList;
        this.channelRangeList = channelRangeList;
        this.accumulationList = accumulationList;

        Optional<ParamsDocument> paramsDocument = paramsDocumentRepository.findByTypeV("Ресурс-П");
        if (!paramsDocument.isPresent()) {
            log.error("Нет постоянных параметров Ресурса");
            return;
        }

        fdata.setTypeV(paramsDocument.get().getTypeV());
        if (element.hasChildNodes()) {
            fdata.setSunHeight(paramsDocument.get().getSunHeight());
            fdata.setMaxAngle(paramsDocument.get().getMaxAngle());
            fdata.setAlignAccuracy(paramsDocument.get().getAlignAccuracy());
            fdata.setCompressionType(paramsDocument.get().getCompressionType());
            fdata.setCompressionRate(paramsDocument.get().getCompressionRate());
            NodeList list = element.getChildNodes();
            parseChildNodes(list, paramsDocument.get());
            fdata.setAlpha("");
            fdata.setOmega("");
            fdata.setKappa("");
        }
    }

    private void parseChildNodes(NodeList childlist, ParamsDocument paramsDocument) {
        String nNumberKA = "";
        String cDataFileName = "";
        String fileName = xmlName;
        fileName = fileName.replace("fr_", "");
        fileName = fileName.replace("_G", "");
        fileName = fileName.replace("_S", "");
        fileName = fileName.replace(".xml", "");

        String[] list = fileName.split("_");
        fdata.setNumberTurnOn(list[5]);
        fdata.setNumberCoil(list[4]);

        channel = "";
        if (fdata.getMetaData().size() > 1 && list.length == 7)
            channel = "канал " + list[list.length - 1];

        for (int i = 0; i < childlist.getLength(); i++) {
            if (childlist.item(i).getNodeName().equals("nNumberKA"))
                nNumberKA = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("Geographic"))
                parseGeographic(childlist.item(i).getChildNodes());
            if (childlist.item(i).getNodeName().equals("CoordinateSystem"))
                parseCoordinateSystem(childlist.item(i).getChildNodes());
            if (childlist.item(i).getNodeName().equals("Normal"))
                parseNormal(childlist.item(i).getChildNodes());
            if (childlist.item(i).getNodeName().equals("RPC"))
                parseRPC(childlist.item(i).getChildNodes());
            if (childlist.item(i).getNodeName().equals("Passport"))
                parsePassport(childlist.item(i).getChildNodes());
            if (childlist.item(i).getNodeName().equals("cDataFileName"))
                cDataFileName = childlist.item(i).getTextContent();
//            if (childlist.item(i).getNodeName().equals("cFileName_quicklook"))
//                childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("additionalInfo")) {
                List<AddInfo> addInfo = new ArrayList<>();
                addInfo.add(new AddInfo(childlist.item(i).getTextContent()));
                fdata.setAddInfo(addInfo);
            }
        }
        fdata.setNumberV(nNumberKA);
        if (level.equals("1"))
            level = "1A";
        if (xmlName.contains("_S."))
            level = level + "1";
        fdata.setLevel(level);
        String format = cDataFileName.substring(cDataFileName.indexOf(".") + 1);
        Pattern p = Pattern.compile("tif|tiff");
        Pattern p2 = Pattern.compile("[Ii]mg");
        if (p.matcher(format).find() && level.contains("2A")) {
            format = "GeoTIFF";
        } else if (p.matcher(format).find()) {
            format = "TIFF";
        } else if (p2.matcher(format).find()) {
            format = "IMG";
        }
        fdata.setFormat(format);
        if (xmlName.contains("S")) {
            fdata.setPixelSize(paramsDocument.getPixelSizeRpM());
            fdata.setRange(paramsDocument.getRangeRpM());
        } else {
            fdata.setPixelSize(paramsDocument.getPixelSizeRpP());
            fdata.setRange(paramsDocument.getRangeRpP());
        }
        for (int j = 0; j < fdata.getFileName().size(); j++) {
            String name = fdata.getFileName().get(j).getData();
            if (name.equals(cDataFileName))
                fdata.getFileName().get(j).setLabel(channel);
        }
    }

    private void parseGeographic(NodeList childlist) {
        geographic = true;
        String aNWLat = "";
        String aNWLong = "";
        String aNELat = "";
        String aNELong = "";
        String aSELat = "";
        String aSELong = "";
        String aSWLat = "";
        String aSWLong = "";
        String aMidLat = "";
        String aMidLong = "";
        for (int i = 0; i < childlist.getLength(); i++) {
            if (childlist.item(i).getNodeName().equals("aNWLat"))
                aNWLat = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("aNWLong"))
                aNWLong = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("aNELat"))
                aNELat = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("aNELong"))
                aNELong = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("aSELat"))
                aSELat = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("aSELong"))
                aSELong = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("aSWLat"))
                aSWLat = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("aSWLong"))
                aSWLong = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("aMidLat"))
                aMidLat = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("aMidLong"))
                aMidLong = childlist.item(i).getTextContent();
        }
        channelAngleList.add(new ChannelAngle(channel, aNWLat, aNWLong, aNELat, aNELong, aSELat, aSELong, aSWLat,
                aSWLong, aMidLat, aMidLong));
    }

    private void parseCoordinateSystem(NodeList childlist) {
        for (int i = 0; i < childlist.getLength(); i++) {
            if (childlist.item(i).getNodeName().equals("cCoordSystName"))
                fdata.setCoordinateSystem(childlist.item(i).getTextContent());
        }
    }

    private void parseNormal(NodeList childlist) {
        Map minList = new HashMap();
        Map maxList = new HashMap();
        String aNWLat = "";
        String aNWLong = "";
        String aNELat = "";
        String aNELong = "";
        String aSELat = "";
        String aSELong = "";
        String aSWLat = "";
        String aSWLong = "";
        String aMidLat = "";
        String aMidLong = "";
        String pixelImg = "";
        String pixelImgSrc = "";
        for (int i = 0; i < childlist.getLength(); i++) {
            if (childlist.item(i).getNodeName().equals("aSunElevC"))
                fdata.setSunHeightDuring(childlist.item(i).getTextContent());
            String smin = "nSRMinChannel";
            if (childlist.item(i).getNodeName().contains(smin)) {
                String pos = childlist.item(i).getNodeName().substring(smin.length());
                minList.put(pos, childlist.item(i).getTextContent());
            }
            String smax = "nSRMaxChannel";
            if (childlist.item(i).getNodeName().contains(smax)) {
                String pos = childlist.item(i).getNodeName().substring(smax.length());
                maxList.put(pos, childlist.item(i).getTextContent());
            }

            if (childlist.item(i).getNodeName().equals("dSceneDate"))
                fdata.setSceneDate(childlist.item(i).getTextContent());
            if (childlist.item(i).getNodeName().equals("tSceneTime"))
                fdata.setSceneTime(childlist.item(i).getTextContent());
            if (childlist.item(i).getNodeName().equals("nDeltaTime"))
                fdata.setDeltaTime(childlist.item(i).getTextContent());
            if (childlist.item(i).getNodeName().equals("nPixelImg"))
                pixelImg = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("nPixelImgSrc"))
                pixelImgSrc = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("aAngleSum"))
                fdata.setAngleOptSys(childlist.item(i).getTextContent());
            if (childlist.item(i).getNodeName().equals("cLevel"))
                level = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("nBitsPerPixel"))
                fdata.setResolution(childlist.item(i).getTextContent());

            if (!geographic) {
                if (childlist.item(i).getNodeName().equals("nLUpNord"))
                    aNWLat = childlist.item(i).getTextContent();
                if (childlist.item(i).getNodeName().equals("nLUpEast"))
                    aNWLong = childlist.item(i).getTextContent();
                if (childlist.item(i).getNodeName().equals("nRUpNord"))
                    aNELat = childlist.item(i).getTextContent();
                if (childlist.item(i).getNodeName().equals("nRUpEast"))
                    aNELong = childlist.item(i).getTextContent();
                if (childlist.item(i).getNodeName().equals("nRDownNord"))
                    aSELat = childlist.item(i).getTextContent();
                if (childlist.item(i).getNodeName().equals("nRDownEast"))
                    aSELong = childlist.item(i).getTextContent();
                if (childlist.item(i).getNodeName().equals("nLDownNord"))
                    aSWLat = childlist.item(i).getTextContent();
                if (childlist.item(i).getNodeName().equals("nLDownEast"))
                    aSWLong = childlist.item(i).getTextContent();
            }
        }
        if (!geographic) {
            channelAngleList.add(new ChannelAngle(channel, aNWLat, aNWLong, aNELat, aNELong, aSELat, aSELong, aSWLat,
                    aSWLong, aMidLat, aMidLong));
        }

        if (!pixelImg.isEmpty())
            fdata.setSideSize(pixelImg);
        else
            fdata.setSideSize(pixelImgSrc);

        if (minList.size() > 1 && minList.size() == maxList.size()) {
            for (Object key : minList.keySet()) {
                String num = "";
                String numch = new BigDecimal(minList.get(key).toString()).setScale(2, RoundingMode.HALF_UP).toString();
                if (numch.contains("0.4"))
                    num = "33";
                else if (numch.contains("0.5"))
                    num = "23";
                else if (numch.contains("0.6"))
                    num = "21";
                else if (numch.contains("0.7"))
                    num = "22";

                channelRangeList.add(new ChannelRange("канал " + num, minList.get(key) + "-" + maxList.get(key)));
                accumulationList.add(new AccumulationLine("канал " + num, "0"));
            }
        } else if (minList.size() == 1) {
            for (Object key : minList.keySet()) {
                channelRangeList.add(new ChannelRange(channel, minList.get(key) + "-" + maxList.get(key)));
                accumulationList.add(new AccumulationLine(channel, "0"));
            }
        } else {
            channelRangeList.add(new ChannelRange("", ""));
            accumulationList.add(new AccumulationLine("", ""));
        }
    }

    private void parseRPC(NodeList childlist) {
        String nLineOff = "";
        String nSampOff = "";
        String nLatOff = "";
        String nLongOff = "";
        String nHeightOff = "";
        String nLineScale = "";
        String nSampScale = "";
        String nLatScale = "";
        String nLongScale = "";
        String nHeightScale = "";
        String bLineNum = "";
        String bLineDen = "";
        String bSampNum = "";
        String bSampDen = "";
        for (int i = 0; i < childlist.getLength(); i++) {
            if (childlist.item(i).getNodeName().equals("nLineOff"))
                nLineOff = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("nSampOff"))
                nSampOff = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("nLatOff"))
                nLatOff = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("nLongOff"))
                nLongOff = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("nHeightOff"))
                nHeightOff = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("nLineScale"))
                nLineScale = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("nSampScale"))
                nSampScale = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("nLatScale"))
                nLatScale = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("nLongScale"))
                nLongScale = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("nHeightScale"))
                nHeightScale = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("bLineNum"))
                bLineNum = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("bLineDen"))
                bLineDen = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("bSampNum"))
                bSampNum = childlist.item(i).getTextContent();
            if (childlist.item(i).getNodeName().equals("bSampDen"))
                bSampDen = childlist.item(i).getTextContent();
        }
        List<Rpc> rpc = new ArrayList<>();
        rpc.add(new Rpc(nLineOff, nSampOff, nLatOff, nLongOff, nHeightOff,
                nLineScale, nSampScale, nLatScale, nLongScale, nHeightScale, bLineNum, bLineDen, bSampNum, bSampDen));
        fdata.setRPC(rpc);
    }

    private void parsePassport(NodeList childlist) {
        for (int i = 0; i < childlist.getLength(); i++) {
            if (childlist.item(i).getNodeName().equals("cDeviceName")) {
                String str = childlist.item(i).getTextContent();
                if (str.contains("GEOTONP"))
                    fdata.setEquipment("Геотон-Л1");
                if (str.contains("KSHMSAH"))
                    fdata.setEquipment("КШМСА-ВР");
                if (str.contains("KSHMSAM"))
                    fdata.setEquipment("КШМСА-СР");
            }
//            if (childlist.item(i).getNodeName().equals("nBitsPerPixel"))
//                fdata.setResolution(childlist.item(i).getTextContent());
        }
    }

    public void setAdditionalElement(Element element, ProductData fdata, String fileName) {
        if (element.hasChildNodes()) {
            NodeList childlist = element.getChildNodes();
            String cDataFileName = "";
            fileName = fileName.replace("fr_", "");
            fileName = fileName.replace("_G", "");
            fileName = fileName.replace("_S", "");
            fileName = fileName.replace(".xml", "");

            String[] list = fileName.split("_");
            channel = "";
            if (fdata.getMetaData().size() > 1 && list.length == 7) {
                channel = "канал " + list[list.length - 1];
                for (int i = 0; i < childlist.getLength(); i++) {
                    if (childlist.item(i).getNodeName().equals("Geographic"))
                        parseGeographic(childlist.item(i).getChildNodes());
                    if (childlist.item(i).getNodeName().equals("Normal"))
                        parseNormal(childlist.item(i).getChildNodes());
                    if (childlist.item(i).getNodeName().equals("cDataFileName"))
                        cDataFileName = childlist.item(i).getTextContent();
                }
                for (int j = 0; j < fdata.getFileName().size(); j++) {
                    String name = fdata.getFileName().get(j).getData();
                    if (name.equals(cDataFileName))
                        fdata.getFileName().get(j).setLabel(channel);
                }
            }
        }
    }
}
