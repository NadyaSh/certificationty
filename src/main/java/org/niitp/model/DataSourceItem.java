package org.niitp.model;

import lombok.Data;

@Data
public class DataSourceItem {
    private Long id;
    private String Application;
    private String Task;
    private String FileName;
    private String DateIn;
    private String DateCertification;
    private String Vehicle;
}
