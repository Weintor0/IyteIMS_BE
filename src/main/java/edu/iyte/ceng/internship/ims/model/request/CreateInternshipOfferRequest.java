package edu.iyte.ceng.internship.ims.model.request;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.InternshipOffer;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@AssociatedWithEntity(entityName = InternshipOffer.entityName)
public class CreateInternshipOfferRequest {
    @NotNull(message = "Job title can't be blank")
    @Size(max = 50)
    private String jobTitle ;
    @Size(max = 50)
    @NotNull(message = "Title can't be blank")
    private String title;
    @NotNull(message = "Content can't be blank")
    @Size(max = 400)
    private String content ;
}
