package org.niitp.service;

import org.niitp.model.XmlEntity;

import java.util.List;

public interface XmlService {
    XmlEntity loadXml(String filename, String vehicle, String xmldata);

    List<XmlEntity> findByFilename(String filename);

    List<XmlEntity> findByApplication(String application);

    XmlEntity findById(Long id);

    List<XmlEntity> findAll();
}
