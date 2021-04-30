package org.niitp.model;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

@Data
public class Response implements Serializable {
    @Id
    private Integer ka_id;
    private Integer mar_id;
    private Float ka_gam;
    private Float ka_fi;
    private Float ka_psi;
    private Float ka_hn;
    private Float s_to_point;
    private List<Integer> step;
    private Float focusp;
    private Float focusm;
    private Float delta_time;
}
