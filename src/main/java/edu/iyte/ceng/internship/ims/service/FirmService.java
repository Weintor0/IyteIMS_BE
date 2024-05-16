package edu.iyte.ceng.internship.ims.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.iyte.ceng.internship.ims.entity.Firm;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.model.request.CreateFirmRequest;
import edu.iyte.ceng.internship.ims.model.request.UpdateFirmRequest;
import edu.iyte.ceng.internship.ims.repository.FirmRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FirmService {
    private FirmRepository firmRepository;
    private UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public String createFirm(CreateFirmRequest createRequest) {
        User user = userService.createUser(
            createRequest.getEmail(), 
            createRequest.getPassword());

        Firm firm = Firm.builder()
                //.id(user.getId())
                .user(user)
                .registerDate(createRequest.getRegisterDate())
                .firmName(createRequest.getFirmName())
                .typeOfBusiness(createRequest.getTypeOfBusiness())
                .businessRegistrationNumber(createRequest.getBusinessRegistrationNumber())
                .legalStructure(createRequest.getLegalStructure())
                .phoneNumber(createRequest.getPhoneNumber())
                .address(createRequest.getAddress())
                .build();

        return firmRepository.save(firm).getUser().getId();
    }

    public Firm getFirm(String userId) {
        Firm firm = firmRepository.findFirmById(userId).orElseThrow(
            () -> new BusinessException(ErrorCode.AccountMissing,
            "Firm with User ID " + userId + " does not exist")
        );

        ensureReadPrivilege(userId);
        return firm;
    }

    @Transactional(rollbackFor = Exception.class)
    public Firm updateFirm(String userId, UpdateFirmRequest updateRequest) {
        Firm firm = firmRepository.findFirmById(userId).orElseThrow(
            () -> new BusinessException(ErrorCode.AccountMissing, "Firm with user ID " + userId + " does not exist.")
        );

        userService.updateUser(userId, updateRequest.getEmail(), updateRequest.getPassword());

        if (updateRequest.getAddress() != null) firm.setAddress(updateRequest.getAddress());
        if (updateRequest.getFirmName() != null) firm.setFirmName(updateRequest.getFirmName());
        if (updateRequest.getPhoneNumber() != null) firm.setPhoneNumber(updateRequest.getPhoneNumber());

        return firmRepository.save(firm);
    }

    public void ensureReadPrivilege(String userToBeAccessed) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getUserByEmail(currentEmail);

        if (!currentUser.getId().equals(userToBeAccessed)) {
            throw new BusinessException(ErrorCode.Forbidden, "A firm can only read its own account information");
        }
    }
}
