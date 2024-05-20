package edu.iyte.ceng.internship.ims.entity;

import java.time.ZonedDateTime;
import java.util.Date;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@Entity
@Builder
@Table(name = "firm", uniqueConstraints = {
    @UniqueConstraint(name = "UC_FIRM_NAME", columnNames = { "firm_name" }),
    @UniqueConstraint(name = "UC_BUSINESS_REGISTRATION_NUMBER", columnNames = { "business_registration_number" })
})
@AllArgsConstructor
@NoArgsConstructor
@AssociatedWithEntity(entityName = Firm.entityName)
public class Firm extends BaseEntity {
    public static final String entityName = "Firm";

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    @Setter(AccessLevel.NONE)
    private ZonedDateTime registerDate;

    @NotNull
    @NotBlank
    @Column(name = "firm_name")
    private String firmName;

    @NotNull
    @NotBlank
    @Column(name = "type_of_business")
    private String typeOfBusiness;

    @NotNull
    @NotBlank
    @Column(name = "business_registration_number")
    private String businessRegistrationNumber;

    @NotNull
    @NotBlank
    @Column(name = "legal_structure")
    private String legalStructure;

    @NotNull
    @NotBlank
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull
    @NotBlank
    @Column(name = "address")
    private String address;

    public String getUserId() {
        return user.getId();
    }
}
