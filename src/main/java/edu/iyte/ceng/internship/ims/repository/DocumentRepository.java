package edu.iyte.ceng.internship.ims.repository;

import edu.iyte.ceng.internship.ims.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
}
