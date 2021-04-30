package org.niitp.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.niitp.exception.*;
import org.niitp.model.*;
import org.niitp.mongo.CertificateDocumentRepository;
import org.niitp.mongo.ParamsDocumentRepository;
import org.niitp.service.ChooserService;
import org.niitp.service.GeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/passport")
@Api(value = "parser", tags = "Парсер xml Ресурса-П и Канопуса-В")
public class ApoiParsingController {

    @Autowired
    private ChooserService chooserService;
    @Autowired
    private GeneratorService generatorService;
    @Autowired
    private ParamsDocumentRepository paramsDocumentRepository;
    @Autowired
    private CertificateDocumentRepository certificateDocumentRepository;

    static final String typeVRp = "Ресурс-П";
    static final String typeVKv = "Канопус-В";

    public static final List<String> listImg =
            Collections.unmodifiableList(Arrays.asList(".ige", ".img", ".tiff", ".tif", ".IMG", ".TIFF", ".TIF"));

    public static final List<String> listShp =
            Collections.unmodifiableList(Arrays.asList(".dbf", ".prj", ".shp", ".shx"));

    @PostMapping(value = "/parse/apoi", headers = "Accept=application/json")
    public ResponseEntity<?> parse(@RequestBody Input data) {

        Result result = new Result();
        try {
            data.getDirectories().forEach(fatList -> {
                List<CertificateData> certificateDataList = new ArrayList<>();

                for (ThinList thinList : fatList.getFiles()) {
                    CertificateData certificateData = new CertificateData(data);
                    if (thinList.getExtension().equals(".xml")) {

                        String xmlName = thinList.getName();
                        xmlName = xmlName.replace(".xml", "");
                        if (xmlName.contains("KV")) {
                            certificateData.getMetaData().add(new MetaData(thinList.getName(), thinList.getSize(), thinList.getContent()));
                            certificateData.setPdfName(certificateData.getApplicationNumber() + "_" + certificateData.getTaskNumber() + "_" + xmlName + ".pdf");
                            for (ThinList thinList2 : fatList.getFiles()) {
                                if (listImg.contains(thinList2.getExtension()) && thinList2.getName().contains(xmlName)) {
                                    certificateData.getFileName().add(new MyFileName(thinList2.getName(), thinList2.getSize()));
                                } else if (listShp.contains(thinList2.getExtension()) && thinList2.getName().contains(xmlName)) {
                                    certificateData.getShapeFiles().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                } else if (thinList2.getExtension().equals(".twf") && thinList2.getName().contains(xmlName)) {
                                    certificateData.getGeo().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                } else if (thinList2.getExtension().equals(".jpg") && thinList2.getName().contains(xmlName)) {
                                    certificateData.getQuickLook().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                }
                            }
                        } else {
                            certificateData.getMetaData().add(new MetaData(thinList.getName(), thinList.getSize(), thinList.getContent()));

                            String searchShpName = xmlName.substring(0, xmlName.length() - 4);
                            certificateData.setPdfName(certificateData.getApplicationNumber() + "_" + certificateData.getTaskNumber() + "_" + xmlName + ".pdf");

                            for (ThinList thinList2 : fatList.getFiles()) {
                                if (listImg.contains(thinList2.getExtension()) && thinList2.getName().contains(xmlName)) {
                                    certificateData.getFileName().add(new MyFileName(thinList2.getName(), thinList2.getSize()));
                                } else if (thinList2.getExtension().equals(".jpg") && thinList2.getName().contains(xmlName)) {
                                    certificateData.getQuickLook().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                } else if (listShp.contains(thinList2.getExtension()) && thinList2.getName().contains(searchShpName)) {
                                    certificateData.getShapeFiles().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                } else if (thinList2.getExtension().equals(".twf") && thinList2.getName().contains(searchShpName)) {
                                    certificateData.getGeo().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                }
                            }
                        }
                        certificateDataList.add(certificateData);
                    }
                }

                certificateDataList.forEach(certificateData -> {
                    certificateData = chooserService.chooseParser(certificateData);

                    if (certificateData.getTypeV().equals(typeVRp)) {
                        Optional<ParamsDocument> paramsDocument = paramsDocumentRepository.findByTypeV(typeVRp);
                        if (!paramsDocument.isPresent()) {
                            log.error("Нет постоянных параметров Ресурса");
                            result.getErrors().add("Нет постоянных параметров Ресурса");
                            throw new AppParamsException("Нет постоянных параметров Ресурса");
                        }
                        int sunHeightRpInteger = Integer.parseInt(paramsDocument.get().getSunHeight().replace("не менее ", ""));

                        if (checkSunHeightDuring(certificateData.getSunHeightDuring()) < sunHeightRpInteger ||
                                checkAngleOptSys(certificateData.getAngleOptSys()) > Double.parseDouble(paramsDocument.get().getMaxAngle())) {
                            log.error(certificateData.getPdfName() + " не прошел по ТУ");
                            result.getErrors().add(certificateData.getPdfName() + " не прошел по ТУ");
                            return;
                        }
                    } else if (certificateData.getTypeV().equals(typeVKv)) {

                        Optional<ParamsDocument> paramsDocument = paramsDocumentRepository.findByTypeV(typeVKv);
                        if (!paramsDocument.isPresent()) {
                            log.error("Нет постоянных параметров Канопуса");
                            result.getErrors().add("Нет постоянных параметров Канопуса");
                            throw new AppParamsException("Нет постоянных параметров Канопуса");
                        }
                        int sunHeightKvInteger = Integer.parseInt(paramsDocument.get().getSunHeight().replace("не менее ", ""));
                        if (checkSunHeightDuring(certificateData.getSunHeightDuring()) < sunHeightKvInteger ||
                                checkKAODegree(certificateData.getAlpha()) > Double.parseDouble(paramsDocument.get().getMaxAngle()) ||
                                checkKAODegree(certificateData.getOmega()) > Double.parseDouble(paramsDocument.get().getMaxAngle()) ||
                                checkKAODegree(certificateData.getKappa()) > Double.parseDouble(paramsDocument.get().getMaxAngle())) {
                            log.error(certificateData.getPdfName() + " не прошел по ТУ");
                            result.getErrors().add(certificateData.getPdfName() + " не прошел по ТУ");
                            return;
                        }
                    } else {
                        log.error(certificateData.getTaskNumber() + " не Ресурс и не Канопус ");
                        result.getErrors().add(certificateData.getTaskNumber() + " не Ресурс и не Канопус ");
                        return;
                    }

                    ResultLink link = createPdf(certificateData);

                    result.getLinks().add(link);
                });
            });
            return ResponseEntity.ok().body(result);
        } catch (AppParamsException e) {
            return ResponseEntity.badRequest().body(result);
        } catch (VehicleBadRequestException e) {
            log.error(e.getLocalizedMessage());
            result.getErrors().add("В базе нет такого аппарата");
            return ResponseEntity.badRequest().body(result);
        } catch (BadRequestException e) {
            log.error(e.getLocalizedMessage());
            result.getErrors().add("Не удалось распарсить xml");
            return ResponseEntity.badRequest().body(result);
        } catch (ResourceNotFoundException e) {
            log.error(e.getLocalizedMessage());
            result.getErrors().add("Не нашлось что-то");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } catch (AppException e) {
            log.error(e.getLocalizedMessage());
            result.getErrors().add("Нет изображения сертификата");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            result.getErrors().add("Все плохо");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }

    }

