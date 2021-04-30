package org.niitp.model;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
public class GetPlanonkaUkasoi implements Serializable {
    @Id
    private Integer Ka_id;
    private Integer mar_id;
    private String fokus;
    private Float str_spektr;
    private String step;
    private Float ka_gam;
    private Float ka_fi;
    private Float ka_psi;
    private Float ka_hn;
    private Float s_to_point;

}
