package edu.iyte.ceng.internship.ims.service.mapper;

import edu.iyte.ceng.internship.ims.entity.InternshipOffer;
import edu.iyte.ceng.internship.ims.model.request.CreateInternshipOfferRequest;
import edu.iyte.ceng.internship.ims.model.request.UpdateInternshipOfferRequest;
import edu.iyte.ceng.internship.ims.model.response.InternshipOfferResponse;

import org.springframework.stereotype.Service;


@Service
public class InternshipOfferMapper {
    public InternshipOfferResponse fromEntity( InternshipOffer offer) {
        InternshipOfferResponse response = InternshipOfferResponse.builder().
                offerId(offer.getId()).
                title(offer.getTitle()).
                jobTitle(offer.getJobTitle()).
                content(offer.getContent()).
                firmId(offer.getFirmId()).
                build();

                return response;
    }

    public InternshipOffer fromCreateRequest(InternshipOffer internshipOffer , CreateInternshipOfferRequest internshipOfferRequest) {
        internshipOffer.setTitle(internshipOfferRequest.getTitle());
        internshipOffer.setContent(internshipOfferRequest.getContent());
        internshipOffer.setAccepted(Boolean.FALSE);
        internshipOffer.setJobTitle(internshipOfferRequest.getJobTitle());
        return internshipOffer;
    }
    public InternshipOffer fromUpdateRequest(InternshipOffer internshipOffer , UpdateInternshipOfferRequest internshipOfferRequest) {
        internshipOffer.setTitle(internshipOfferRequest.getTitle());
        internshipOffer.setContent(internshipOfferRequest.getContent());
        internshipOffer.setAccepted(internshipOfferRequest.getIsAccepted());
        internshipOffer.setJobTitle(internshipOfferRequest.getJobTitle());
        return internshipOffer;
    }
}
