package org.niitp.service;

import org.niitp.exception.ResourceNotFoundException;
import org.niitp.model.XmlEntity;
import org.niitp.repository.XmlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class XmlServiceImpl implements XmlService {
    @Autowired
    private XmlRepository xmlRepository;

    @Override
    public XmlEntity loadXml(String filename, String vehicle, String xmldata) {
        XmlEntity xml = new XmlEntity();
        xml.setDatetime(Timestamp.valueOf(LocalDateTime.now()));
        xml.setFilename(filename);
        xml.setVehicle(vehicle);
        xml.setXml(xmldata);
        return xmlRepository.saveAndFlush(xml);
    }

    @Override
    public List<XmlEntity> findByFilename(String filename) {
        return xmlRepository.findByFilename(filename);
    }

    @Override
    public List<XmlEntity> findByApplication(String application) {
        return xmlRepository.findByApplication(application);
    }

    @Override
    public XmlEntity findById(Long id) {
        return xmlRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("XmlEntity", "xml", id));
    }

    @Override
    public List<XmlEntity> findAll() {
        return xmlRepository.findAll();
    }
}
