package org.niitp;

import org.niitp.model.*;
import org.niitp.mongo.ChannelDocumentRepository;
import org.niitp.mongo.ParamsDocumentRepository;
import org.niitp.mongo.VehicleDocumentRepository;
import org.niitp.repository.ChannelRepository;
import org.niitp.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.thymeleaf.TemplateEngine;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.TimeZone;


@SpringBootApplication
@EntityScan(basePackageClasses = {
        MyApplication.class,
        Jsr310JpaConverters.class
})
public class MyApplication {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ParamsDocumentRepository paramsDocumentRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleDocumentRepository vehicleDocumentRepository;

    @Autowired
    private ChannelDocumentRepository channelDocumentRepository;

    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        mongoTemplate.dropCollection("params");
        paramsDocumentRepository.save(new ParamsDocument(
                "Канопус-В",
                "7.4*7.4", "",
                "",
                "",
                "",
                "не менее 10",
                "40",
                "до 50",
                "8",
                "",
                ""
        ));
        paramsDocumentRepository.save(new ParamsDocument(
                "Ресурс-П",
                "",
                "18*18",
                "мультиспектральный",
                "6*6",
                "панхроматический",
                "не менее 30",
                "45",
                "15-30",
                "",
                "ДИКМ",
                "2,5"

        ));

        List<ChannelEntity> channelEntityList = channelRepository.findAll();
        List<VehicleEntity> vehicleEntityList = vehicleRepository.findAll();

        mongoTemplate.dropCollection("channels");
        channelEntityList.forEach(channelEntity -> channelDocumentRepository.save(new ChannelDocument(channelEntity.getVehicle(), channelEntity.getChannelNum(), channelEntity.getColor())));
        mongoTemplate.dropCollection("vehicles");
        vehicleEntityList.forEach(vehicleEntity -> {
            vehicleDocumentRepository.save(new VehicleDocument(vehicleEntity.getId(), vehicleEntity.getType(), vehicleEntity.getName(), vehicleEntity.getCode()));
        });
    }

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}

