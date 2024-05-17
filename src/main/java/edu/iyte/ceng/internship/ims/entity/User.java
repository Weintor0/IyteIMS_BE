package edu.iyte.ceng.internship.ims.entity;

import jakarta.persistence.*;
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
    private String password;

    @Column(name = "user_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
}
