package org.niitp.service;

import lombok.extern.slf4j.Slf4j;
import org.niitp.model.Params;
import org.niitp.model.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class RestClientCertificationAsutsp {

    @Value("${service.url.certification.asutsp}")
    String service;

    private final RestTemplate rest;
    private final HttpHeaders headers;

    public RestClientCertificationAsutsp() {
        this.rest = new RestTemplate();
        this.headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
    }

    public ResponseEntity<Response> getParams(Params params) {
        try {
            HttpEntity<?> requestEntity = new HttpEntity<>(params, headers);
            return rest.exchange(service + "/params", HttpMethod.POST, requestEntity, Response.class);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<?> getStatus() {
        try {
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            return rest.exchange(service + "/status", HttpMethod.GET, requestEntity, String.class);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
