package edu.iyte.ceng.internship.ims.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@AssociatedWithEntity(entityName = Internship.entityName)
public class Internship extends BaseEntity {
   public static final String entityName = "Internship";

   @Column(name = "internship_status", nullable = false)
   @Enumerated(EnumType.STRING)
   private InternshipStatus status;

   @ManyToOne
   @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
   private Student student;

   @ManyToOne
   @JoinColumn(name = "internship_offer_id", referencedColumnName = "id", nullable = false)
   private InternshipOffer internshipOffer;

   @ManyToOne
   @JoinColumn(name = "application_letter_id", referencedColumnName = "id", nullable = false)
   private Document applicationLetter;

   @ManyToOne
   @JoinColumn(name = "application_form_id_by_student", referencedColumnName = "id")
   private Document applicationFormByStudent;

   @ManyToOne
   @JoinColumn(name = "application_form_id_by_firm", referencedColumnName = "id")
   private Document applicationFormByFirm;

   @ManyToOne
   @JoinColumn(name = "employment_document_id", referencedColumnName = "id")
   private Document employmentDocument;

   @ManyToOne
   @JoinColumn(name = "summer_practice_report_id", referencedColumnName = "id")
   private Document summerPracticeReport;

   @ManyToOne
   @JoinColumn(name = "company_form_id", referencedColumnName = "id")
   private Document companyForm;

   @ManyToOne
   @JoinColumn(name = "survey", referencedColumnName = "id")
   private Document survey;

   public String getStudentId() {
      return student.getUserId();
   }

   public String getFirmId() {
      return internshipOffer.getFirmId();
   }
}
