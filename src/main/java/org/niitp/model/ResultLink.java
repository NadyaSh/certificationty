package org.niitp.model;

import lombok.Data;

@Data
public class ResultLink {
    private String name;
    private String link;

    public ResultLink(String name, String link) {
        this.name = name;
        this.link = link;
    }
}
