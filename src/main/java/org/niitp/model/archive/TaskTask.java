package org.niitp.model.archive;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskTask {

    private Long id;
    private String message;
    private Integer percent;
    private Integer status;
}
