package edu.iyte.ceng.internship.ims.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InternshipStatus {
    StudentSentApplicationLetter(1),
    FirmRejectedApplicationLetter(2),
    FirmAcceptedApplicationLetter(3),
    StudentSentApplicationForm(4),
    FirmSentApplicationForm(5),
    CoordinatorAcceptedApplicationForm(6),
    CoordinatorRejectedApplicationForm(7),
    CoordinatorSentFormToDepartmentSecretary(8),
    DepartmentSecretaryDelegatedToDeansOffice(9),
    //DepartmentSecretaryUploadedEmploymentDocument(11),
    InternshipStarted(12),
    StudentSentSummerPracticeReport(13),
    FirmAcceptedReport(14),
    FirmRejectedReport(15),
    FirmSentCompanyForm(16),
    StudentSentSurvey(17);

    private final int order;
}
