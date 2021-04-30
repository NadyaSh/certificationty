package org.niitp.service;

import org.niitp.model.JsonEntity;
import org.niitp.model.ProductData;

import java.sql.Timestamp;
import java.util.List;

public interface JsonService {
    JsonEntity saveJson(String filename, ProductData data, Timestamp ts);

    JsonEntity findJson(Long id);

    List<JsonEntity> findAll();
}
