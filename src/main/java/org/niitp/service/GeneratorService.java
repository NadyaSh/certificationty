package org.niitp.service;

import com.lowagie.text.pdf.BaseFont;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.niitp.component.pdf.ImgReplacedElementFactory;
import org.niitp.exception.AppException;
import org.niitp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GeneratorService {

    final private TemplateEngine templateEngine = new TemplateEngine();

    @Autowired
    private ChannelService channelService;

    private String vehicleType;

    public byte[] createPdf(String templateName, CertificateData object) {

        Context context = new Context();
        vehicleType = object.getIdV();

        context.setVariable("fileName", checkFileName(object.getFileName()));
        context.setVariable("metaData", object.getMetaData());
        context.setVariable("quickLook", object.getQuickLook());
        context.setVariable("shapeFiles", object.getShapeFiles());
        context.setVariable("geo", object.getGeo());

        context.setVariable("channelRange", checkChannelRange(object.getChannelRange()));
        context.setVariable("channelAngle", checkChannelAngle(object.getChannelAngle()));
        context.setVariable("RPC", object.getRpc());

        context.setVariable("customer", object.getCustomer());
        context.setVariable("region", object.getRegion());
        context.setVariable("applicationNumber", object.getApplicationNumber());
        context.setVariable("boss", object.getBoss());
        context.setVariable("controller", object.getController());
        context.setVariable("operator", object.getOperator());

        context.setVariable("level", object.getLevel());
        context.setVariable("typeV", object.getTypeV());
        context.setVariable("equipment", object.getEquipment());
        context.setVariable("pixelSize", checkPixelSize(object.getPixelSize()));
        context.setVariable("range", object.getRange());
        context.setVariable("sunHeight", object.getSunHeight());
        context.setVariable("maxAngle", object.getMaxAngle());
        context.setVariable("alignAccuracy", object.getAlignAccuracy());
        context.setVariable("resolution", object.getResolution());
        context.setVariable("compressionType", object.getCompressionType());
        context.setVariable("compressionRate", object.getCompressionRate());

        context.setVariable("numberV", object.getNumberV());

        context.setVariable("numberCoil", object.getNumberCoil());
        context.setVariable("numberTurnOn", object.getNumberTurnOn());

        context.setVariable("sceneDate", checkSceneDate(object.getSceneDate()));
        context.setVariable("sceneTime", checkSceneTime(object.getSceneTime()));
        context.setVariable("deltaTime", checkDeltaTime(object.getDeltaTime()));

        context.setVariable("format", object.getFormat());

        context.setVariable("sideSize", checkSideSize(object.getSideSize()));
        context.setVariable("angleOptSys", checkAngleOptSys(object.getAngleOptSys()));
        context.setVariable("alpha", checkKAODegree(object.getAlpha()));
        context.setVariable("omega", checkKAODegree(object.getOmega()));
        context.setVariable("kappa", checkKAODegree(object.getKappa()));
        context.setVariable("sunHeightDuring", checkSunHeightDuring(object.getSunHeightDuring()));
        context.setVariable("coordinateSystem", object.getCoordinateSystem());

        context.setVariable("focalLength", object.getFocalLength());
        context.setVariable("orbitAltitude", doubleHalfUp(object.getOrbitAltitude()));
        context.setVariable("slantRange", doubleHalfUp(object.getSlantRange()));

        context.setVariable("accumulationLines", checkCAccumulationLine(object.getAccumulationLines()));

        Assert.notNull(templateName, "The templateName can not be null");
        String processedHtml = templateEngine.process(templateName, context);
        try {
            ITextRenderer renderer = new ITextRenderer();
            final ClassPathResource regular = new ClassPathResource("static/fonts/times.ttf");
            renderer.getFontResolver().addFont(regular.getURL().toString(), BaseFont.IDENTITY_H, true);
            SharedContext sharedContext = renderer.getSharedContext();
            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);
            sharedContext.setReplacedElementFactory(new ImgReplacedElementFactory());
            sharedContext.getTextRenderer().setSmoothingThreshold(0);
            renderer.setDocumentFromString(processedHtml);
            renderer.layout();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            renderer.createPDF(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Не удалось сгенерировать pdf" + e);
            throw new AppException("Не удалось сгенерировать pdf" + e);
        }
    }


    private String checkPixelSize(String data) {
        String[] strlist = data.split("\\*");
        for (int i = 0; i < strlist.length; i++) {
            strlist[i] = strlist[i] + "мкм";
        }
        data = StringUtils.join(strlist, '*');
        return data;
    }

    private String checkSceneDate(String data) {
        if (data.contains("/"))
            data = data.replace("/", ".");
        return data;
    }

    private String checkSceneTime(String data) {
        if (data.contains(":")) {
            String[] strlist = data.split(":");
            double d = Double.parseDouble(strlist[2]);
            int newInt = BigDecimal.valueOf(d).setScale(0, RoundingMode.HALF_UP).intValue();
            data = strlist[0] + " час " + strlist[1] + " мин " + Integer.toString(newInt) + " сек";
        }
        return data;
    }

    private String checkDeltaTime(String data) {
        if (!data.isEmpty()) {
            double d = Double.parseDouble(data);
            double newDouble = BigDecimal.valueOf(d).setScale(1, RoundingMode.HALF_UP).doubleValue();
            data = Double.toString(newDouble);
        }
        return data;
    }

    private String checkSideSize(String data) {
        if (!data.isEmpty() && !data.equals("н/д")) {
            double d = Double.parseDouble(data);
            double newDouble = BigDecimal.valueOf(d).setScale(1, RoundingMode.HALF_UP).doubleValue();
            data = Double.toString(newDouble);
        }
        return data;
    }

    private String checkAngleOptSys(String data) {
        if (!data.isEmpty() && !data.equals("н/д")) {
            if (data.contains(":"))
                data = degreeminsecToDoubleStr(data);
            double d = Double.parseDouble(data);
            double newDouble = BigDecimal.valueOf(d).setScale(1, RoundingMode.HALF_UP).doubleValue();
            data = Double.toString(newDouble);
        }
        return data;
    }

    private String checkKAODegree(String data) {
        if (!data.isEmpty() && !data.equals("н/д")) {
            double d = Double.parseDouble(data);
            double newDouble = BigDecimal.valueOf(d).setScale(1, RoundingMode.HALF_UP).doubleValue();
            data = Double.toString(newDouble);
        }
        return data;
    }

    private String checkSunHeightDuring(String data) {
        if (data.contains(":") && !data.equals("н/д"))
            data = degreeminsecToDoubleStr(data);
        double d = Double.parseDouble(data);
        int newInt = BigDecimal.valueOf(d).setScale(0, RoundingMode.HALF_UP).intValue();
        data = Integer.toString(newInt);
        return data;
    }

    private List<MyFileName> checkFileName(List<MyFileName> fileNameList) {
        List<MyFileName> newfileNameList = new ArrayList<>();
        for (MyFileName i : fileNameList) {
            if (!i.getLabel().isEmpty()) {
                String label = i.getLabel();
                String color = findColor(label);
                if (color != null)
                    i.setLabel(label + " (" + color + ")");
            }
            if (!i.getData().contains(".ige"))
                newfileNameList.add(i);
        }
        for (MyFileName i : fileNameList) {
            if (i.getData().contains(".ige")) {
                String s1 = i.getData().substring(0, i.getData().indexOf('.'));
                int j;
                for (j = 0; j < newfileNameList.size(); j++) {
                    if (newfileNameList.get(j).getData().contains(s1))
                        break;
                }
                newfileNameList.add(j + 1, i);
            }
        }
        return newfileNameList;
    }


    private List<ChannelRange> checkChannelRange(List<ChannelRange> channelRangeList) {
        for (ChannelRange i : channelRangeList) {
            String label = i.getLabel();
            String color = findColor(label);
            if (color != null)
                i.setLabel(label + " (" + color + ")");
            String str = i.getData();
            if (str.contains(".")) {
                String[] strlist = str.split("-");
                for (int j = 0; j < strlist.length; j++) {
                    double num = Double.parseDouble(strlist[j]) * 1000;
                    int num2 = (int) num;
                    strlist[j] = Integer.toString(num2);
                }
                str = StringUtils.join(strlist, '-');
                i.setData(str);
            }
        }
        return channelRangeList;
    }

    private List<AccumulationLine> checkCAccumulationLine(List<AccumulationLine> accumulationLineList) {
        for (AccumulationLine i : accumulationLineList) {
            String label = i.getLabel();
            String color = findColor(label);
            if (color != null)
                i.setLabel(label + " (" + color + ")");
        }
        return accumulationLineList;
    }

    private List<ChannelAngle> checkChannelAngle(List<ChannelAngle> channelAngle) {
        for (ChannelAngle i : channelAngle) {
            String name = i.getName();
            String color = findColor(name);
            if (color != null)
                i.setName(name + " (" + color + ")");
            String aNWLat = checkLat(i.getANWLat());
            String aNWLong = checkLong(i.getANWLong());
            String aNELat = checkLat(i.getANELat());
            String aNELong = checkLong(i.getANELong());
            String aSELat = checkLat(i.getASELat());
            String aSELong = checkLong(i.getASELong());
            String aSWLat = checkLat(i.getASWLat());
            String aSWLong = checkLong(i.getASWLong());
            String aMidLat = checkLat(i.getAMidLat());
            String aMidLong = checkLong(i.getAMidLong());
            i.setANWLat(aNWLat);
            i.setANWLong(aNWLong);
            i.setANELat(aNELat);
            i.setANELong(aNELong);
            i.setASELat(aSELat);
            i.setASELong(aSELong);
            i.setASWLat(aSWLat);
            i.setASWLong(aSWLong);
            i.setAMidLat(aMidLat);
            i.setAMidLong(aMidLong);
        }
        return channelAngle;
    }

    private String[] checkAngle(String data) {
        int secInt = 0;
        int minInt = 0;
        int degInt = 0;
        if (data.contains(":")) {
            String[] strlist = data.split(":");
            double secD = Double.parseDouble(strlist[2]);
            secInt = BigDecimal.valueOf(secD).setScale(0, RoundingMode.HALF_UP).intValue();
            minInt = Integer.parseInt(strlist[1]);
            degInt = Integer.parseInt(strlist[0]);
        } else {
            if (data.contains(".")) {
                degInt = Integer.parseInt(data.substring(0, data.indexOf('.')));
                double number = Double.parseDouble(data);
                minInt = (int) Math.floor((Math.abs(number) % 1) * 60);
                secInt = BigDecimal.valueOf(((Math.abs(number) % 1) * 60 - (double) minInt) * 60).setScale(0, RoundingMode.HALF_UP).intValue();
            }
        }
        if (secInt == 60) {
            secInt = 0;
            minInt = minInt + 1;
        }
        if (minInt == 60) {
            minInt = 0;
            degInt = Math.abs(degInt) + 1;
        }
        return new String[]{Integer.toString(degInt), Integer.toString(minInt), Integer.toString(secInt)};
    }

    private String checkLat(String data) {
        String[] list = checkAngle(data);
        String sec = "00" + list[2];
        sec = sec.substring(sec.length() - 2);
        String min = "00" + list[1];
        min = min.substring(min.length() - 2);
        String sign;
        String degree;
        if (!data.contains("-")) {
            sign = "+";
            degree = "00" + list[0];
        } else {
            sign = "-";
            degree = "00" + list[0].substring(1);
        }
        degree = degree.substring(degree.length() - 2);
        data = sign + degree + "." + min + "." + sec;
        return data;
    }

    private String checkLong(String data) {
        String[] list = checkAngle(data);
        String sec = "00" + list[2];
        sec = sec.substring(sec.length() - 2);
        String min = "00" + list[1];
        min = min.substring(min.length() - 2);
        String sign;
        String degree;
        if (!data.contains("-")) {
            sign = "+";
            degree = "000" + list[0];
        } else {
            sign = "-";
            degree = "000" + list[0].substring(1);
        }
        degree = degree.substring(degree.length() - 3);
        data = sign + degree + "." + min + "." + sec;
        return data;
    }

    private String degreeminsecToDoubleStr(String data) {
        String[] strlist = data.split(":");
        double sec = Double.parseDouble(strlist[2]) / 60;
        double min = (Double.parseDouble(strlist[1]) + sec) / 60;
        return String.valueOf(Double.parseDouble(strlist[0]) + min);
    }

    private String findColor(String label) {
        if (!label.isEmpty()) {
            String[] strlist = label.split(" ");
            return channelService.findChannelColor(vehicleType, Integer.valueOf(strlist[1]));
        }
        return null;
    }

    private String doubleHalfUp(String data) {
        if (!data.isEmpty() && !data.equals("н/д"))
            data = BigDecimal.valueOf(Double.parseDouble(data)).setScale(1, RoundingMode.HALF_UP).toString();
        return data;
    }
}