    private ResultLink createPdf(CertificateData certificateData) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        byte[] pdfContents = generatorService.createPdf("passport", certificateData);

        CertificateDocument certificate = certificateDocumentRepository.insert(new CertificateDocument(certificateData.getPdfName(), new Binary(pdfContents)));
        return new ResultLink(certificate.getTitle(), "/certification/" + certificate.getId());
    }

    private String getNameForSearch(String xmlName) {
        xmlName = xmlName.replace("_G", "");
        xmlName = xmlName.replace("_S", "");
        xmlName = xmlName.replace("fr_", "");
        return xmlName;
    }

    private String getTempPdfName(String xmlName) {
        String temp = xmlName;
        temp = temp.replace("_G", "");
        temp = temp.replace("_S", "");
        temp = temp.replace("fr_", "");
        if (temp.split("_").length > 6)
            xmlName = xmlName.substring(0, xmlName.length() - 3);
        return xmlName;
    }

    private Double checkAngleOptSys(String data) {
        if (!data.isEmpty()) {
            if (data.contains(":"))
                data = degreeMinSecToDoubleStr(data);
            double newDouble = BigDecimal.valueOf(Double.parseDouble(data)).setScale(1, RoundingMode.HALF_UP).doubleValue();
            log.info("AngleOptSys {} -> {}", data, newDouble);
            return newDouble;
        }
        log.info("угол {} -> пусто", data);
        return 100.0;
    }

    private Double checkKAODegree(String data) {
        if (!data.isEmpty()) {
            double newDouble = BigDecimal.valueOf(Double.parseDouble(data)).setScale(1, RoundingMode.HALF_UP).doubleValue();
            log.info("угол {} -> {}", data, newDouble);
            return newDouble;
        }
        log.info("угол {} -> пусто", data);
        return 100.0;
    }

    private Integer checkSunHeightDuring(String data) {
        if (data.contains(":"))
            data = degreeMinSecToDoubleStr(data);
        return BigDecimal.valueOf(Double.parseDouble(data)).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private String degreeMinSecToDoubleStr(String data) {
        String[] strList = data.split(":");
        double sec = Double.parseDouble(strList[2]) / 60;
        double min = (Double.parseDouble(strList[1]) + sec) / 60;
        return String.valueOf(Double.parseDouble(strList[0]) + min);
    }
}