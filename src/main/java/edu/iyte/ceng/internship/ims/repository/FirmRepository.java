package edu.iyte.ceng.internship.ims.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.iyte.ceng.internship.ims.entity.Firm;

public interface FirmRepository extends JpaRepository<Firm, Long> {
    Optional<Firm> findFirmByFirmName(String name);

    Optional<Firm> findFirmByBusinessRegistrationNumber(String registrationNumber);

    Optional<Firm> findFirmById(Long userId);
}
