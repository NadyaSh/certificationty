package org.niitp.component.realclient;

import lombok.extern.slf4j.Slf4j;
import org.niitp.model.*;
import org.niitp.mongo.ParamsDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ParseKanopusXml {

    @Autowired
    private ParamsDocumentRepository paramsDocumentRepository;

    private ProductData fdata;
    private String xmlName;

    public void setElement(Element element, ProductData productData, String xmlName) {
        this.fdata = productData;
        this.xmlName = xmlName;

        Optional<ParamsDocument> paramsDocument = paramsDocumentRepository.findByTypeV("Канопус-В");
        if (!paramsDocument.isPresent()) {
            log.error("Нет постоянных параметров Канопуса");
            return;
        }

        fdata.setTypeV(paramsDocument.get().getTypeV());
        if (element.hasChildNodes()) {
            fdata.setPixelSize(paramsDocument.get().getPixelSizeKv());
            fdata.setSunHeight(paramsDocument.get().getSunHeight());
            fdata.setMaxAngle(paramsDocument.get().getMaxAngle());
            fdata.setAlignAccuracy(paramsDocument.get().getAlignAccuracy());
            fdata.setResolution(paramsDocument.get().getResolution());
            NodeList list = element.getChildNodes();
            parseChildNodes(list, element.getTextContent());
        }
    }

    private String findValue(String paramName, String str) {
        Pattern p = Pattern.compile(paramName + " = .+[\n]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            String temp = m.group().split(" = ")[1];
            if (temp.contains("\""))
                temp = temp.replaceAll("\"", "");
            if (temp.contains("\n"))
                temp = temp.replaceAll("\n", "");
            return temp;
        } else
            return "";
    }

    public String findCoordSystem(String paramName, String str) {
        String[] list = str.split("\n");
        String temp = "";
        for (String i : list) {
            if (i.contains("cCoordSystName")) {
                temp = i.split(" = ")[1];
                if (temp.contains("\""))
                    temp = temp.replaceAll("\"", "");
                if (temp.contains("\n"))
                    temp = temp.replaceAll("\n", "");
            }
        }

        return temp;
    }

    private void parseChildNodes(NodeList childlist, String text) {

        String level = findValue("cProcLevel", text);

        fdata.setSceneTime(findValue("tSessionTime", text));
        fdata.setSceneDate(findValue("dSessionDate", text));
        String h = findValue("bSunAngle", text);
        if (!h.isEmpty()) {
            String[] list = h.split(", ");
            if (list.length > 0)
                fdata.setSunHeightDuring(list[0]);
        }
        for (int i = 0; i < childlist.getLength(); i++) {
            if (childlist.item(i).getNodeName().equals("Device"))
                parseDevice(childlist.item(i).getChildNodes(), childlist.item(i).getTextContent());
            if (childlist.item(i).getNodeName().equals("Geo")) {
                NodeList childlist2 = childlist.item(i).getChildNodes();
                for (int j = 0; j < childlist2.getLength(); j++) {
                    if (childlist2.item(j).getNodeName().equals("Orientation"))
                        parseOrientation(childlist2.item(j).getTextContent());
                    if (childlist2.item(j).getNodeName().equals("RPC"))
                        parseRPC(childlist2.item(j).getTextContent());
                    if (childlist2.item(j).getNodeName().equals("GeoCoding"))
                        parseGeoCoding(childlist2.item(j).getTextContent());
                    if (childlist2.item(j).getNodeName().equals("CSGeoRef"))
                        parseCSGeoRef(childlist2.item(j).getTextContent());
                }
            }
            if (childlist.item(i).getNodeName().equals("additionalInfo")) {
                List<AddInfo> addInfo = new ArrayList<>();
                addInfo.add(new AddInfo(childlist.item(i).getTextContent()));
                fdata.setAddInfo(addInfo);
            }
        }
        String cDataFileName = findValue("cDataFileName", text);
        String fileName = xmlName;
        if (fileName.contains("fr"))
            fileName = fileName.substring(fileName.indexOf('_', 0) + 1);
        String[] list = fileName.split("_");
        if (list.length > 3) {
            fdata.setNumberV(list[0]);
            fdata.setNumberTurnOn(list[3]);
            fdata.setNumberCoil(list[2]);
        }

        if (fdata.getRange().equals("мультиспектральный")) {
            if (fdata.getFileName().size() == 1 && (fdata.getFileName().get(0).getData().contains(".tif") || fdata.getFileName().get(0).getData().contains(".img")))
                level = level + "1";

            if (fdata.getFileName().size() == 2 && fdata.getFileName().stream().anyMatch(myFileName -> myFileName.getData().contains(".ige")))
                level = level + "1";
        }
        fdata.setLevel(level);
        String format = cDataFileName.substring(cDataFileName.indexOf('.') + 1);
        Pattern p = Pattern.compile("tif|tiff");
        Pattern p2 = Pattern.compile("[Ii]mg");
        if (p.matcher(format).find() && level.contains("2A"))
            format = "GeoTIFF";
        else if (p.matcher(format).find())
            format = "TIFF";
        else if (p2.matcher(format).find())
            format = "IMG";
        fdata.setFormat(format);
        if (fdata.getFileName().size() > 1)
            for (int i = 0; i < fdata.getFileName().size(); i++) {
                String name = fdata.getFileName().get(i).getData();
                String xname = xmlName;
                xname = xname.substring(0, xname.indexOf('.'));
                name = name.substring(0, name.indexOf('.'));
                if (xname.length() < name.length()) {
                    String[] strlist = name.split("_");
                    fdata.getFileName().get(i).setLabel("канал " + strlist[strlist.length - 1]);
                }
            }
        fdata.setDeltaTime("");
        fdata.setSideSize("");
    }

    private void parseDevice(NodeList childlist, String text) {
        Map list = new HashMap();
        String str = findValue("cDeviceName", text);
        if (str.contains("PSS")) {
            fdata.setEquipment("ПСС");
            fdata.setRange("панхроматический");
        } else {
            fdata.setEquipment("МСС");
            fdata.setRange("мультиспектральный");
        }
        List<ChannelRange> channelRange = new ArrayList<>();
        List<AccumulationLine> accumulationLines = new ArrayList<>();
        for (int i = 0; i < childlist.getLength(); i++) {
            if (childlist.item(i).getNodeName().contains("Ch")) {
                String num = findValue("nChannelNumber", childlist.item(i).getTextContent());
                String s = findValue("bSpectralZone", childlist.item(i).getTextContent());
                if (!s.isEmpty())
                    list.put(num, s.replace(", ", "-"));
            }
        }
        if (list.size() > 1) {
            for (Object key : list.keySet()) {
                channelRange.add(new ChannelRange("канал " + key, list.get(key).toString()));
                accumulationLines.add(new AccumulationLine("канал " + key, "0"));
            }
        } else if (list.size() == 1) {
            for (Object key : list.keySet()) {
                channelRange.add(new ChannelRange("", list.get(key).toString()));
                accumulationLines.add(new AccumulationLine("", "15"));
            }
        } else {
            channelRange.add(new ChannelRange("", ""));
            accumulationLines.add(new AccumulationLine("", ""));
        }
        fdata.setChannelRange(channelRange);
        fdata.setAccumulationLines(accumulationLines);
    }

    private void parseOrientation(String text) {
        fdata.setAlpha(findValue("nAlpha", text));
        fdata.setOmega(findValue("nOmega", text));
        fdata.setKappa(findValue("nKappa", text));
    }

    private void parseRPC(String text) {
        String nLineOff = findValue("nLineOff", text);
        String nSampOff = findValue("nSampOff", text);
        String nLatOff = findValue("nLatOff", text);
        String nLongOff = findValue("nLongOff", text);
        String nHeightOff = findValue("nHeightOff", text);
        String nLineScale = findValue("nLineScale", text);
        String nSampScale = findValue("nSampScale", text);
        String nLatScale = findValue("nLatScale", text);
        String nLongScale = findValue("nLongScale", text);
        String nHeightScale = findValue("nHeightScale", text);
        String bLineNum = findValue("bLineNum", text);
        String bLineDen = findValue("bLineDen", text);
        String bSampNum = findValue("bSampNum", text);
        String bSampDen = findValue("bSampDen", text);
        List<Rpc> rpc = new ArrayList<>();
        rpc.add(new Rpc(nLineOff, nSampOff, nLatOff, nLongOff, nHeightOff, nLineScale, nSampScale,
                nLatScale, nLongScale, nHeightScale, bLineNum, bLineDen, bSampNum, bSampDen));
        fdata.setRPC(rpc);
    }

    private void parseGeoCoding(String text) {
        fdata.setCoordinateSystem(findCoordSystem("сCoordSystName", text));
    }

    private void parseCSGeoRef(String text) {
        String aNWLat = findValue("nLUpLat", text);
        String aNWLong = findValue("nLUpLon", text);
        String aNELat = findValue("nRUpLat", text);
        String aNELong = findValue("nRUpLon", text);
        String aSELat = findValue("nRDownLat", text);
        String aSELong = findValue("nRDownLon", text);
        String aSWLat = findValue("nLDownLat", text);
        String aSWLong = findValue("nLDownLon", text);
        String aMidLat = "";
        String aMidLong = " ";
        List<ChannelAngle> channelAngle = new ArrayList<>();
        channelAngle.add(new ChannelAngle("", aNWLat, aNWLong, aNELat, aNELong, aSELat, aSELong, aSWLat, aSWLong,
                aMidLat, aMidLong));
        fdata.setChannelAngle(channelAngle);
    }
}
