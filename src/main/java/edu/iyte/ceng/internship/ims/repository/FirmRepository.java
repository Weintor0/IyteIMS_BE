package edu.iyte.ceng.internship.ims.repository;

import java.util.Optional;

import edu.iyte.ceng.internship.ims.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.iyte.ceng.internship.ims.entity.Firm;

public interface FirmRepository extends JpaRepository<Firm, String> {
    Optional<Firm> findFirmByFirmName(String name);

    Optional<Firm> findFirmByBusinessRegistrationNumber(String registrationNumber);

    //Optional<Firm> findFirmById(String id);

    Optional<Firm> findFirmByUser(User user);
}
