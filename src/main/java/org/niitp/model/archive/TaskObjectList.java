package org.niitp.model.archive;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TaskObjectList {
    @JsonProperty("Object")
    private List<TaskObject> objects;

    @JsonProperty("Task")
    private TaskTask task;
}