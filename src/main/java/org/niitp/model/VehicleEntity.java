package org.niitp.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "vehicle")
public class VehicleEntity {
    @Id
    private String id;
    private String type;
    private String name;
    private String code;
}
