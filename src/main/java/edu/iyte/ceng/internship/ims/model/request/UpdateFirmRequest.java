package edu.iyte.ceng.internship.ims.model.request;

import edu.iyte.ceng.internship.ims.entity.AssociatedWithEntity;
import edu.iyte.ceng.internship.ims.entity.Firm;
import lombok.Data;

@Data
@AssociatedWithEntity(entityName = Firm.entityName)
public class UpdateFirmRequest {
    private String firmName;
    private String phoneNumber;
    private String address;
    private String email;
    private String password;
}
