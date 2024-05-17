package edu.iyte.ceng.internship.ims.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "UC_EMAIL", columnNames = {"email"})
})
@AllArgsConstructor
@NoArgsConstructor
@AssociatedWithEntity(entityName = User.entityName)
public class User extends BaseEntity {
    public static final String entityName = "User";

    @Email
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;
}
