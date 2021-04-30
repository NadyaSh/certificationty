package org.niitp.service;

import lombok.extern.slf4j.Slf4j;
import org.niitp.component.subclients.ParseKanopus;
import org.niitp.component.subclients.ParseResurs;
import org.niitp.exception.BadRequestException;
import org.niitp.model.CertificateData;
import org.niitp.model.ChannelAngle;
import org.niitp.model.ChannelRange;
import org.niitp.model.MetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ChooserService {

    @Autowired
    private ParseResurs parseResurs;
    @Autowired
    private ParseKanopus parseKanopus;

    public CertificateData chooseParser(CertificateData input) {
        List<MetaData> metaData = input.getMetaData();
        String xmlName = metaData.get(0).getData();
        try {
            DocumentBuilder d = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            if (d == null) {
                log.error(xmlName + " не удалось распарсить");
                throw new BadRequestException("CertificateData", "chooseParser", xmlName + " не удалось распарсить");
            }

            Document doc = d.parse(new ByteArrayInputStream(metaData.get(0).getContent().getBytes()));
            Element el = doc.getDocumentElement();
            switch (el.getNodeName()) {
                case "SPP_ROOT":
                    List<ChannelAngle> channelAngleList = new ArrayList<>();
                    List<ChannelRange> channelRangeList = new ArrayList<>();
                    parseResurs.setElement(el, input, xmlName, channelAngleList, channelRangeList);
                    for (MetaData metaDatum : metaData) {
                        if (!metaDatum.getData().equals(xmlName)) {
                            doc = d.parse(new ByteArrayInputStream(metaDatum.getContent().getBytes()));
                            el = doc.getDocumentElement();
                            parseResurs.setAdditionalElement(el, input, metaDatum.getData());
                        }
                    }
                    input.setChannelRange(channelRangeList);
                    input.setChannelAngle(channelAngleList);
                    break;
                case "PASP_ROOT":

                    parseKanopus.setElement(el, input, xmlName);
                    if (input.getQuickLook().isEmpty()) {
                        List<MetaData> quickLookList = new ArrayList<>();
                        quickLookList.add(new MetaData("", ""));
                        input.setQuickLook(quickLookList);
                    }
                    break;
                default:

                    log.error(xmlName + " неправильный формат xml");
                    throw new BadRequestException("CertificateData", "chooseParser", xmlName + " неправильный формат xml");
            }
            if (input.getShapeFiles().isEmpty()) {
                List<MetaData> shapeFiles = new ArrayList<>();
                shapeFiles.add(new MetaData("", ""));
                input.setShapeFiles(shapeFiles);
            }
            if (input.getGeo().isEmpty()) {
                List<MetaData> geoList = new ArrayList<>();
                geoList.add(new MetaData("", ""));
                input.setGeo(geoList);
            }

        } catch (Exception e) {
            log.error("Не удалось разобраться с xml", e);
            throw new BadRequestException("CertificateData", "chooseParser", "Не удалось разобраться с xml" + e);
        }
        return input;
    }
}



