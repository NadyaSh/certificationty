package org.niitp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Input {
    @NonNull
    private String customer;
    @NonNull
    private String region;
    @NonNull
    private String applicationNumber;
    @NonNull
    private String taskNumber;

    @NonNull
    private String boss;
    @NonNull
    private String controller;
    @NonNull
    private String operator;

    private List<FatList> directories = new ArrayList<>();

}


