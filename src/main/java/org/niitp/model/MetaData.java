package org.niitp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MetaData {

    private String data;
    private String size;
    private String content;

    public MetaData(@JsonProperty(value = "data") String data,
                    @JsonProperty(value = "size") String size) {
        this.data = data;
        this.size = size;
    }

    public MetaData(String name,
                    Long size,
                    String content) {
        this.data = name;
        this.size = size.toString();
        this.content = content;
    }

    public MetaData(String data, Long size) {
        this.data = data;
        this.size = size.toString();
        this.content = "";
    }
}
