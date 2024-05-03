package edu.iyte.ceng.internship.ims.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.iyte.ceng.internship.ims.entity.Firm;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.entity.UserRole;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.BusinessExceptionType;
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
    public Long createFirm(CreateFirmRequest createRequest) {
        User user = userService.createUser(
            UserRole.Student, 
            createRequest.getEmail(), 
            createRequest.getPassword());

        Firm firm = new Firm(
            user.getUserId(),
            user,
            createRequest.getRegisterDate(),
            createRequest.getFirmName(),
            createRequest.getTypeOfBusiness(),
            createRequest.getBusinessRegistrationNumber(),
            createRequest.getLegalStructure(),
            createRequest.getPhoneNumber(),
            createRequest.getAddress());

        return firmRepository.save(firm).getUser().getUserId();
    }

    public Firm getFirm(Long userId) {
        Firm firm = firmRepository.findFirmById(userId).orElseThrow(
            () -> new BusinessException(BusinessExceptionType.AccountMissing, 
            "Firm with User ID " + userId + " does not exist")
        );

        ensureReadPrivilege(userId);
        return firm;
    }

    @Transactional(rollbackFor = Exception.class)
    public Firm updateFirm(Long userId, UpdateFirmRequest updateRequest) {
        Firm firm = firmRepository.findFirmById(userId).orElseThrow(
            () -> new BusinessException(BusinessExceptionType.AccountMissing, "Firm with user ID " + userId + " does not exist.")
        );

        userService.updateUser(userId, updateRequest.getEmail(), updateRequest.getPassword());

        if (updateRequest.getAddress() != null) firm.setAddress(updateRequest.getAddress());
        if (updateRequest.getFirmName() != null) firm.setFirmName(updateRequest.getFirmName());
        if (updateRequest.getPhoneNumber() != null) firm.setPhoneNumber(updateRequest.getPhoneNumber());

        return firmRepository.save(firm);
    }

    public void ensureReadPrivilege(Long userToBeAccessed) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getUserByEmail(currentEmail);

        if (!currentUser.getUserId().equals(userToBeAccessed)) {
            throw new BusinessException(BusinessExceptionType.Forbidden, "A firm can only read its own account information");
        }
    }
}
