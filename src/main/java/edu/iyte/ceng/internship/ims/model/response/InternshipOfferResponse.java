package edu.iyte.ceng.internship.ims.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternshipOfferResponse {
    private String offerId ;
    private String firmId ;
    private String title;
    private String jobTitle;
    private String content;
}
