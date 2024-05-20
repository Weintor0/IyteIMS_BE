package edu.iyte.ceng.internship.ims.service;

import edu.iyte.ceng.internship.ims.entity.*;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.model.request.internship.UpdateDocumentAcceptanceRequest;
import edu.iyte.ceng.internship.ims.model.request.notification.CreateNotificationRequest;
import edu.iyte.ceng.internship.ims.model.response.internship.InternshipResponse;
import edu.iyte.ceng.internship.ims.model.response.internship.SendApplicationLetterResponse;
import edu.iyte.ceng.internship.ims.repository.InternshipOfferRepository;
import edu.iyte.ceng.internship.ims.repository.InternshipRepository;
import edu.iyte.ceng.internship.ims.service.mapper.InternshipMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final NotificationService notificationService;
    private final DocumentService documentService;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final InternshipMapper internshipMapper;
    private final InternshipOfferRepository internshipOfferRepository;

    Internship getInternshipByIdInternal(String id) {
        Internship internship = internshipRepository.findById(id).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing, "Internship with id " + id + " does not exist.")
        );
        authenticationService.doIfUserIsStudent((student) -> {
            if (!student.getUserId().equals(internship.getStudentId())) {
                throw new BusinessException(ErrorCode.Forbidden,
                        "A student cannot access another student's internship information.");
            }
        }).doIfUserIsFirm((firm) -> {
            if (!firm.getUserId().equals(internship.getFirmId())) {
                throw new BusinessException(ErrorCode.Forbidden,
                        "A firm cannot access an internship record of another company.");
            }
        }).doIfUserIsDepartmentSecretary((secretary) -> {
            int order = internship.getStatus().getOrder();
            int ssiBegin = InternshipStatus.CoordinatorSentFormToDepartmentSecretary.getOrder();
            int ssiEnd = InternshipStatus.InternshipStarted.getOrder();
            if ((order < ssiBegin || order > ssiEnd) && internship.getEmploymentDocument() == null) {
                throw new BusinessException(ErrorCode.Unauthorized,
                        "The department secretary is only responsible with internships requiring insurance");
            }
        });

        return internship;
    }

    List<Internship> getInternshipsInternal() {
        List<Internship> internships = new ArrayList<>();
        authenticationService.doIfUserIsStudent(
                (student) -> {
                    internships.addAll(internshipRepository.findByStudent(student));
                }
        ).doIfUserIsFirm(
                (firm) -> {
                    internshipRepository.findByInternshipOffer_FirmId(firm.getUserId());
                }
        ).doIfUserIsDepartmentSecretary(
                (secretary) -> {
                    internships.addAll(internshipRepository.findByStatus(InternshipStatus.CoordinatorSentFormToDepartmentSecretary));
                    internships.addAll(internshipRepository.findByStatus(InternshipStatus.DepartmentSecretaryDelegatedToDeansOffice));
                    internships.addAll(internshipRepository.findByEmploymentDocumentIsNotNull());
                }
        ).doIfUserIsInternshipCoordinator(
                (coordinator) -> {
                    internships.addAll(internshipRepository.findAll());
                }
        );
        return internships;
    }

    public InternshipResponse getInternshipById(String internshipId) {
        Internship internship = getInternshipByIdInternal(internshipId);
        return internshipMapper.fromInternship(internship);
    }

    public List<InternshipResponse> getInternships() {
        List<InternshipResponse> responses = new ArrayList<>();
        for (Internship internship : getInternshipsInternal()) {
            responses.add(internshipMapper.fromInternship(internship));
        }
        return responses;
    }

    public SendApplicationLetterResponse sendApplicationLetter(String offerId, MultipartFile file) {
        return authenticationService.doIfUserIsStudentOrElseThrow((student) -> {
            InternshipOffer internshipOffer = internshipOfferRepository.findInternshipOfferById(offerId).orElseThrow(
                    () -> new BusinessException(ErrorCode.ResourceMissing,
                            "Internship offer with ID " + offerId + " does not exist"));

            // Create and save the application letter in the database.
            String letterId = documentService.createDocument(file, internshipOffer.getFirmId()).getDocumentId();

            // Create and save the internship record in the database.
            Internship internship = internshipRepository.save(Internship.builder()
                            .status(InternshipStatus.StudentSentApplicationLetter)
                            .student(student)
                            .internshipOffer(internshipOffer)
                            .applicationLetter(documentService.getDocument(letterId))
                            .build());

            // Send notification to the firm
            notificationService.createNotificationInternal(internshipOffer.getFirmId(), CreateNotificationRequest.builder()
                            .content("The student " + student.getStudentNumber() + " has sent an application letter.")
                            .build());

            return new SendApplicationLetterResponse(internship.getId());
        }, () -> new BusinessException(ErrorCode.Forbidden, "Only students can send application letters"));
    }

    public void updateApplicationLetterAcceptance(String internshipId, UpdateDocumentAcceptanceRequest acceptance) {
        authenticationService.doIfUserIsFirmOrElseThrow((firm) -> {
            // Get the internship record.
            Internship internship = getInternshipByIdInternal(internshipId);

            // Ensure that the letter was not evaluated before. It is not permitted to change the approval state twice.
            if (internship.getStatus() != InternshipStatus.StudentSentApplicationLetter) {
                throw new BusinessException(ErrorCode.Forbidden, "The application letter has already been evaluated");
            }

            // Set the acceptance state
            boolean accepted = acceptance.getAcceptance();
            internship.setStatus(accepted ?
                    InternshipStatus.FirmAcceptedApplicationLetter :
                    InternshipStatus.FirmRejectedApplicationLetter);
            internshipRepository.save(internship);

            // Send notification to the student.
            notificationService.createNotificationInternal(internship.getStudentId(), CreateNotificationRequest
                            .builder().content(firm.getFirmName() + " has " + (accepted ? "accepted" : "rejected")
                                    + " your application letter.").build());

            return null;
        }, () -> new BusinessException(ErrorCode.Forbidden, "Only firms can evaluate application letters"));
    }

    public Document getApplicationLetter(String internshipId) {
        Internship internship = getInternshipByIdInternal(internshipId);
        return internship.getApplicationLetter();
    }

    public void sendApplicationForm(String internshipId, MultipartFile file) {
        Internship internship = getInternshipByIdInternal(internshipId);

        authenticationService.doIfUserIsStudent((student) -> {
            sendApplicationFormByStudent(internship, student, file);
        }).doIfUserIsFirm((firm) -> {
            sendApplicationFormByFirm(internship, firm, file);
        }).doIfUserIsInternshipCoordinator((user) -> {
            throw new BusinessException(ErrorCode.Forbidden, "Internship coordinator cannot send an application form.");
        }).doIfUserIsDepartmentSecretary((user) -> {
            throw new BusinessException(ErrorCode.Forbidden, "Department secretary cannot send  an application form.");
        });

        internshipRepository.save(internship);
    }

    private void sendApplicationFormByStudent(Internship internship, Student student, MultipartFile file) {
        // Ensure that the internship is in a state in which an application form can be set by the student
        boolean receivedApprovalForLetter = internship.getStatus() == InternshipStatus.FirmAcceptedApplicationLetter;
        boolean coordinatorRequestedChangeInForm = internship.getStatus() == InternshipStatus.CoordinatorRejectedApplicationForm;
        if (!(receivedApprovalForLetter || coordinatorRequestedChangeInForm)) {
            throw new BusinessException(ErrorCode.Forbidden,
                    "A student can only send an application form if her letter was recently accepted, or the" +
                    "coordinator had requested a modification in the previously sent application form");
        }

        // Save the application form document to the database.
        String formId = documentService.createDocument(file, internship.getFirmId()).getDocumentId();

        // Update the internship record.
        internship.setApplicationFormByStudent(documentService.getDocument(formId));
        internship.setStatus(InternshipStatus.StudentSentApplicationForm);

        // Send notification to the firm
        notificationService.createNotificationInternal(internship.getFirmId(), CreateNotificationRequest.builder()
                .content("The student " + student.getStudentNumber() + " has sent an application form.").build());
    }

    private void sendApplicationFormByFirm(Internship internship, Firm firm, MultipartFile file) {
        // Ensure that the student has sent an application form, or the coordinator requested change.
        boolean studentSentForm = internship.getStatus() == InternshipStatus.StudentSentApplicationForm;
        boolean coordinatorRequestedChangeInForm = internship.getStatus() == InternshipStatus.CoordinatorRejectedApplicationForm;
        if (!(studentSentForm || coordinatorRequestedChangeInForm)) {
            throw new BusinessException(ErrorCode.Forbidden,
                    "Before a company can send the filled application form to a student, the student must have sent " +
                    "the form to the firm, or the coordinator must have requested change in a previous form.");
        }

        // Save the application form document to the database.
        String formId = documentService.createDocument(file, internship.getStudentId()).getDocumentId();

        // Update the internship record.
        internship.setApplicationFormByFirm(documentService.getDocument(formId));
        internship.setStatus(InternshipStatus.FirmSentApplicationForm);

        // Send notification to the student
        notificationService.createNotificationInternal(internship.getStudentId(), CreateNotificationRequest.builder()
                .content("The firm " + firm.getFirmName() + " has responded with a filled application form.").build());

        // Send notification to the internship coordinator
        User internshipCoordinator = userService.getUsersByUserRole(UserRole.InternshipCoordinator).get(0);
        notificationService.createNotificationInternal(internshipCoordinator.getId(), CreateNotificationRequest.builder()
                .content("The firm " + firm.getFirmName() + " has filled an application form.").build());
    }

    public void updateApplicationFormAcceptance(String internshipId, UpdateDocumentAcceptanceRequest acceptance) {
        authenticationService.doIfUserIsInternshipCoordinatorOrElseThrow((user) -> {
            Internship internship = getInternshipByIdInternal(internshipId);
            if (internship.getStatus() != InternshipStatus.FirmSentApplicationForm) {
                throw new BusinessException(ErrorCode.Forbidden,
                        "In order for the internship coordinator to be able to accept or reject an application form" +
                        "the firm must have responded with a filled application form.");
            }

            boolean accepted = acceptance.getAcceptance();
            internship.setStatus(accepted ? InternshipStatus.CoordinatorAcceptedApplicationForm :
                                            InternshipStatus.CoordinatorRejectedApplicationForm);
            internshipRepository.save(internship);

            String acceptedStr = accepted ? "accepted" : "rejected";
            // Send notification to the firm.
            notificationService.createNotificationInternal(internship.getFirmId(), CreateNotificationRequest.builder()
                    .content("The internship coordinator has " + acceptedStr + " the application form for student" +
                            internship.getStudent().getStudentNumber() + "\n\n" + acceptance.getFeedback()).build());
            // Send notification to the student.
            notificationService.createNotificationInternal(internship.getStudentId(), CreateNotificationRequest.builder()
                    .content("The internship coordinator has "+ acceptedStr + " the application form for " +
                            internship.getInternshipOffer().getFirmId() + "\n\n" + acceptance.getFeedback()).build());

            return null;
        }, () -> new BusinessException(
                ErrorCode.Forbidden, "Only the internship coordinator can approve or reject an application form."));
    }

    public Document getApplicationFormByStudent(String internshipId) {
        Internship internship = getInternshipByIdInternal(internshipId);
        if (internship.getStatus().getOrder() < InternshipStatus.StudentSentApplicationForm.getOrder()) {
            throw new BusinessException(ErrorCode.ResourceMissing, "Student has not sent any application forms.");
        }
        return internship.getApplicationFormByStudent();
    }

    public Document getApplicationFormByFirm(String internshipId) {
        Internship internship = getInternshipByIdInternal(internshipId);
        if (internship.getStatus().getOrder() < InternshipStatus.FirmSentApplicationForm.getOrder()) {
            throw new BusinessException(ErrorCode.ResourceMissing, "Firm has not sent any application forms.");
        }
        return internship.getApplicationFormByFirm();
    }

    public void setNoInsurance(String internshipId) {
        authenticationService.doIfUserIsInternshipCoordinatorOrElseThrow((coordinator) -> {
            Internship internship = getInternshipByIdInternal(internshipId);
            if (internship.getStatus() != InternshipStatus.CoordinatorAcceptedApplicationForm) {
                throw new BusinessException(ErrorCode.Forbidden,
                        "Unnecessity of insurance cannot be recorded without first approving the application form.");
            }
            internship.setStatus(InternshipStatus.InternshipStarted);
            internshipRepository.save(internship);

            // Send notification to the student.
            notificationService.createNotificationInternal(internship.getStudentId(), CreateNotificationRequest.builder()
                    .content("Your internship has formally started").build());

            return null;
        }, () -> new BusinessException(ErrorCode.Forbidden,
                "Only the internship coordinator is allowed set record that no insurance is required."));
    }

    public void setHandlerToDepartmentSecretary(String internshipId) {
        authenticationService.doIfUserIsInternshipCoordinatorOrElseThrow((coordinator) -> {
            Internship internship = getInternshipByIdInternal(internshipId);
            if (internship.getStatus() != InternshipStatus.CoordinatorAcceptedApplicationForm) {
                throw new BusinessException(ErrorCode.Forbidden,
                        "Handler cannot be set to department secretary without first approving the application form.");
            }
            internship.setStatus(InternshipStatus.CoordinatorSentFormToDepartmentSecretary);
            internshipRepository.save(internship);

            // Send notification to department secretary
            User departmentSecretary = userService.getUsersByUserRole(UserRole.DepartmentSecretary).get(0);
            notificationService.createNotificationInternal(departmentSecretary.getId(), CreateNotificationRequest.builder()
                    .content("The internship coordinator has delegated an application form.").build());

            return null;
        }, () -> new BusinessException(ErrorCode.Forbidden,
                "Only the internship coordinator is allowed to delegate SSI transactions to the Department Secretary"));
    }

    public void setHandlerToDeansOffice(String internshipId) {
        authenticationService.doIfUserIsDepartmentSecretaryOrElseThrow((secretary) -> {
            Internship internship = getInternshipByIdInternal(internshipId);
            if (internship.getStatus() != InternshipStatus.CoordinatorSentFormToDepartmentSecretary) {
                throw new BusinessException(ErrorCode.Forbidden, "Handler cannot be set to dean's office " +
                        "without first receiving permission to process the application form " +
                        "from the internship coordinator");
            }
            internship.setStatus(InternshipStatus.DepartmentSecretaryDelegatedToDeansOffice);
            internshipRepository.save(internship);

            return null;
        }, () -> new BusinessException(ErrorCode.Forbidden,
                "Only the department secretary is allowed to delegate SSI transactions to the Dean's Office."));
    }

    public void sendEmploymentDocument(String internshipId, MultipartFile file) {
        authenticationService.doIfUserIsDepartmentSecretaryOrElseThrow((user) -> {
            Internship internship = getInternshipByIdInternal(internshipId);
            if (internship.getStatus() != InternshipStatus.DepartmentSecretaryDelegatedToDeansOffice) {
                throw new BusinessException(ErrorCode.Forbidden,
                        "The department secretary can upload the employment document only if the transactions" +
                        "were delegated to the Dean's office, and are now complete.");
            }

            // Save the application form document to the database.
            String documentId = documentService.createDocument(file, internship.getStudentId()).getDocumentId();

            // Update the internship record.
            internship.setEmploymentDocument(documentService.getDocument(documentId));
            internship.setStatus(InternshipStatus.InternshipStarted);
            internshipRepository.save(internship);

            // Send notification to the student
            notificationService.createNotificationInternal(internship.getStudentId(), CreateNotificationRequest.builder()
                            .content("The Department Secretary has uploaded your employment document. " +
                                    "Your internship has formally started.").build());

            return null;
        }, () -> new BusinessException(ErrorCode.Forbidden,
                "Only the department secretary can upload employment documents."));
    }

    public Document getEmploymentDocument(String internshipId) {
        Internship internship = getInternshipByIdInternal(internshipId);
        if (internship.getStatus().getOrder() < InternshipStatus.InternshipStarted.getOrder()) {
            throw new BusinessException(ErrorCode.ResourceMissing,
                    "No employment document has been uploaded by the department secretary");
        }
        return internship.getEmploymentDocument();
    }

    public void sendSummerPracticeReport(String internshipId, MultipartFile file) {
        authenticationService.doIfUserIsStudentOrElseThrow((student) -> {
            Internship internship = getInternshipByIdInternal(internshipId);
            boolean internshipStarted = internship.getStatus() == InternshipStatus.InternshipStarted;
            boolean firmRequestedModification = internship.getStatus() == InternshipStatus.FirmRejectedReport;
            if (!(internshipStarted || firmRequestedModification)) {
                throw new BusinessException(ErrorCode.Forbidden,
                        "Cannot send a summer practice report before the internship formally starts," +
                        "or before the firm requests a modification.");
            }

            // Save the summer practice report document to the database.
            String documentId = documentService.createDocument(file, internship.getFirmId()).getDocumentId();

            // Update the internship record.
            internship.setSummerPracticeReport(documentService.getDocument(documentId));
            internship.setStatus(InternshipStatus.StudentSentSummerPracticeReport);
            internshipRepository.save(internship);

            // Send notification to the firm
            notificationService.createNotificationInternal(internship.getFirmId(), CreateNotificationRequest.builder()
                    .content("The student with number " + internship.getStudent().getStudentNumber()
                            + " has uploaded summer practice report").build());

            return null;
        }, () -> new BusinessException(ErrorCode.Forbidden,
                "Only students can upload summer practice reports"));
    }

    public void updateSummerPracticeReportAcceptance(String internshipId, UpdateDocumentAcceptanceRequest acceptance) {
        authenticationService.doIfUserIsFirmOrElseThrow((firm) -> {
            Internship internship = getInternshipByIdInternal(internshipId);
            if (internship.getStatus() != InternshipStatus.StudentSentSummerPracticeReport) {
                throw new BusinessException(ErrorCode.Forbidden,
                        "Cannot approve or reject a report before it is sent by the student.");
            }

            boolean accepted = acceptance.getAcceptance();
            internship.setStatus(accepted ? InternshipStatus.FirmAcceptedReport :
                                            InternshipStatus.FirmRejectedReport);
            internshipRepository.save(internship);

            // Send notification to the student.
            String acceptedStr = accepted ? "accepted" : "rejected";
            notificationService.createNotificationInternal(internship.getStudentId(), CreateNotificationRequest.builder()
                    .content("The firm has "+ acceptedStr + " your summer practice report form for "
                            + "\n\n" + acceptance.getFeedback()).build());

            return null;
        }, () -> new BusinessException(ErrorCode.Forbidden,
                "Only companies can approve or reject summer practice reports"));
    }

    public Document getSummerPracticeReport(String internshipId) {
        Internship internship = getInternshipByIdInternal(internshipId);
        if (internship.getStatus().getOrder() < InternshipStatus.StudentSentSummerPracticeReport.getOrder()) {
            throw new BusinessException(ErrorCode.ResourceMissing,
                    "No summer practice report has been uploaded by the student.");
        }
        return internship.getSummerPracticeReport();
    }

    public void sendCompanyForm(String internshipId, MultipartFile companyForm) {
        authenticationService.doIfUserIsFirmOrElseThrow((firm) -> {
            Internship internship = getInternshipByIdInternal(internshipId);
            boolean firmAcceptedReport = internship.getStatus() == InternshipStatus.FirmAcceptedReport;
            boolean firmRejectedReport = internship.getStatus() == InternshipStatus.FirmRejectedReport;
            if (!(firmAcceptedReport || firmRejectedReport)) {
                throw new BusinessException(ErrorCode.Forbidden,
                        "Cannot send a company form before accepting or rejecting the student's report.");
            }

            // Save the summer practice report document to the database.
            User internshipCoordinator = userService.getUsersByUserRole(UserRole.InternshipCoordinator).get(0);
            String documentId = documentService.createDocument(companyForm, internshipCoordinator.getId()).getDocumentId();

            // Update internship record
            internship.setCompanyForm(documentService.getDocument(documentId));
            internship.setStatus(InternshipStatus.FirmSentCompanyForm);
            internshipRepository.save(internship);

            // Send notification to the internship coordinator
            notificationService.createNotificationInternal(internshipCoordinator.getId(), CreateNotificationRequest.builder()
                    .content("The firm " + firm.getFirmName() + " has sent a company form.").build());

            return null;
        }, () -> new BusinessException(ErrorCode.Forbidden,
                "Only companies can send company forms"));
    }

    public Document getCompanyForm(String internshipId) {
        return authenticationService.doIfUserIsInternshipCoordinatorOrElseThrow((coordinator) -> {
            Internship internship = getInternshipByIdInternal(internshipId);
            if (internship.getStatus().getOrder() < InternshipStatus.FirmSentCompanyForm.getOrder()) {
                throw new BusinessException(ErrorCode.ResourceMissing,
                        "No company form has been uploaded by the student.");
            }
            return internship.getCompanyForm();
        }, () -> new BusinessException(ErrorCode.Forbidden,
                "Only internship coordinator can download company forms"));
    }

    public void sendSurvey(String internshipId, MultipartFile survey) {
        authenticationService.doIfUserIsStudentOrElseThrow((student) -> {
            Internship internship = getInternshipByIdInternal(internshipId);
            if (internship.getStatus() != InternshipStatus.FirmSentCompanyForm) {
                throw new BusinessException(ErrorCode.Forbidden,
                        "Students can send the survey only after the firm has sent the company form");
            }

            // Save the survey to the database.
            User internshipCoordinator = userService.getUsersByUserRole(UserRole.InternshipCoordinator).get(0);
            String documentId = documentService.createDocument(survey, internshipCoordinator.getId()).getDocumentId();

            // Update internship record
            internship.setCompanyForm(documentService.getDocument(documentId));
            internship.setStatus(InternshipStatus.StudentSentSurvey);
            internshipRepository.save(internship);

            // Send notification to the internship coordinator
            notificationService.createNotificationInternal(internshipCoordinator.getId(), CreateNotificationRequest.builder()
                    .content("The student " + student.getStudentNumber() + " has sent a survey.").build());

            return null;
        }, () -> new BusinessException(ErrorCode.Forbidden, "Only students can send surveys"));
    }

    public Document getSurvey(String internshipId) {
        return authenticationService.doIfUserIsInternshipCoordinatorOrElseThrow((coordinator) -> {
            Internship internship = getInternshipByIdInternal(internshipId);
            if (internship.getStatus().getOrder() < InternshipStatus.StudentSentSurvey.getOrder()) {
                throw new BusinessException(ErrorCode.ResourceMissing,
                        "No survey has been uploaded by the student.");
            }
            return internship.getSurvey();
        }, () -> new BusinessException(ErrorCode.Forbidden,
                "Only internship coordinator can download surveys"));
    }
}
