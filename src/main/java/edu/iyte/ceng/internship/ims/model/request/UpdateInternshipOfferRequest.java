package edu.iyte.ceng.internship.ims.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateInternshipOfferRequest {
    @NotNull(message = "Job title can't be blank")
    private String jobTitle ;
    @NotNull(message = "Title can't be blank")
    private String title;
    @NotNull(message = "Content can't be blank")
    private String content ;
    @NotNull(message = "You should accept or not")
    private Boolean isAccepted ;
}
