package edu.iyte.ceng.internship.ims.service;

import edu.iyte.ceng.internship.ims.entity.Firm;
import edu.iyte.ceng.internship.ims.entity.User;
import edu.iyte.ceng.internship.ims.exception.BusinessException;
import edu.iyte.ceng.internship.ims.exception.ErrorCode;
import edu.iyte.ceng.internship.ims.model.request.users.UpdateFirmRequest;
import edu.iyte.ceng.internship.ims.model.response.users.FirmResponse;
import edu.iyte.ceng.internship.ims.repository.FirmRepository;
import edu.iyte.ceng.internship.ims.service.mapper.FirmMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class FirmService {
    private FirmRepository firmRepository;
    private UserService userService;
    private FirmMapper firmMapper;
    private AuthenticationService authenticationService;


    public FirmResponse getFirm(String userId) {
        User user = userService.getUserById(userId);
        Firm firm = firmRepository.findFirmByUser(user).orElseThrow(
                () -> new BusinessException(ErrorCode.AccountMissing,
                        "Firm with User ID " + userId + " does not exist")
        );
        ensureReadPrivilege(userId);
        return firmMapper.fromEntity(firm);
    }

    @Transactional(rollbackFor = Throwable.class)
    public FirmResponse updateFirm(String userId, UpdateFirmRequest updateRequest) {
        User user = userService.getUserById(userId);
        ensureReadPrivilege(userId);
        Firm firm = firmRepository.findFirmByUser(user).orElseThrow(
                () -> new BusinessException(ErrorCode.AccountMissing, "Firm with user ID " + userId + " does not exist.")
        );

        userService.updateUser(userId, updateRequest.getEmail(), updateRequest.getPassword());

        if (updateRequest.getAddress() != null) firm.setAddress(updateRequest.getAddress());
        if (updateRequest.getFirmName() != null) firm.setFirmName(updateRequest.getFirmName());
        if (updateRequest.getPhoneNumber() != null) firm.setPhoneNumber(updateRequest.getPhoneNumber());

        Firm savedFirm = firmRepository.save(firm);
        return firmMapper.fromEntity(savedFirm);
    }

    private void ensureReadPrivilege(String userId) {
        User currentUser = authenticationService.getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.Forbidden, "A firm can only read its own account information");
        }
    }
}
