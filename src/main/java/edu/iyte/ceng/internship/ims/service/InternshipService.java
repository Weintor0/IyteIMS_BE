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
                TransactionsState state = internship.getCurrentTransactionsState();
                if (state != TransactionsState.DepartmentSecretary &&
                    state != TransactionsState.DeansOffice &&
                    state != TransactionsState.Completed) {
                    throw new BusinessException(ErrorCode.Unauthorized,
                            "The department secretary is only responsible with internships requiring insurance");
                }
                break;
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

                internships.addAll(internshipRepository.findByCurrentTransactionsState(
                        TransactionsState.DeansOffice));
                internships.addAll(internshipRepository.findByCurrentTransactionsState(
                        TransactionsState.DepartmentSecretary));
                internships.addAll(internshipRepository.findByCurrentTransactionsState(
                        TransactionsState.Completed));

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
                          .student(student)
                          .internshipOffer(internshipOffer)
                          .applicationLetter(letter)
                          .applicationFormAcceptanceStatus(AcceptanceStatus.NotEvaluated)
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

        // Set the acceptance state
        boolean accepted = acceptance.getAcceptance();
        internship.setApplicationLetterAcceptanceStatus(
                accepted ? AcceptanceStatus.Accepted : AcceptanceStatus.Rejected
        );

        // Send notification to the student.
        notificationService.createNotification(internship.getStudent().getUser().getId(),
                CreateNotificationRequest
                        .builder()
                        .content(firm.getFirmName() + " has " + (accepted ? "accepted" : "rejected")
                                + " your application letter.")
                        .build());
    }
}
