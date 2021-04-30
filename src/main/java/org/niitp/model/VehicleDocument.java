package org.niitp.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@Slf4j
@Document(collection = "vehicles")
public class VehicleDocument {
    @Id
    private String id;
    private String type;
    private String name;
    private String code;

    public VehicleDocument(String id, String type, String name, String code) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.code = code;
    }
}
