package org.niitp.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@Slf4j
@Document(collection = "channels")
public class ChannelDocument implements Serializable {
    @Id
    private String id;
    private String vehicle;
    private Integer channelNum;
    private String color;

    public ChannelDocument(String vehicle, Integer channelNum, String color) {
        this.vehicle = vehicle;
        this.channelNum = channelNum;
        this.color = color;
    }
}
