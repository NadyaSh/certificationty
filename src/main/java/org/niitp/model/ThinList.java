package org.niitp.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThinList implements Serializable {
    private String name;
    private String content;
    private Long size;
    private String extension;
}
