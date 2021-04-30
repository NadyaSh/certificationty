package org.niitp.model.archive;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SearchObjectList {
    @JsonProperty("Object")
    private List<SearchObject> objects;
}