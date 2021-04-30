package org.niitp.model.archive;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskObject {

    private Long id;
    private String name;
    private String path;
    private Integer percent;
    private Long size;
    private String statusFile;
}
