package org.niitp.service;

import org.niitp.exception.ResourceNotFoundException;
import org.niitp.model.JsonEntity;
import org.niitp.model.ProductData;
import org.niitp.repository.JsonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JsonServiceImpl implements JsonService {
    @Autowired
    private JsonRepository jsonRepository;

    @Override
    public JsonEntity saveJson(String filename, ProductData data, Timestamp ts) {
        JsonEntity json = new JsonEntity();
        json.setFilename(filename);
        json.setJson(data);
        json.setXmlid(data.getXmlid());
        json.setDatetime(Timestamp.valueOf(LocalDateTime.now()));
        json.setDatetimein(ts);
        return jsonRepository.saveAndFlush(json);
    }

    @Override
    public JsonEntity findJson(Long id) {
        return jsonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JsonEntity", "json", id));
    }

    @Override
    public List<JsonEntity> findAll() {
        return jsonRepository.findAll();
    }
}
