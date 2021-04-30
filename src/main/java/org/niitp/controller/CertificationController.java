package org.niitp.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.niitp.model.CertificateDocument;
import org.niitp.mongo.CertificateDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * User: shumakova
 * Date: 2019-08-12
 * Time: 17:00
 * To change this template use File | Settings | File and Code Templates.
 */
@Slf4j
@Controller
@RequestMapping("/certification")
@Api(value = "certification")
public class CertificationController {

    @Autowired
    private CertificateDocumentRepository certificateDocumentRepository;

    @ApiOperation(value = "Возвращает сертификат")
    @GetMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<?> getCertificate(@PathVariable String id) throws UnsupportedEncodingException {
        Optional<CertificateDocument> certificate = certificateDocumentRepository.findById(id);
        if (certificate.isPresent()) {
            String name = URLEncoder.encode(certificate.get().getTitle(), "UTF-8");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");
            headers.setContentDispositionFormData("attachment", name);
            return new ResponseEntity<>(certificate.get().getContent().getData(), headers, HttpStatus.OK);
        } else
            return ResponseEntity.notFound().build();

    }
}
