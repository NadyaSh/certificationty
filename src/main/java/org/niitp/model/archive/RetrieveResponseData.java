package org.niitp.model.archive;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RetrieveResponseData {
    @JsonProperty("Status")
    private RetrieveStatus status;
    @JsonProperty("Task")
    private RetrieveTask task;
}