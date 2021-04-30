package org.niitp.model.archive;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RetrieveResponse {
    @JsonProperty("Response")
    private RetrieveResponseData response;
}
