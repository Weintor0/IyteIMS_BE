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
import jakarta.validation.constraints.Past;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "student", uniqueConstraints = {
    @UniqueConstraint(name = "UC_STUDENT_NUMBER", columnNames = {"student_number"})
})
@AllArgsConstructor
@NoArgsConstructor
@AssociatedWithEntity(entityName = Student.entityName)
public class Student extends BaseEntity {
    public static final String entityName = "Student";

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull
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
