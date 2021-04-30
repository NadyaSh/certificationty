package org.niitp.mongo;

import org.niitp.model.VehicleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleDocumentRepository extends MongoRepository<VehicleDocument, String> {
}
