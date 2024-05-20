package edu.iyte.ceng.internship.ims.model.response.internship;

import edu.iyte.ceng.internship.ims.entity.InternshipStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InternshipResponse {
    private String internshipId;
    private InternshipStatus internshipStatus;
    private String studentId;
    private String internshipOfferId;
}
