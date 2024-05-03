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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "firm", uniqueConstraints = {
    @UniqueConstraint(name = "UC_FIRM_NAME", columnNames = { "firm_name" }),
    @UniqueConstraint(name = "UC_BUSINESS_REGISTRATION_NUMBER", columnNames = { "business_registration_number" })
})
@RequiredArgsConstructor
@NoArgsConstructor
@AssociatedWithEntity(entityName = Firm.entityName)
public class Firm {
    public static final String entityName = "Firm";

    @Id
    @NonNull
    @Column(name = "user_id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "user_id")
    @NonNull
    private User user;

    @NonNull
    @Column(name = "register_date")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date registerDate;

    @NonNull
    @NotBlank
    @Column(name = "firm_name")
    private String firmName;

    @NonNull
    @NotBlank
    @Column(name = "type_of_business")
    private String typeOfBusiness;

    @NonNull
    @NotBlank
    @Column(name = "business_registration_number")
    private String businessRegistrationNumber;

    @NonNull
    @NotBlank
    @Column(name = "legal_structure")
    private String legalStructure;

    @NonNull
    @NotBlank
    @Column(name = "phone_number")
    private String phoneNumber;

    @NonNull
    @NotBlank
    @Column(name = "address")
    private String address;
}
