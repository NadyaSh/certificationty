package org.niitp.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.niitp.component.pdf.PdfGenerator;
import org.niitp.component.realclient.ParsingXML;
import org.niitp.exception.*;
import org.niitp.model.*;
import org.niitp.model.rsp.RspParams;
import org.niitp.mongo.CertificateDocumentRepository;
import org.niitp.mongo.ParamsDocumentRepository;
import org.niitp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/passport")
@Api(value = "parser", tags = "Парсер xml Ресурса-П и Канопуса-В")
public class ParsingController {

    @Autowired
    private ParsingXML parser;
    @Autowired
    private ChooserService chooserService;
    @Autowired
    private XmlService xmlService;
    @Autowired
    private JsonService jsonService;
    @Autowired
    private PdfGenerator pdfGenerator;
    @Autowired
    private GeneratorService generatorService;
    @Autowired
    private ParamsDocumentRepository paramsDocumentRepository;
    @Autowired
    private CertificateDocumentRepository certificateDocumentRepository;
    @Autowired
    private RestClientCertificationAsutsp clientCertificationAsutsp;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private ArchiveService archiveService;
    @Autowired
    private ParseRspService parseRspService;

    static final String TYPE_V_RP = "Ресурс-П";
    static final String TYPE_V_KV = "Канопус-В";

    public static final List<String> LIST_IMG =
            Collections.unmodifiableList(Arrays.asList(".ige", ".img", ".tiff", ".tif", ".IMG", ".TIFF", ".TIF"));

    public static final List<String> LIST_SHP =
            Collections.unmodifiableList(Arrays.asList(".dbf", ".prj", ".shp", ".shx"));

