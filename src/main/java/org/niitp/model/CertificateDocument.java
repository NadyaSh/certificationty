package org.niitp.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Slf4j
//@NoArgsConstructor
@Document(collection = "certificates")
public class CertificateDocument {
    @Id
    private String id;

    private String title;

    private Binary content;

    public CertificateDocument(String title, Binary content) {
        this.title = title;
        this.content = content;
    }
}