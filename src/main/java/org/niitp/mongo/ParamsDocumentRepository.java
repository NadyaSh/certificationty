package org.niitp.mongo;

import org.niitp.model.ParamsDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParamsDocumentRepository extends MongoRepository<ParamsDocument, String> {
    Optional<ParamsDocument> findByTypeV(String typeV);
}
