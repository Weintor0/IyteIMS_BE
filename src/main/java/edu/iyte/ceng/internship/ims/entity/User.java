package edu.iyte.ceng.internship.ims.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.NonNull;

@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "UC_EMAIL", columnNames = {"email"})
})
@NoArgsConstructor
@AssociatedWithEntity(entityName = User.entityName)
public class User extends BaseEntity {
    public static final String entityName = "User";

    @Email
    @NotBlank(message = "Email cannot be blank")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;
}
