package org.niitp.model.archive;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RetrieveStatus {
    private String message;
    private Boolean success;
}