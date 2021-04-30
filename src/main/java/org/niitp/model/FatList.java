package org.niitp.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class FatList implements Serializable {
    private List<ThinList> files = new ArrayList<>();
    private String name;
}