    @PostMapping(value = "/parse", headers = "Accept=application/json")
    public ResponseEntity<?> parse(@RequestBody Input data) {

        Result result = new Result();
        try {
            data.getDirectories().forEach(fatList -> {
                List<CertificateData> certificateDataList = new ArrayList<>();

                long xmlListSize = fatList.getFiles().stream().filter(thin -> thin.getExtension().equals(".xml")).count();
                if (xmlListSize == 1) {
                    CertificateData certificateData = new CertificateData(data);
                    fatList.getFiles().forEach(thinList -> {
                        if (thinList.getExtension().equals(".xml")) {
                            String xmlName = thinList.getName();
                            xmlName = xmlName.replace(".xml", "");
                            if (!xmlName.contains("KV"))
                                xmlName = getTempPdfName(xmlName);
                            certificateData.setPdfName(certificateData.getApplicationNumber() + "_" + certificateData.getTaskNumber() + "_" + xmlName + ".pdf");

                            certificateData.getMetaData().add(new MetaData(thinList.getName(), thinList.getSize(), thinList.getContent()));
                        } else if (LIST_IMG.contains(thinList.getExtension())) {
                            certificateData.getFileName().add(new MyFileName(thinList.getName(), thinList.getSize()));
                        } else if (LIST_SHP.contains(thinList.getExtension())) {
                            certificateData.getShapeFiles().add(new MetaData(thinList.getName(), thinList.getSize()));
                        } else if (thinList.getExtension().equals(".twf")) {
                            certificateData.getGeo().add(new MetaData(thinList.getName(), thinList.getSize()));
                        } else if (thinList.getExtension().equals(".jpg")) {
                            certificateData.getQuickLook().add(new MetaData(thinList.getName(), thinList.getSize()));
                        }
                    });
                    certificateDataList.add(certificateData);

                } else if (xmlListSize > 1) {
                    List<String> addedXml = new ArrayList<>();

                    for (ThinList thinList : fatList.getFiles()) {
                        CertificateData certificateData = new CertificateData(data);
                        if (thinList.getExtension().equals(".xml")) {

                            boolean groupImg = false;

                            String xmlName = thinList.getName();
                            xmlName = xmlName.replace(".xml", "");
                            if (xmlName.contains("KV")) {
                                certificateData.getMetaData().add(new MetaData(thinList.getName(), thinList.getSize(), thinList.getContent()));
                                certificateData.setPdfName(certificateData.getApplicationNumber() + "_" + certificateData.getTaskNumber() + "_" + xmlName + ".pdf");
                                for (ThinList thinList2 : fatList.getFiles()) {
                                    if (LIST_IMG.contains(thinList2.getExtension()) && thinList2.getName().contains(xmlName)) {
                                        certificateData.getFileName().add(new MyFileName(thinList2.getName(), thinList2.getSize()));
                                    } else if (LIST_SHP.contains(thinList2.getExtension()) && thinList2.getName().contains(xmlName)) {
                                        certificateData.getShapeFiles().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                    } else if (thinList2.getExtension().equals(".twf") && thinList2.getName().contains(xmlName)) {
                                        certificateData.getGeo().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                    } else if (thinList2.getExtension().equals(".jpg") && thinList2.getName().contains(xmlName)) {
                                        certificateData.getQuickLook().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                    }
                                }
                            } else {
                                if (addedXml.contains(thinList.getName()))
                                    continue;

                                addedXml.add(thinList.getName());
                                certificateData.getMetaData().add(new MetaData(thinList.getName(), thinList.getSize(), thinList.getContent()));

                                String searchShpName;
                                String[] strings = getNameForSearch(xmlName).split("_");

                                boolean channelsFlag = false;
                                String endsWith = "";

                                if (strings.length <= 8) {
                                    String last = strings[strings.length - 1];
                                    if (last.length() == 2) {
                                        channelsFlag = true;
                                        if (strings.length < 8)
                                            groupImg = true;
                                        xmlName = xmlName.substring(0, xmlName.length() - 3);
                                        searchShpName = xmlName.substring(0, xmlName.length() - 3);
                                        certificateData.setPdfName(certificateData.getApplicationNumber() + "_" + certificateData.getTaskNumber() + "_" + xmlName + ".pdf");
                                    } else if (last.length() == 3) {
                                        searchShpName = xmlName.substring(0, xmlName.length() - 4);
                                        certificateData.setPdfName(certificateData.getApplicationNumber() + "_" + certificateData.getTaskNumber() + "_" + xmlName + ".pdf");
                                    } else {
                                        log.error("Неизвестная штука перед __.xml");
                                        result.getErrors().add("Неизвестная штука перед __.xml");
                                        throw new BadRequestException("Неизвестная штука перед __.xml");
                                    }
                                } else if (strings.length == 9) {
                                    String last = strings[strings.length - 1];
                                    if (last.length() == 3) {
                                        searchShpName = xmlName.substring(0, xmlName.length() - 4);
                                        if (!xmlName.contains("_10_00_")) {
                                            channelsFlag = true;
                                            xmlName = xmlName.substring(0, xmlName.length() - 10);
                                            certificateData.setPdfName(certificateData.getApplicationNumber() + "_" + certificateData.getTaskNumber() + "_" + xmlName + "_" + strings[strings.length - 2] + "_" + last + ".pdf");
                                            if (xmlListSize > 4) {
                                                endsWith = strings[strings.length - 2] + "_" + last;
                                            }
                                        } else {
                                            certificateData.setPdfName(certificateData.getApplicationNumber() + "_" + certificateData.getTaskNumber() + "_" + xmlName + ".pdf");
                                        }
                                    } else {
                                        log.error("Неизвестная штука перед __.xml");
                                        result.getErrors().add("Неизвестная штука перед __.xml");
                                        throw new BadRequestException("Неизвестная штука перед __.xml");
                                    }
                                } else {
                                    log.error("Неизвестный формат названия xml");
                                    result.getErrors().add("Неизвестный формат названия xml");
                                    throw new BadRequestException("Неизвестный формат названия xml");
                                }
                                for (ThinList thinList2 : fatList.getFiles()) {
                                    if (endsWith.isEmpty()) {
                                        if (channelsFlag && !addedXml.contains(thinList2.getName()) && thinList2.getExtension().equals(".xml") && thinList2.getName().contains(xmlName)) {
                                            addedXml.add(thinList2.getName());
                                            certificateData.getMetaData().add(new MetaData(thinList2.getName(), thinList2.getSize(), thinList2.getContent()));
                                        }
                                        if (LIST_IMG.contains(thinList2.getExtension()) && thinList2.getName().contains(xmlName)) {
                                            certificateData.getFileName().add(new MyFileName(thinList2.getName(), thinList2.getSize()));
                                        } else if (thinList2.getExtension().equals(".jpg") && thinList2.getName().contains(xmlName)) {
                                            certificateData.getQuickLook().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                        }
                                    } else {
                                        if (channelsFlag && !addedXml.contains(thinList2.getName()) && thinList2.getExtension().equals(".xml") && thinList2.getName().contains(xmlName) && thinList2.getName().endsWith(endsWith + ".xml")) {
                                            addedXml.add(thinList2.getName());
                                            certificateData.getMetaData().add(new MetaData(thinList2.getName(), thinList2.getSize(), thinList2.getContent()));
                                        }
                                        if (LIST_IMG.contains(thinList2.getExtension()) && thinList2.getName().contains(xmlName) && thinList2.getName().contains("_" + endsWith + ".")) {
                                            certificateData.getFileName().add(new MyFileName(thinList2.getName(), thinList2.getSize()));
                                        } else if (thinList2.getExtension().equals(".jpg") && thinList2.getName().contains(xmlName) && thinList2.getName().contains("_" + endsWith + "_")) {
                                            certificateData.getQuickLook().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                        }
                                    }

                                    if (LIST_SHP.contains(thinList2.getExtension()) && thinList2.getName().contains(searchShpName)) {
                                        certificateData.getShapeFiles().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                    } else if (thinList2.getExtension().equals(".twf") && thinList2.getName().contains(searchShpName)) {
                                        certificateData.getGeo().add(new MetaData(thinList2.getName(), thinList2.getSize()));
                                    }
                                }
                            }

                            if (groupImg) {
                                certificateData.getFileName().sort((o1, o2) -> {
                                    String n1 = o1.getData().substring(0, o1.getData().indexOf('.'));
                                    String[] stringsN1 = n1.split("_");
                                    String n2 = o2.getData().substring(0, o2.getData().indexOf('.'));
                                    String[] stringsN2 = n2.split("_");
                                    if (stringsN1[stringsN1.length - 1].equals(stringsN2[stringsN2.length - 1])) {
                                        if (Integer.parseInt(stringsN1[stringsN1.length - 2]) > Integer.parseInt(stringsN2[stringsN2.length - 2])) {
                                            return 1;
                                        } else {
                                            return -1;
                                        }
                                    }
                                    if (!stringsN1[stringsN1.length - 1].equals(stringsN2[stringsN2.length - 1])) {
                                        if (Integer.parseInt(stringsN1[stringsN1.length - 1]) > Integer.parseInt(stringsN2[stringsN2.length - 1])) {
                                            return 1;
                                        } else {
                                            return -1;
                                        }
                                    }
                                    return 0;
                                });
                            }
                            certificateDataList.add(certificateData);
                        }
                    }
                } else {
                    log.error("Нет xml в исходных данных");
                    result.getErrors().add("Нет xml в исходных данных");
                    throw new AppParamsException("Нет xml в исходных данных");
                }

                certificateDataList.forEach(certificateData -> {
                    certificateData = chooserService.chooseParser(certificateData);

                    if (certificateData.getTypeV().equals(TYPE_V_RP)) {
                        Optional<ParamsDocument> paramsDocument = paramsDocumentRepository.findByTypeV(TYPE_V_RP);
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
                    } else if (certificateData.getTypeV().equals(TYPE_V_KV)) {

                        Optional<ParamsDocument> paramsDocument = paramsDocumentRepository.findByTypeV(TYPE_V_KV);
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

                    certificateData = getCertificateDataContent(certificateData);
                    if(certificateData == null)
                        result.getErrors().add("Вид сжатия не соответствует ДИКМ!");

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

    @GetMapping(value = "/parserkvp/test")
    public ResponseEntity<?> testForKvp() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //for real certification client
    @ApiOperation(value = "Возвращает json паспорта обработанного изображения", response = ProductData.class)
    @PostMapping(value = "/parser", headers = "Accept=application/json")
    public @ResponseBody
    ProductData getPassport(@RequestBody ProductData formdata) {
        return parser.fromXML(formdata);
    }

    //for real certification client
    @PreAuthorize("hasAnyRole('READWRITE', 'SUPERMEN')")
    @ApiOperation(value = "Сохраняет json и возвращает его id из базы")
    @PostMapping(value = "/save", consumes = {"application/json"})
    public @ResponseBody
    Long saveRsResult(@RequestBody ProductData formdata) {
        for (int i = 0; i < formdata.getMetaData().size(); i++)
            formdata.getMetaData().get(i).setContent("");
        String xmlname = formdata.getMetaData().get(0).getData();
        xmlname = xmlname.replace(".xml", "");
        xmlname = getString(formdata, xmlname);
        XmlEntity xe = xmlService.findById(formdata.getXmlid());
        JsonEntity je = jsonService.saveJson(xmlname, formdata, xe.getDatetime());
        return je.getId();
    }

    //for real certification client
    @PreAuthorize("hasAnyRole('READWRITE', 'SUPERMEN')")
    @ApiOperation(value = "Возвращает pdf паспорта обработанного изображения по json id")
    @GetMapping(value = "/report/{id}")
    public ResponseEntity<byte[]> getPdf(@PathVariable Long id) throws UnsupportedEncodingException {
        JsonEntity je = jsonService.findJson(id);
        String xmlName = je.getFilename();
        ProductData object = je.getJson();
        String task = object.getRegion().substring(0, 7);
        return getPdfContent(object, xmlName, task);
    }

    //for real certification client
    @ApiOperation(value = "Возвращает значения из таблицы базы с json для отчета", response = DataSource.class)
    @PostMapping(value = "/table")
    public @ResponseBody
    DataSource getTableData() {
        DataSource dataSource = new DataSource();
        List<JsonEntity> jsonlist = jsonService.findAll();
        for (JsonEntity i : jsonlist) {
            DataSourceItem item = new DataSourceItem();
            item.setId(i.getId());
            item.setApplication(i.getJson().getApplicationNumber());
            item.setTask(i.getJson().getRegion().substring(0, 6));
            item.setFileName(i.getFilename());
            item.setVehicle(i.getJson().getTypeV());
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
            Timestamp ts = i.getDatetimein();
            if (ts != null) {
                LocalDateTime localDateTimeIn = LocalDateTime.ofInstant(ts.toInstant(), ZoneOffset.of("+3"));
                item.setDateIn(localDateTimeIn.format(dateFormat));
            }
            Timestamp tsC = i.getDatetime();
            if (tsC != null) {
                LocalDateTime localDateTimeC = LocalDateTime.ofInstant(tsC.toInstant(), ZoneOffset.of("+3"));
                item.setDateCertification(localDateTimeC.format(dateFormat));
            }
            dataSource.add(item);
        }
        return dataSource;
    }

    //for real certification client
    @ApiOperation(value = "Возвращает значения из таблицы базы c xml для отчета", response = DataSource.class)
    @PostMapping(value = "/tablexml")
    public @ResponseBody
    DataSource getTableXmlData() {
        DataSource dataSource = new DataSource();
        List<XmlEntity> xmllist = xmlService.findAll();
        for (XmlEntity i : xmllist) {
            DataSourceItem item = new DataSourceItem();
            item.setId(i.getId());
            item.setFileName(i.getFilename());
            item.setVehicle(i.getVehicle());
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
            Timestamp ts = i.getDatetime();
            if (ts != null) {
                LocalDateTime localDateTimeIn = LocalDateTime.ofInstant(ts.toInstant(), ZoneOffset.of("+3"));
                item.setDateIn(localDateTimeIn.format(dateFormat));
            }
            dataSource.add(item);
        }
        return dataSource;
    }

    private String getString(ProductData productData, String xmlName) {
        if (productData.getTypeV().equals("Ресурс-П")) {
            String temp = xmlName;
            temp = temp.replace("_G", "");
            temp = temp.replace("_S", "");
            temp = temp.replace("fr_", "");
            if (temp.split("_").length > 6)
                xmlName = xmlName.substring(0, xmlName.length() - 3);
        }
        return xmlName;
    }

    private ResponseEntity<byte[]> getPdfContent(ProductData productData, String xmlName, String task) throws UnsupportedEncodingException {

        boolean resurs = true;
        if (productData.getTypeV().contains("Канопус-В")) {
            productData.setIdV(productData.getNumberV());
            String code = vehicleService.findVehicle(productData.getNumberV()).getCode();
            productData.setNumberV(code);
            resurs = false;
        }
        if (productData.getTypeV().equals("Ресурс-П"))
            productData.setIdV("RSP");

        try {

            String data = productData.getSceneDate().replace("/", "-").replace(".", "-") + "T" + productData.getSceneTime();
            log.info(data);
            Date date = new SimpleDateFormat("dd-MM-yyyy'T'hh:mm:ss").parse(data);

            ResponseEntity responseEntity = clientCertificationAsutsp.getStatus();
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                Params params = new Params(
                        Integer.parseInt(productData.getNumberV()),
                        Integer.parseInt(productData.getNumberCoil()),
                        date,
                        Integer.parseInt(productData.getNumberTurnOn()),
                        resurs);
                ResponseEntity<Response> responseEntity2 = clientCertificationAsutsp.getParams(params);
                if (responseEntity2.getStatusCode().is2xxSuccessful()) {
                    Response response = responseEntity2.getBody();
                    if (productData.getTypeV().contains("Канопус-В")) {
                        if (productData.getEquipment().equals("МСС")) {
                            if (response.getFocusm() != null)
                                productData.setFocalLength(response.getFocusm().toString());
                            else
                                productData.setFocalLength("н/д");
                        } else {
                            if (response.getFocusp() != null)
                                productData.setFocalLength(response.getFocusp().toString());
                            else
                                productData.setFocalLength("н/д");
                        }
                        productData.setDeltaTime(response.getDelta_time().toString());
                    } else {
                        if (response.getKa_gam() != null)
                            productData.setAlpha(response.getKa_gam().toString());
                        else
                            productData.setAlpha("н/д");
                        if (response.getKa_fi() != null)
                            productData.setOmega(response.getKa_fi().toString());
                        else
                            productData.setOmega("н/д");
                        if (response.getKa_psi() != null)
                            productData.setKappa(response.getKa_psi().toString());
                        else
                            productData.setKappa("н/д");
                        if (response.getFocusp() != null)
                            productData.setFocalLength(response.getFocusp().toString());
                        else
                            productData.setFocalLength("н/д");
                    }
                    if (response.getKa_hn() != null)
                        productData.setOrbitAltitude(response.getKa_hn().toString());
                    else
                        productData.setOrbitAltitude("н/д");
                    if (response.getS_to_point() != null)
                        productData.setSlantRange(response.getS_to_point().toString());
                    else
                        productData.setSlantRange("н/д");
                } else {
                    log.info("Получить данные от АСУЦП не удалось");
                }
            }
        } catch (ParseException e) {
            log.info("getPdfContent сервис не доступен");
        }

        if (productData.getTypeV().contains("Канопус-В")) {
            List<AccumulationLine> accumulationLines = productData.getAccumulationLines();
            if (productData.getEquipment().equals("МСС")) {
                accumulationLines.forEach(accumulationLine -> {
                    if (accumulationLine.getLabel().equals("канал 1") || accumulationLine.getLabel().equals("канал 2"))
                        accumulationLine.setData("-");
                    if (accumulationLine.getLabel().equals("канал 3"))
                        accumulationLine.setData("-");
                    if (accumulationLine.getLabel().equals("канал 4"))
                        accumulationLine.setData("-");
                });
            } else {
                accumulationLines.forEach(accumulationLine -> accumulationLine.setData("15"));
            }
            productData.setAccumulationLines(accumulationLines);
            if (!productData.getAlpha().isEmpty() && !productData.getAlpha().equals("") &&
                    !productData.getOrbitAltitude().isEmpty() && !productData.getOrbitAltitude().equals("") && !productData.getOrbitAltitude().equals("0.0") &&
                    !productData.getFocalLength().isEmpty() && !productData.getFocalLength().equals("") && !productData.getFocalLength().equals("0.0")) {
                double alpha = Double.parseDouble(productData.getAlpha());
                double omega = Double.parseDouble(productData.getOmega());
                double r = Double.parseDouble("6370000");
                double h = Double.parseDouble(productData.getOrbitAltitude());
                double f = Double.parseDouble(productData.getFocalLength()) * Math.pow(10, -3);
                log.info("f " + f);
                log.info("h " + h);
                log.info("alpha " + alpha);
                log.info("omega " + omega);

                double slantRange = Math.abs(h / Math.cos(Math.toRadians(alpha)));
                log.info("slantRange " + slantRange);

                double gamma = Math.acos(Math.cos(Math.toRadians(alpha)) * Math.cos(Math.toRadians(omega)));
                log.info("gamma " + Math.toDegrees(gamma));

                double rh = r + h;
                double m = (rh * (Math.cos(gamma) - Math.sqrt(Math.pow(r / rh, 2) - Math.pow(Math.sin(gamma), 2)))) / f;
                double e = Math.asin((rh / r) * Math.sin(gamma));

                double gsd = m * 7.4 * Math.pow(10, -6) / Math.cos(e);
                log.info("gsd " + gsd);
                productData.setOrbitAltitude(Double.toString(h / 1000));
                productData.setSideSize(String.valueOf(gsd));
                productData.setAngleOptSys(Double.toString(Math.toDegrees(gamma)));
                productData.setSlantRange(String.valueOf(slantRange / 1000));
            } else {
                log.info("Невозмодно вычислить параметры пунтков 11 12 16, данных не достаточно!");
                return ResponseEntity.badRequest().build();
            }
        } else if (productData.getTypeV().contains("Ресурс-П")) {
            String fileName = xmlName;
            fileName = fileName.replace("fr_", "");
            fileName = fileName.replace(".xml", "");
            String[] strings = fileName.split("_");
            String result = String.join("_", strings[0], strings[1], strings[2], strings[3], strings[4], strings[5], "00");
            String rspPath = archiveService.getRsp(result);
            RspParams rspParams = parseRspService.parseRsp(rspPath);

            if (!rspParams.getRspOEPs().stream().allMatch(rspOEP -> rspOEP.getTypeCODer().equals("4"))) {
                log.info("Вид сжатия не соответствует ДИКМ!");
                return ResponseEntity.badRequest().build();
            }

            double alpha = Double.parseDouble(rspParams.getAlpha());
            productData.setAlpha(rspParams.getAlpha());
            productData.setFocalLength(rspParams.getFocalL());
            List<AccumulationLine> accumulationLines = productData.getAccumulationLines();
            if (productData.getRange().equals("панхроматический")) {
                rspParams.getRspOEPs().stream().filter(rspOEP -> rspOEP.getNumber().equals("10")).findFirst()
                        .ifPresent(rspOEP -> {
                            accumulationLines.forEach(accumulationLine -> accumulationLine.setData(rspOEP.getLineQuant()));
                        });
            } else {
                rspParams.getRspOEPs().forEach(rspOEP ->
                        accumulationLines.forEach(accumulationLine -> {
                            if (accumulationLine.getLabel().contains(rspOEP.getNumber()))
                                accumulationLine.setData(rspOEP.getLineQuant());
                        }));
            }
            productData.setAccumulationLines(accumulationLines);

            double h = Double.parseDouble(productData.getOrbitAltitude());
            log.info("alpha " + alpha);
            log.info("Math.cos(alpha) " + Math.cos(Math.toRadians(alpha)));
            double slantRange = Math.abs(h / Math.cos(Math.toRadians(alpha)));
            log.info("slantRange " + slantRange);
            productData.setOrbitAltitude(Double.toString(h / 1000));
            productData.setSlantRange(String.valueOf(slantRange / 1000));
        }

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        byte[] pdfContents = pdfGenerator.createPdf("passport", productData);

        String filename = productData.getApplicationNumber() + "_" + task + "_" + xmlName + ".pdf";
        log.info("Сформирован паспорт -> {}", filename);
        filename = URLEncoder.encode(filename, "UTF-8");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        headers.add("Access-Control-Expose-Headers", "Content-Disposition");
        return new ResponseEntity<>(pdfContents, headers, HttpStatus.OK);
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

    private CertificateData getCertificateDataContent(CertificateData certificateData) {

        boolean resurs = true;
        if (certificateData.getTypeV().contains("Канопус-В")) {
            certificateData.setIdV(certificateData.getNumberV());
            String code = vehicleService.findVehicle(certificateData.getNumberV()).getCode();
            certificateData.setNumberV(code);
            resurs = false;
        }
        if (certificateData.getTypeV().equals("Ресурс-П"))
            certificateData.setIdV("RSP");

        try {

            String data = certificateData.getSceneDate().replace("/", "-").replace(".", "-") + "T" + certificateData.getSceneTime();
            log.info(data);
            Date date = new SimpleDateFormat("dd-MM-yyyy'T'hh:mm:ss").parse(data);

            ResponseEntity responseEntity = clientCertificationAsutsp.getStatus();
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                Params params = new Params(
                        Integer.parseInt(certificateData.getNumberV()),
                        Integer.parseInt(certificateData.getNumberCoil()),
                        date,
                        Integer.parseInt(certificateData.getNumberTurnOn()),
                        resurs);
                ResponseEntity<Response> responseEntity2 = clientCertificationAsutsp.getParams(params);
                if (responseEntity2.getStatusCode().is2xxSuccessful()) {
                    Response response = responseEntity2.getBody();
                    if (certificateData.getTypeV().contains("Канопус-В")) {
                        if (certificateData.getEquipment().equals("МСС")) {
                            if (response.getFocusm() != null)
                                certificateData.setFocalLength(response.getFocusm().toString());
                            else
                                certificateData.setFocalLength("н/д");
                        } else {
                            if (response.getFocusp() != null)
                                certificateData.setFocalLength(response.getFocusp().toString());
                            else
                                certificateData.setFocalLength("н/д");
                        }
                        certificateData.setDeltaTime(response.getDelta_time().toString());
                    } else {
                        if (response.getKa_gam() != null)
                            certificateData.setAlpha(response.getKa_gam().toString());
                        else
                            certificateData.setAlpha("н/д");
                        if (response.getKa_fi() != null)
                            certificateData.setOmega(response.getKa_fi().toString());
                        else
                            certificateData.setOmega("н/д");
                        if (response.getKa_psi() != null)
                            certificateData.setKappa(response.getKa_psi().toString());
                        else
                            certificateData.setKappa("н/д");
                        if (response.getFocusp() != null)
                            certificateData.setFocalLength(response.getFocusp().toString());
                        else
                            certificateData.setFocalLength("н/д");
                    }
                    if (response.getKa_hn() != null)
                        certificateData.setOrbitAltitude(response.getKa_hn().toString());
                    else
                        certificateData.setOrbitAltitude("н/д");
                    if (response.getS_to_point() != null)
                        certificateData.setSlantRange(response.getS_to_point().toString());
                    else
                        certificateData.setSlantRange("н/д");
                } else {
                    log.info("Получить данные от АСУЦП не удалось");
                }
            }
        } catch (ParseException e) {
            log.info("getPdfContent сервис не доступен");
        }

        if (certificateData.getTypeV().contains("Канопус-В")) {
            List<AccumulationLine> accumulationLines = certificateData.getAccumulationLines();
            if (certificateData.getEquipment().equals("МСС")) {
                accumulationLines.forEach(accumulationLine -> {
                    if (accumulationLine.getLabel().equals("канал 1") || accumulationLine.getLabel().equals("канал 2"))
                        accumulationLine.setData("-");
                    if (accumulationLine.getLabel().equals("канал 3"))
                        accumulationLine.setData("-");
                    if (accumulationLine.getLabel().equals("канал 4"))
                        accumulationLine.setData("-");
                });
            } else {
                accumulationLines.forEach(accumulationLine -> accumulationLine.setData("15"));
            }
            certificateData.setAccumulationLines(accumulationLines);
            if (!certificateData.getAlpha().isEmpty() && !certificateData.getAlpha().equals("") &&
                    !certificateData.getOrbitAltitude().isEmpty() && !certificateData.getOrbitAltitude().equals("") && !certificateData.getOrbitAltitude().equals("0.0") &&
                    !certificateData.getFocalLength().isEmpty() && !certificateData.getFocalLength().equals("") && !certificateData.getFocalLength().equals("0.0")) {
                double alpha = Double.parseDouble(certificateData.getAlpha());
                double omega = Double.parseDouble(certificateData.getOmega());
                double r = Double.parseDouble("6370000");
                double h = Double.parseDouble(certificateData.getOrbitAltitude());
                double f = Double.parseDouble(certificateData.getFocalLength()) * Math.pow(10, -3);
                log.info("f " + f);
                log.info("h " + h);
                log.info("alpha " + alpha);
                log.info("omega " + omega);

                double slantRange = Math.abs(h / Math.cos(Math.toRadians(alpha)));
                log.info("slantRange " + slantRange);

                double gamma = Math.acos(Math.cos(Math.toRadians(alpha)) * Math.cos(Math.toRadians(omega)));
                log.info("gamma " + Math.toDegrees(gamma));

                double rh = r + h;
                double m = (rh * (Math.cos(gamma) - Math.sqrt(Math.pow(r / rh, 2) - Math.pow(Math.sin(gamma), 2)))) / f;
                double e = Math.asin((rh / r) * Math.sin(gamma));

                double gsd = m * 7.4 * Math.pow(10, -6) / Math.cos(e);
                log.info("gsd " + gsd);
                certificateData.setOrbitAltitude(Double.toString(h / 1000));
                certificateData.setSideSize(String.valueOf(gsd));
                certificateData.setAngleOptSys(Double.toString(Math.toDegrees(gamma)));
                certificateData.setSlantRange(String.valueOf(slantRange / 1000));
            } else {
                log.info("Невозмодно вычислить параметры пунтков 11 12 16, данных не достаточно!");
                return certificateData;
            }
        } else if (certificateData.getTypeV().contains("Ресурс-П")) {
            String[] strings = certificateData.getPdfName().split("_");
            String result = String.join("_", strings[0], strings[1], strings[2], strings[3], strings[4], strings[5], "00");
            String rspPath = archiveService.getRsp(result);
            RspParams rspParams = parseRspService.parseRsp(rspPath);

            if (!rspParams.getRspOEPs().stream().allMatch(rspOEP -> rspOEP.getTypeCODer().equals("4"))) {
                log.info("Вид сжатия не соответствует ДИКМ!");
                return null;
            }

            double alpha = Double.parseDouble(rspParams.getAlpha());
            certificateData.setAlpha(rspParams.getAlpha());
            certificateData.setFocalLength(rspParams.getFocalL());
            List<AccumulationLine> accumulationLines = certificateData.getAccumulationLines();
            if (certificateData.getRange().equals("панхроматический")) {
                rspParams.getRspOEPs().stream().filter(rspOEP -> rspOEP.getNumber().equals("10")).findFirst()
                        .ifPresent(rspOEP -> {
                            accumulationLines.forEach(accumulationLine -> accumulationLine.setData(rspOEP.getLineQuant()));
                        });
            } else {
                rspParams.getRspOEPs().forEach(rspOEP ->
                        accumulationLines.forEach(accumulationLine -> {
                            if (accumulationLine.getLabel().contains(rspOEP.getNumber()))
                                accumulationLine.setData(rspOEP.getLineQuant());
                        }));
            }
            certificateData.setAccumulationLines(accumulationLines);

            double h = Double.parseDouble(certificateData.getOrbitAltitude());
            log.info("alpha " + alpha);
            log.info("Math.cos(alpha) " + Math.cos(Math.toRadians(alpha)));
            double slantRange = Math.abs(h / Math.cos(Math.toRadians(alpha)));
            log.info("slantRange " + slantRange);
            certificateData.setOrbitAltitude(Double.toString(h / 1000));
            certificateData.setSlantRange(String.valueOf(slantRange / 1000));
        }
        return certificateData;
    }
}
