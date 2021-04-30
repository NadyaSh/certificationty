package org.niitp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class Params implements Serializable {
    private Integer ka;
    private Integer cycle;
    private Date date;
    private Integer turnOn;
    private Boolean resurs;

    public Params(Integer ka, Integer cycle, Date date, Integer turnOn, Boolean resurs) {
        this.ka = ka;
        this.cycle = cycle;
        this.date = date;
        this.turnOn = turnOn;
        this.resurs = resurs;
    }
}
