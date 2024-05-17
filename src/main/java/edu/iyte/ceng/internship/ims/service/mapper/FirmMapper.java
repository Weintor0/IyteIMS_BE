package edu.iyte.ceng.internship.ims.service.mapper;

import edu.iyte.ceng.internship.ims.entity.Firm;
import edu.iyte.ceng.internship.ims.entity.Student;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.model.request.users.FirmRegisterRequest;
import edu.iyte.ceng.internship.ims.model.request.users.StudentRegisterRequest;
import edu.iyte.ceng.internship.ims.model.response.users.FirmResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FirmMapper {
    private final UserMapper userMapper;

    @Autowired
    public FirmMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public FirmResponse fromEntity(Firm firm) {
        FirmResponse response = FirmResponse.builder()
                .user(userMapper.fromUser(firm.getUser()))
                .registerDate(firm.getRegisterDate())
                .firmName(firm.getFirmName())
                .typeOfBusiness(firm.getTypeOfBusiness())
                .businessRegistrationNumber(firm.getBusinessRegistrationNumber())
                .legalStructure(firm.getLegalStructure())
                .phoneNumber(firm.getPhoneNumber())
                .address(firm.getAddress())
                .build();
        return response;
    }

    public Firm fromRequest(FirmRegisterRequest firmRegisterRequest, User user) {
        Firm firm = Firm.builder()
                .user(user)
                .registerDate(firmRegisterRequest.getRegisterDate())
                .firmName(firmRegisterRequest.getFirmName())
                .typeOfBusiness(firmRegisterRequest.getTypeOfBusiness())
                .businessRegistrationNumber(firmRegisterRequest.getBusinessRegistrationNumber())
                .legalStructure(firmRegisterRequest.getLegalStructure())
                .phoneNumber(firmRegisterRequest.getPhoneNumber())
                .address(firmRegisterRequest.getPhoneNumber())
                .build();
        return firm;
    }
}
