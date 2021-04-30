package org.niitp.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Result {
    private List<ResultLink> links = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
}
