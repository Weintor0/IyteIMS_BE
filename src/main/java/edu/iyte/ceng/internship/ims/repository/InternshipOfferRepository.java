package edu.iyte.ceng.internship.ims.repository;

import edu.iyte.ceng.internship.ims.entity.InternshipOffer;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InternshipOfferRepository extends JpaRepository<InternshipOffer, String> {
    Optional<InternshipOffer> findInternshipOfferByTitle(String title);
    Optional<InternshipOffer> findInternshipOfferById(String id);
    Page<InternshipOffer> findAllByAccepted(boolean accepted, Pageable pageable);
}
