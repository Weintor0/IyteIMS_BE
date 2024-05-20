package edu.iyte.ceng.internship.ims.repository;

import edu.iyte.ceng.internship.ims.entity.Announcement;
import edu.iyte.ceng.internship.ims.entity.InternshipOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, String> {
    Announcement findByTitle(String title);

    Announcement findAnnouncementById(String id);
}
