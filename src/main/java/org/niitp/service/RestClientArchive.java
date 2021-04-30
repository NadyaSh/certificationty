package org.niitp.service;

import lombok.extern.slf4j.Slf4j;
import org.niitp.model.archive.RetrieveResponse;
import org.niitp.model.archive.SearchResponse;
import org.niitp.model.archive.TaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RestClientArchive {

    @Autowired
    RestTemplateResponseErrorHandler restTemplateResponseErrorHandler;

    @Value("${service.url.archive}")
    String service;

    private final RestTemplate rest;
    private final HttpHeaders headers;

    public RestClientArchive() {
        this.rest = new RestTemplate();
        this.headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
    }

    public ResponseEntity<SearchResponse> getListFiles(String name) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("object_name", name);
            params.put("content", "json");
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            rest.setErrorHandler(restTemplateResponseErrorHandler);
            return rest.exchange(service + "/v2/objects/search?object_name={object_name}&content={content}", HttpMethod.GET, requestEntity, SearchResponse.class, params);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<RetrieveResponse> getRsp(Long id) {
        try {
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            Map<String, String> params = new HashMap<>();
            params.put("object_type", "1");
            params.put("mode", "2");
            params.put("client_ip", "auto");
            params.put("content", "json");
            rest.setErrorHandler(restTemplateResponseErrorHandler);
            return rest.exchange(service + "/v2/objects/" + id + "/retrieve?object_type={object_type}&mode={mode}&client_ip={client_ip}&content={content}", HttpMethod.GET, requestEntity, RetrieveResponse.class, params);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<TaskResponse> getStatus(Long taskId) {
        try {
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            Map<String, String> params = new HashMap<>();
            params.put("task_type", "1");
            params.put("content", "json");
            rest.setErrorHandler(restTemplateResponseErrorHandler);
            return rest.exchange(service + "/v2/tasks/" + taskId + "?task_type={task_type}&content={content}", HttpMethod.GET, requestEntity, TaskResponse.class, params);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
