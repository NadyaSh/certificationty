package org.niitp.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.host}")
    String host;

    @Override
    protected String getDatabaseName() {
        return "certification";
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(host, 27017);
    }

}