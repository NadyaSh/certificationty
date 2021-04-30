package org.niitp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelRange implements Serializable {
    String label;
    String data;

    public ChannelRange(@JsonProperty(value = "label") String label,
                        @JsonProperty(value = "data") String data) {
        this.label = label;
        this.data = data;
    }
}
