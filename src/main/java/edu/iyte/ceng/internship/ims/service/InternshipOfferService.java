package edu.iyte.ceng.internship.ims.service;

import edu.iyte.ceng.internship.ims.entity.InternshipOffer;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.entity.UserRole;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.model.request.CreateInternshipOfferRequest;
import edu.iyte.ceng.internship.ims.model.request.UpdateInternshipOfferRequest;
import edu.iyte.ceng.internship.ims.model.response.InternshipOfferResponse;
import edu.iyte.ceng.internship.ims.repository.InternshipOfferRepository;
import edu.iyte.ceng.internship.ims.service.mapper.InternshipOfferMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class InternshipOfferService {
    private final InternshipOfferRepository internshipOfferRepository;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final InternshipOfferMapper internshipOfferMapper;


    public InternshipOfferResponse getInternshipOffer(String title) {
        InternshipOffer internshipOffer = internshipOfferRepository.findInternshipOfferByTitle(title).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing,
                        "Internship Offer with" + title + " does not exist")
        );
        return internshipOfferMapper.fromEntity(internshipOffer);
    }

    @Transactional(rollbackFor = Throwable.class)
    public InternshipOfferResponse createInternshipOffer(CreateInternshipOfferRequest internshipOfferRequest) {
        ensureCreatePrivilege();
        InternshipOffer internshipOffer = new InternshipOffer();
        internshipOfferMapper.fromCreateRequest(internshipOffer, internshipOfferRequest, authenticationService.getCurrentUser().getId());
        internshipOfferRepository.save(internshipOffer);
        return internshipOfferMapper.fromEntity(internshipOffer);
    }

    public Page<InternshipOfferResponse> listInternshipOffer(Pageable pageable) {
        if(ensureReadPrivilege()){
            return internshipOfferRepository.findAll(pageable).map(internshipOfferMapper::fromEntity);}
        return internshipOfferRepository.findAllByAccepted(Boolean.TRUE, pageable)
                .map(internshipOfferMapper::fromEntity);
    }

    @Transactional(rollbackFor = Throwable.class)
    public InternshipOfferResponse updateInternshipOffer(UpdateInternshipOfferRequest internshipOfferRequest,  String internshipOfferid) {
        ensureUpdatePrivilege();
        InternshipOffer internshipOffer = internshipOfferRepository.findInternshipOfferById(internshipOfferid).orElseThrow(() -> new BusinessException(ErrorCode.ResourceMissing,
                "Internship Offer with" + internshipOfferid + " does not exist"));
        internshipOfferMapper.fromUpdateRequest(internshipOffer, internshipOfferRequest);
        internshipOfferRepository.save(internshipOffer);
        return internshipOfferMapper.fromEntity(internshipOffer);

    }


    private void ensureCreatePrivilege() {
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser.getUserRole() != UserRole.Firm) {
            throw new BusinessException(ErrorCode.Unauthorized, "A firm can only  create an internship offer");
        }

    }

    private void ensureUpdatePrivilege() {

        User currentUser = authenticationService.getCurrentUser();


        if (currentUser.getUserRole() != UserRole.InternshipCoordinator) {
            throw new BusinessException(ErrorCode.Unauthorized, "A  internship coordinator can only edit an internship offer");
        }

    }

    private boolean ensureReadPrivilege() {

        User currentUser = authenticationService.getCurrentUser();


        if (currentUser.getUserRole() != UserRole.InternshipCoordinator) {
            return false ;
        }
        return true;
    }

}
