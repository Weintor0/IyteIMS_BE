package edu.iyte.ceng.internship.ims.controller;

import edu.iyte.ceng.internship.ims.model.request.CreateInternshipOfferRequest;
import edu.iyte.ceng.internship.ims.model.request.UpdateInternshipOfferRequest;
import edu.iyte.ceng.internship.ims.model.response.InternshipOfferResponse;
import edu.iyte.ceng.internship.ims.service.InternshipOfferService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/internshipoffer")
public class InternshipOfferController {
    private final InternshipOfferService internshipOfferService;

    @PostMapping("/createinternship")
    public InternshipOfferResponse createInternship(@Valid @RequestBody CreateInternshipOfferRequest internshipOffer) {
        return internshipOfferService.createInternshipOffer(internshipOffer);
    }

    @GetMapping("/list")
    public Page<InternshipOfferResponse> getAllInternshipOffers(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        return internshipOfferService.listInternshipOffer(PageRequest.of(page, size));
    }

    @PutMapping("/update/{offerId}")
    public InternshipOfferResponse updateInternship(@Valid @RequestBody UpdateInternshipOfferRequest internshipOffer, @PathVariable("offerId")String offerId){
        return internshipOfferService.updateInternshipOffer(internshipOffer, offerId);
    }
}
