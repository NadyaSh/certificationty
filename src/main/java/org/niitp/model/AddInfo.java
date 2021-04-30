package org.niitp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddInfo {
    String info;

    public AddInfo(@JsonProperty(value = "info") String info) {
        this.info = info;
    }
}
