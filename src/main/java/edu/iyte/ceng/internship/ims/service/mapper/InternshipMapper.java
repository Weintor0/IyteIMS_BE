package edu.iyte.ceng.internship.ims.service.mapper;

import edu.iyte.ceng.internship.ims.entity.Internship;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.model.response.internship.InternshipResponse;
import edu.iyte.ceng.internship.ims.model.response.users.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class InternshipMapper {
    public InternshipResponse fromInternship(Internship internship) {
        return InternshipResponse.builder()
                .internshipId(internship.getId())
                .studentId(internship.getStudentId())
                .internshipOfferId(internship.getInternshipOffer().getId())
                .internshipStatus(internship.getStatus())
                .build();
    }
}
