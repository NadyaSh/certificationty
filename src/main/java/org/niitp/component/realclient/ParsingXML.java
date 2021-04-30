package org.niitp.component.realclient;

import lombok.extern.slf4j.Slf4j;
import org.niitp.exception.AppException;
import org.niitp.model.*;
import org.niitp.service.XmlServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ParsingXML {

    private ParseResursXml parseResursXml;
    private ParseKanopusXml parseKanopusXml;
    private final XmlServiceImpl xmlServiceImpl;

    @Autowired
    public ParsingXML(ParseResursXml parseResursXml,
                      ParseKanopusXml parseKanopusXml,
                      XmlServiceImpl xmlServiceImpl) {
        this.parseResursXml = parseResursXml;
        this.parseKanopusXml = parseKanopusXml;
        this.xmlServiceImpl = xmlServiceImpl;
    }

    public ProductData fromXML(ProductData fdata) {
        List<MetaData> metaData = fdata.getMetaData();
        String xmlName = metaData.get(0).getData();
        try {
            DocumentBuilder d = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Transformer transformerToXml = TransformerFactory.newInstance().newTransformer();
            if (d == null) {
                fdata.setErrormess(xmlName + " не удалось распарсить");
                log.error("Оператор: " + fdata.getOperator() + " " + xmlName + " не удалось распарсить");
                throw new AppException(xmlName + " не удалось распарсить");
            }
            StringWriter writer = new StringWriter();
            Document doc = d.parse(new ByteArrayInputStream(metaData.get(0).getContent().getBytes()));
            transformerToXml.transform(new DOMSource(doc), new StreamResult(writer));
            String xml = writer.toString();
            Element el = doc.getDocumentElement();
            switch (el.getNodeName()) {
                case "SPP_ROOT":
                    List<ChannelAngle> channelAngleList = new ArrayList<>();
                    List<ChannelRange> channelRangeList = new ArrayList<>();
                    List<AccumulationLine> accumulationList = new ArrayList<>();
                    parseResursXml.setElement(el, fdata, xmlName, channelAngleList, channelRangeList, accumulationList);
                    for (MetaData metaDatum : metaData) {
                        if (!metaDatum.getData().equals(xmlName)) {
                            doc = d.parse(new ByteArrayInputStream(metaDatum.getContent().getBytes()));
                            el = doc.getDocumentElement();
                            parseResursXml.setAdditionalElement(el, fdata, metaDatum.getData());
                        }
                    }
                    fdata.setChannelRange(channelRangeList);
                    fdata.setChannelAngle(channelAngleList);
                    fdata.setAccumulationLines(accumulationList);
                    break;
                case "PASP_ROOT":
                    parseKanopusXml.setElement(el, fdata, xmlName);
                    if (fdata.getQuickLook().isEmpty()) {
                        List<MetaData> quickLookList = new ArrayList<>();
                        quickLookList.add(new MetaData("", ""));
                        fdata.setQuickLook(quickLookList);
                    }
                    break;
                default:
                    fdata.setErrormess(xmlName + " неправильный формат xml");
                    log.error("Оператор: " + fdata.getOperator() + " " + xmlName + " неправильный формат xml");
                    throw new AppException(xmlName + " неправильный формат xml");
            }
            if (fdata.getShapeFiles().isEmpty()) {
                List<MetaData> shapeFiles = new ArrayList<>();
                shapeFiles.add(new MetaData("", ""));
                fdata.setShapeFiles(shapeFiles);
            }
            if (fdata.getGeo().isEmpty()) {
                List<MetaData> geoList = new ArrayList<>();
                geoList.add(new MetaData("", ""));
                fdata.setGeo(geoList);
            }

            Long id = xmlServiceImpl.loadXml(xmlName, fdata.getTypeV(), xml).getId();
            fdata.setXmlid(id);
            fdata.setErrormess("Данные получены");
        } catch (SAXException | ParserConfigurationException | IOException e) {
            fdata.setErrormess(xmlName + " не удалось распарсить");
            log.error("Оператор: " + fdata.getOperator() + " " + xmlName + " не удалось распарсить" + e);
            throw new AppException(xmlName + " не удалось распарсить" + e);
        } catch (TransformerException e) {
            fdata.setErrormess(xmlName + " не удалось преобразовать xml для записи в БД");
            log.error("Оператор: " + fdata.getOperator() + " " + xmlName + " не удалось преобразовать xml для записи в БД" + e);
            throw new AppException(xmlName + " не удалось преобразовать xml для записи в БД" + e);
        }
        return fdata;
    }
}



