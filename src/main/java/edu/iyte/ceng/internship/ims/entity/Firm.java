package edu.iyte.ceng.internship.ims.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "firm", uniqueConstraints = {
    @UniqueConstraint(name = "UC_FIRM_NAME", columnNames = { "firm_name" }),
    @UniqueConstraint(name = "UC_BUSINESS_REGISTRATION_NUMBER", columnNames = { "business_registration_number" })
})
@AllArgsConstructor
@NoArgsConstructor
@AssociatedWithEntity(entityName = Firm.entityName)
public class Firm extends BaseEntity {
    public static final String entityName = "Firm";

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "user_id")
    @NotNull
    private User user;

    @NotNull
    @Column(name = "register_date")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date registerDate;

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
}
