package org.niitp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MyFileName {

    String label;
    String data;
    String size;

    public MyFileName(@JsonProperty(value = "label") String label,
                      @JsonProperty(value = "data") String data,
                      @JsonProperty(value = "size") String size) {
        this.label = label;
        this.data = data;
        this.size = size;
    }

    public MyFileName(String name, Long size) {
        this.data = name;
        this.size = size.toString();
        this.label = "";
    }

    public MyFileName(String name, Long size, String label) {
        this.data = name;
        this.size = size.toString();
        this.label = label;
    }
}
