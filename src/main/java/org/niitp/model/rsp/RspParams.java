package org.niitp.model.rsp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class RspParams {
    private String alpha;
    private String focalL;
    private List<RspOEP> rspOEPs = new ArrayList<>();
}
