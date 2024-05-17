package edu.iyte.ceng.internship.ims.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@Builder
@Table(name = "student", uniqueConstraints = {
    @UniqueConstraint(name = "UC_STUDENT_NUMBER", columnNames = {"student_number"})
})
@AllArgsConstructor
@NoArgsConstructor
@AssociatedWithEntity(entityName = Student.entityName)
public class Student extends BaseEntity {
    public static final String entityName = "Student";

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @NotNull
    @NotBlank
    @Column(name = "student_number")
    private String studentNumber;

    @NotNull
    @Past
    @Column(name = "birth_date")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date birthDate;

    @NotNull
    @NotBlank
    @Column(name = "name")
    private String name;

    @NotNull
    @NotBlank
    @Column(name = "surname")
    private String surname;
}
