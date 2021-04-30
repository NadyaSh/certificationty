package org.niitp.service;

import lombok.extern.slf4j.Slf4j;
import org.niitp.model.archive.RetrieveResponse;
import org.niitp.model.archive.SearchObject;
import org.niitp.model.archive.SearchResponse;
import org.niitp.model.archive.TaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArchiveService {
    @Autowired
    private RestClientArchive restClientArchive;

    public String getRsp(String name) {
        log.info("getRsp " + name);
        AtomicReference<String> path = new AtomicReference<>("");
        ResponseEntity<SearchResponse> responseEntity = restClientArchive.getListFiles(name);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("getRsp " + responseEntity.getBody());
            SearchResponse searchResponse = responseEntity.getBody();
            searchResponse.getResponse().getFolder().getObjects().stream().
                    filter(searchObject -> searchObject.getName().contains(".rsp")).
                    sorted(Comparator.comparing(SearchObject::getDateTime).reversed()).
                    collect(Collectors.toList()).stream().findFirst().
                    ifPresent(searchObject -> {
                        ResponseEntity<RetrieveResponse> responseEntity2 = restClientArchive.getRsp(searchObject.getId());
                        if (responseEntity2.getStatusCode().is2xxSuccessful()) {
                            log.info("getRsp " + responseEntity2.getBody());
                            RetrieveResponse retrieveResponse = responseEntity2.getBody();
                            AtomicReference<Integer> percent = new AtomicReference<>(0);
                            while (percent.get() != 100) {
                                ResponseEntity<TaskResponse> responseEntity3 = restClientArchive.getStatus(retrieveResponse.getResponse().getTask().getId());
                                if (responseEntity3.getStatusCode().is2xxSuccessful()) {
                                    log.info("getRsp " + responseEntity3.getBody());
                                    TaskResponse taskResponse = responseEntity3.getBody();
                                    percent.set(taskResponse.getResponse().getTask().getPercent());
                                    if (percent.get().equals(100)) {
                                        log.info(taskResponse.getResponse().toString());
                                        log.info(taskResponse.getResponse().getObjects().toString());
                                        log.info(taskResponse.getResponse().getObjects().stream().findFirst().toString());
                                        log.info(taskResponse.getResponse().getObjects().get(0).toString());
                                        path.set(taskResponse.getResponse().getObjects().get(0).getPath() + File.separator + taskResponse.getResponse().getObjects().get(0).getName());
                                    }
                                } else {
                                    log.error("getRsp " + responseEntity3.toString());
                                    return;
                                }
                            }

                        } else {
                            log.error("getRsp " + responseEntity2.toString());
                            return;
                        }
                    });
        } else {
            log.error("getRsp " + responseEntity.toString());
        }
        log.info("getRsp " + path.get());
        return path.get();
    }
}
