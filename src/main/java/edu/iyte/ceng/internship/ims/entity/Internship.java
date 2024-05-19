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

   @ManyToOne
   @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
   private Student student;

   /** Application Letter Process */

   @ManyToOne
   @JoinColumn(name = "internship_offer_id", referencedColumnName = "id", nullable = false)
   private InternshipOffer internshipOffer;

   @ManyToOne
   @JoinColumn(name = "application_letter_id", referencedColumnName = "id", nullable = false, unique = true)
   private Document applicationLetter;

   @Column(name = "application_letter_acceptance", nullable = false)
   @Enumerated(EnumType.STRING)
   private AcceptanceStatus applicationLetterAcceptanceStatus;

   /** Application Form Process */

   @ManyToOne
   @JoinColumn(name = "application_form_id_by_student", referencedColumnName = "id", unique = true)
   private Document applicationFormByStudent;

   @ManyToOne
   @JoinColumn(name = "application_form_id_by_firm", referencedColumnName = "id", unique = true)
   private Document applicationFormByFirm;

   @Column(name = "application_form_acceptance")
   @Enumerated(EnumType.STRING)
   private AcceptanceStatus applicationFormAcceptanceStatus;

   /** SSI Transactions Process */

   @Column(name = "current_transactions_state")
   @Enumerated(EnumType.STRING)
   private TransactionsState currentTransactionsState;

   @ManyToOne
   @JoinColumn(name = "employment_document_id", referencedColumnName = "id", unique = true)
   private Document employmentDocument;

   /** Internship Process */

   @ManyToOne
   @JoinColumn(name = "summer_practice_report_id", referencedColumnName = "id", unique = true)
   private Document summerPracticeReport;

   @Column(name = "summer_practice_report_acceptance_by_firm")
   @Enumerated(EnumType.STRING)
   private AcceptanceStatus summerPracticeReportAcceptanceByFirm;

   @Column(name = "summer_practice_report_acceptance_by_coordinator")
   @Enumerated(EnumType.STRING)
   private AcceptanceStatus summerPracticeReportAcceptanceByCoordinator;

   /** Final Process */

   @ManyToOne
   @JoinColumn(name = "company_form_id", referencedColumnName = "id")
   private Document companyForm;

   @ManyToOne
   @JoinColumn(name = "survey", referencedColumnName = "id")
   private Document survey;
}
