package edu.iyte.ceng.internship.ims.model.request.internship;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDocumentAcceptanceRequest {
    @NotNull
    private Boolean acceptance;

    @NotNull
    private String feedback;
}
