package org.niitp.mongo;

import org.niitp.model.CertificateDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateDocumentRepository extends MongoRepository<CertificateDocument, String> {
}
