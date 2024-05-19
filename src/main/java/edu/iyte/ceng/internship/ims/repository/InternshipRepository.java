package edu.iyte.ceng.internship.ims.repository;

import edu.iyte.ceng.internship.ims.entity.Firm;
import edu.iyte.ceng.internship.ims.entity.Internship;
import edu.iyte.ceng.internship.ims.entity.Student;
import edu.iyte.ceng.internship.ims.entity.TransactionsState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, String> {
    List<Internship> findByStudent(Student student);

    List<Internship> findByInternshipOffer_FirmId(String firmId);

    List<Internship> findByCurrentTransactionsState(TransactionsState transactionsState);
}
