package org.niitp.model.archive;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchFolder {

    @JsonProperty("Folder")
    private SearchObjectList folder;
}