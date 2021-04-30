package org.niitp.service;

import lombok.extern.slf4j.Slf4j;
import org.niitp.model.rsp.RspOEP;
import org.niitp.model.rsp.RspParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Slf4j
@Service
public class ParseRspService {
    @Value("${service.url.archive.path}")
    private String rspPath;

    public RspParams parseRsp(String partPath) {
        RspParams rspParams = new RspParams();
        File rspFile = new File(rspPath + File.separator + partPath);
        if (!rspFile.exists()) {
            log.error(rspFile.getAbsolutePath() + " не существует");
            return rspParams;
        }

        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder d = builderFactory.newDocumentBuilder();
            if (d == null) {
                log.error("DocumentBuilder не создался");
                return rspParams;
            }
            Document doc = d.parse(rspFile);
            NodeList nlRspRootP = doc.getElementsByTagName("rsp_root");
            NodeList nlRspRoot = nlRspRootP.item(0).getChildNodes();
            for (int i = 0; i < nlRspRoot.getLength(); i++) {
                if ("aViewAngle".equalsIgnoreCase(nlRspRoot.item(i).getNodeName())) {
                    String a = getNodeAttributes("val", nlRspRoot.item(i).getAttributes()).getNodeValue();
                    String[] strList = a.split(":");
                    double sec = Double.parseDouble(strList[2]) / 60;
                    double min = (Double.parseDouble(strList[1]) + sec) / 60;
                    rspParams.setAlpha(Double.toString(Double.parseDouble(strList[0]) + min));
                }
                if ("idggp_data".equalsIgnoreCase(nlRspRoot.item(i).getNodeName())) {
                    NodeList nlIdggpData = nlRspRoot.item(i).getChildNodes();
                    for (int ii = 0; ii < nlIdggpData.getLength(); ii++) {
                        if ("proc_data".equalsIgnoreCase(nlIdggpData.item(ii).getNodeName())) {
                            NodeList nlProcData = nlIdggpData.item(ii).getChildNodes();
                            for (int iii = 0; iii < nlProcData.getLength(); iii++) {
                                if ("interior_orientation".equalsIgnoreCase(nlProcData.item(iii).getNodeName())) {
                                    NodeList nlInteriorOrientation = nlProcData.item(iii).getChildNodes();
                                    for (int iiii = 0; iiii < nlInteriorOrientation.getLength(); iiii++) {
                                        if ("focal_length".equalsIgnoreCase(nlInteriorOrientation.item(iiii).getNodeName())) {
                                            double d1 = Double.parseDouble(getNodeAttributes("val", nlInteriorOrientation.item(iiii).getAttributes()).getNodeValue());
                                            int newDouble = BigDecimal.valueOf(d1).setScale(0, RoundingMode.HALF_UP).intValue();
                                            rspParams.setFocalL(Integer.toString(newDouble));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if ("VOEP_1_Data".equalsIgnoreCase(nlRspRoot.item(i).getNodeName())) {
                    getOEPParams(rspParams, nlRspRoot.item(i));
                }
                if ("VOEP_2_Data".equalsIgnoreCase(nlRspRoot.item(i).getNodeName())) {
                    getOEPParams(rspParams, nlRspRoot.item(i));
                }
                if ("VOEP_3_Data".equalsIgnoreCase(nlRspRoot.item(i).getNodeName())) {
                    getOEPParams(rspParams, nlRspRoot.item(i));
                }
                if ("VOEP_4_Data".equalsIgnoreCase(nlRspRoot.item(i).getNodeName())) {
                    getOEPParams(rspParams, nlRspRoot.item(i));
                }
                if ("VOEP_5_Data".equalsIgnoreCase(nlRspRoot.item(i).getNodeName())) {
                    getOEPParams(rspParams, nlRspRoot.item(i));
                }
                if ("VOEP_6_Data".equalsIgnoreCase(nlRspRoot.item(i).getNodeName())) {
                    getOEPParams(rspParams, nlRspRoot.item(i));
                }
                if ("VOEP_7_Data".equalsIgnoreCase(nlRspRoot.item(i).getNodeName())) {
                    getOEPParams(rspParams, nlRspRoot.item(i));
                }
            }


        } catch (SAXException | ParserConfigurationException | IOException e) {
            log.error(rspFile.getName() + " не удалось распарсить" + e);
            return rspParams;
        }
        return rspParams;
    }

    private void getOEPParams(RspParams rspParams, Node node) {
        RspOEP rspOEP = new RspOEP();
        for (int ii = 0; ii < node.getChildNodes().getLength(); ii++) {
            if ("nTypeCODer".equalsIgnoreCase(Objects.requireNonNull(node.getChildNodes().item(ii).getNodeName()))) {
                rspOEP.setTypeCODer(getNodeAttributes("val", node.getChildNodes().item(ii).getAttributes()).getNodeValue());
            }
            if ("nVOEPNumber".equalsIgnoreCase(Objects.requireNonNull(node.getChildNodes().item(ii).getNodeName()))) {
                switch (getNodeAttributes("val", node.getChildNodes().item(ii).getAttributes()).getNodeValue()) {
                    case "1":
                        rspOEP.setNumber("22");
                        break;
                    case "2":
                        rspOEP.setNumber("23");
                        break;
                    case "3":
                        rspOEP.setNumber("21");
                        break;
                    case "4":
                        rspOEP.setNumber("10");
                        break;
                    case "6":
                        rspOEP.setNumber("33");
                        break;
                }
            }
            if ("nAcLineQuant".equalsIgnoreCase(Objects.requireNonNull(node.getChildNodes().item(ii).getNodeName()))) {
                rspOEP.setLineQuant(getNodeAttributes("val", node.getChildNodes().item(ii).getAttributes()).getNodeValue());
            }
        }
        rspParams.getRspOEPs().add(rspOEP);
    }

    private static Node getNodeAttributes(String tagName, NamedNodeMap namedNodeMap) {
        for (int i = 0; i < namedNodeMap.getLength(); i++) {
            if (namedNodeMap.item(i).getNodeName().equalsIgnoreCase(tagName)) {
                return namedNodeMap.item(i);
            }
        }
        return null;
    }

}
