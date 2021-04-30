package org.niitp.model.archive;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class SearchObject {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("date_time")
    private Date dateTime;

    private Long id;

    private String name;

    @JsonProperty("parent_id")
    private Long parentId;

    private String path;

    private Integer placement;

    private Long size;

    private String statusFile;
}
