package org.niitp.mongo;

import org.niitp.model.ChannelDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelDocumentRepository extends MongoRepository<ChannelDocument, String> {
}
