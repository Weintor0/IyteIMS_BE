package edu.iyte.ceng.internship.ims.service;

import edu.iyte.ceng.internship.ims.entity.*;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.model.request.internship.UpdateDocumentAcceptanceRequest;
import edu.iyte.ceng.internship.ims.model.request.notification.CreateNotificationRequest;
import edu.iyte.ceng.internship.ims.model.response.internship.SendApplicationLetterResponse;
import edu.iyte.ceng.internship.ims.repository.FirmRepository;
import edu.iyte.ceng.internship.ims.repository.InternshipOfferRepository;
import edu.iyte.ceng.internship.ims.repository.InternshipRepository;
import edu.iyte.ceng.internship.ims.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final NotificationService notificationService;
    private final DocumentService documentService;
    private final AuthenticationService authenticationService;

    private final StudentRepository studentRepository;
    private final FirmRepository firmRepository;
    private final InternshipOfferRepository internshipOfferRepository;

    public Internship getInternshipById(String id) {
        Internship internship = internshipRepository.findById(id).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing, "Internship with id " + id + " does not exist.")
        );

        User user = authenticationService.getCurrentUser();
        switch (user.getUserRole()) {
            case Student: {
                Student student = studentRepository.findStudentByUser(user).orElseThrow(
                        () -> new IllegalStateException("UserRole implies a Student record, but it does not exist.")
                );
                if (!student.getId().equals(internship.getStudent().getId())) {
                    throw new BusinessException(ErrorCode.Forbidden,
                            "A student cannot access another student's internship information.");
                }
                break;
            }
            case Firm: {
                Firm firm = firmRepository.findFirmByUser(user).orElseThrow(
                        () -> new IllegalStateException("UserRole implies a Firm record, but it does not exist.")
                );
                if (!firm.getId().equals(internship.getInternshipOffer().getFirmId())) {
                    throw new BusinessException(ErrorCode.Forbidden,
                            "A firm cannot access an internship record of another company.");
                }
                break;
            }
            case DepartmentSecretary: {
                InternshipStatus state = internship.getStatus();
                // Either the internship is within SSI transactions or has completed the transactions (an employment document exists)
                // Otherwise it is not department secretary's responsibility.
                if ((state.getOrder() >= InternshipStatus.CoordinatorSentFormToDepartmentSecretary.getOrder() &&
                    state.getOrder() <= InternshipStatus.DepartmentSecretaryUploadedEmploymentDocument.getOrder()) ||
                    internship.getEmploymentDocument() != null) {
                    break;
                }

                throw new BusinessException(ErrorCode.Unauthorized,
                        "The department secretary is only responsible with internships requiring insurance");
            }
            case InternshipCoordinator:
                break;
        }

        return internship;
    }

    public List<Internship> getInternships() {
        User user = authenticationService.getCurrentUser();
        switch (user.getUserRole()) {
            case Student: {
                Student student = studentRepository.findStudentByUser(user).orElseThrow(
                        () -> new IllegalStateException("UserRole implies a Student record, but it does not exist.")
                );
                return internshipRepository.findByStudent(student);
            }

            case Firm: {
                Firm firm = firmRepository.findFirmByUser(user).orElseThrow(
                        () -> new IllegalStateException("UserRole implies a Firm record, but it does not exist.")
                );
                return internshipRepository.findByInternshipOffer_FirmId(firm.getId());
            }

            case InternshipCoordinator: {
                return internshipRepository.findAll();
            }

            case DepartmentSecretary: {
                List<Internship> internships = new ArrayList<>();
                internships.addAll(internshipRepository.findByStatus(
                        InternshipStatus.CoordinatorSentFormToDepartmentSecretary));
                internships.addAll(internshipRepository.findByStatus(
                        InternshipStatus.DepartmentSecretaryUploadedEmploymentDocument));
                internships.addAll(internshipRepository.findByEmploymentDocumentIsNotNull());
                return internships;
            }

            default:
                throw new IllegalStateException("Unknown user attempted to request an internship record.");
        }
    }

    public SendApplicationLetterResponse sendApplicationLetter(String offerId, MultipartFile file) throws IOException {
        User user = authenticationService.getCurrentUser();

        // Ensure that the user is a student
        if (user.getUserRole() != UserRole.Student) {
            throw new BusinessException(ErrorCode.Forbidden, "Only students can send application letters");
        }

        // Get the Student record.
        Student student = studentRepository.findStudentByUser(user).orElseThrow(
                () -> new IllegalStateException("UserRole implies the existence of a Student, but it does not exist.")
        );

        // Get the InternshipOffer record.
        InternshipOffer internshipOffer = internshipOfferRepository.findInternshipOfferById(offerId).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing,
                        "Internship offer with ID " + offerId + " does not exist")
        );

        // Save the application letter document to the database.
        String letterId = documentService.createDocument(file, internshipOffer.getFirmId()).getDocumentId();
        Document letter = documentService.getDocument(letterId);

        // Create and save the internship record in the database.
        Internship internship = internshipRepository.save(
                Internship.builder()
                          .status(InternshipStatus.StudentSentApplicationLetter)
                          .student(student)
                          .internshipOffer(internshipOffer)
                          .applicationLetter(letter)
                          .build()
        );

        // Send notification to the firm
        notificationService.createNotification(internshipOffer.getFirmId(),
                CreateNotificationRequest
                        .builder()
                        .content("The student " + student.getStudentNumber() + " has sent an application letter.")
                        .build());

        // Return the Internship ID.
        return new SendApplicationLetterResponse(internship.getId());
    }

    public void updateApplicationLetterAcceptance(String internshipId, UpdateDocumentAcceptanceRequest acceptance) {
        User user = authenticationService.getCurrentUser();

        // Ensure that the user is a Firm.
        if (user.getUserRole() != UserRole.Firm) {
            throw new BusinessException(ErrorCode.Forbidden, "Only firms can evaluate application letters");
        }

        // Get the Firm record.
        Firm firm = firmRepository.findFirmByUser(user).orElseThrow(
                () -> new IllegalStateException("UserRole implies the existence of a Firm, but it does not exist.")
        );

        // Get the internship record.
        Internship internship = internshipRepository.findById(internshipId).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing,
                        "Internship with ID " + internshipId + " does not exist.")
        );

        // Ensure that the letter was not evaluated before. It is not permitted to change the approval state twice.
        if (internship.getStatus() != InternshipStatus.StudentSentApplicationLetter) {
            throw new BusinessException(ErrorCode.Forbidden, "The application letter has already been evaluated");
        }

        // Set the acceptance state
        boolean accepted = acceptance.getAcceptance();
        if (accepted) {
            internship.setStatus(InternshipStatus.FirmAcceptedApplicationLetter);
        } else {
            internship.setStatus(InternshipStatus.FirmRejectedApplicationLetter);
        }

        // Send notification to the student.
        notificationService.createNotification(internship.getStudent().getUser().getId(),
                CreateNotificationRequest
                        .builder()
                        .content(firm.getFirmName() + " has " + (accepted ? "accepted" : "rejected")
                                + " your application letter.")
                        .build());
    }

    public void sendApplicationForm(String internshipId, MultipartFile file) throws IOException {
        Internship internship = internshipRepository.findById(internshipId).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing,
                        "Internship with ID " + internshipId + " does not exist.")
        );

        User user = authenticationService.getCurrentUser();
        switch (user.getUserRole()) {
            case Student: {
                Student student = studentRepository.findStudentByUser(user).orElseThrow(
                        () -> new IllegalStateException(
                                "UserRole implies the existence of a Student, but it does not exist.")
                );
                sendApplicationFormByStudent(internship, student, file);
                break;
            }
            case Firm: {
                Firm firm = firmRepository.findFirmByUser(user).orElseThrow(
                        () -> new IllegalStateException(
                                "UserRole implies the existence of a Firm, but it does not exist.")
                );
                sendApplicationFormByFirm(internship, firm, file);
                break;
            }
            default:
                throw new BusinessException(ErrorCode.Forbidden,
                        "Only students and firms can send application forms");
        }

        internshipRepository.save(internship);
    }

    private void sendApplicationFormByStudent(Internship internship, Student student, MultipartFile file) throws IOException {
        // Ensure that the application letter was accepted by the firm.
        if (internship.getStatus() == InternshipStatus.FirmRejectedApplicationLetter) {
            throw new BusinessException(ErrorCode.Forbidden,
                    "Students cannot send application forms to firms that have rejected them.");
        }

        // Ensure that the internship is in a state in which an application form can be set by the student
        boolean receivedApprovalForLetter = internship.getStatus() == InternshipStatus.FirmAcceptedApplicationLetter;
        boolean coordinatorRequestedChangeInForm = internship.getStatus() == InternshipStatus.CoordinatorRejectedApplicationForm;
        if (!(receivedApprovalForLetter || coordinatorRequestedChangeInForm)) {
            throw new BusinessException(ErrorCode.Forbidden,
                    "A student can only send an application form if her letter was recently accepted, or the" +
                            "coordinator had requested a modification in the previously sent application form");
        }

        // Save the application form document to the database.
        String formId = documentService.createDocument(file, internship.getInternshipOffer().getFirmId()).getDocumentId();
        Document form = documentService.getDocument(formId);

        // Send notification to the firm
        notificationService.createNotification(internship.getInternshipOffer().getFirmId(),
                CreateNotificationRequest
                        .builder()
                        .content("The student " + student.getStudentNumber() + " has sent an application form.")
                        .build());

        // Update the internship record.
        internship.setApplicationFormByStudent(form);
        internship.setStatus(InternshipStatus.StudentSentApplicationForm);
    }

    private void sendApplicationFormByFirm(Internship internship, Firm firm, MultipartFile file) throws IOException {
        // Ensure that the student has sent an application form, or the coordinator requested change.
        boolean studentSentForm = internship.getStatus() == InternshipStatus.StudentSentApplicationForm;
        boolean coordinatorRequestedChangeInForm = internship.getStatus() == InternshipStatus.CoordinatorRejectedApplicationForm;
        if (!(studentSentForm || coordinatorRequestedChangeInForm)) {
            throw new BusinessException(ErrorCode.Forbidden,
                    "Before a company can send the filled application form to a student, " +
                            "the student must have sent the form to the firm, or the coordinator must have requested change" +
                            "in a previous form.");

        }

        // Save the application form document to the database.
        String formId = documentService.createDocument(file, internship.getStudent().getUser().getId()).getDocumentId();
        Document form = documentService.getDocument(formId);

        // Send notification to the student
        notificationService.createNotification(internship.getStudent().getUser().getId(),
                CreateNotificationRequest
                        .builder()
                        .content("The firm " + firm.getFirmName() + " has responded with a filled application form.")
                        .build());

        // Update the internship record.
        internship.setApplicationFormByFirm(form);
        internship.setStatus(InternshipStatus.FirmSentApplicationForm);
    }

    public void updateApplicationFormAcceptance(String internshipId, UpdateDocumentAcceptanceRequest acceptance) {
        Internship internship = internshipRepository.findById(internshipId).orElseThrow(
                () -> new BusinessException(ErrorCode.ResourceMissing,
                        "Internship with ID " + internshipId + " does not exist.")
        );

        User user = authenticationService.getCurrentUser();
        if (user.getUserRole() != UserRole.InternshipCoordinator) {
            throw new BusinessException(ErrorCode.Forbidden,
                    "Only the internship coordinator can approve or reject an application form.");
        }

        if (internship.getStatus() != InternshipStatus.FirmSentApplicationForm) {
            throw new BusinessException(ErrorCode.Forbidden,
                    "In order for the internship coordinator to be able to accept or reject an application form" +
                            "the firm must have been responded with a filled application form.");
        }

        // Set the acceptance state
        boolean accepted = acceptance.getAcceptance();
        if (accepted) {
            internship.setStatus(InternshipStatus.CoordinatorAcceptedApplicationForm);
        } else {
            internship.setStatus(InternshipStatus.CoordinatorRejectedApplicationForm);
        }

        String acceptedRejectMessage = accepted ? "accepted" : "rejected";
        String feedback = acceptance.getFeedback() == null ? "" : "Feedback: " + acceptance.getFeedback();

        // Send notification to the firm.
        notificationService.createNotification(internship.getInternshipOffer().getFirmId(),
                CreateNotificationRequest
                        .builder()
                        .content("The internship coordinator has " + acceptedRejectMessage
                                + " the application form for student" +
                                internship.getStudent().getStudentNumber() + "\n" + feedback)
                        .build());

        // Send notification to the student.
        notificationService.createNotification(internship.getStudent().getUser().getId(),
                CreateNotificationRequest
                        .builder()
                        .content("The internship coordinator has "+ acceptedRejectMessage
                                + " the application form for " +
                                internship.getInternshipOffer().getFirmId() + "\n" + feedback)
                        .build());
    }
}
