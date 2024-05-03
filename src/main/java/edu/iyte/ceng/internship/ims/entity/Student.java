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
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "student", uniqueConstraints = {
    @UniqueConstraint(name = "UC_STUDENT_NUMBER", columnNames = {"student_number"})
})
@RequiredArgsConstructor
@NoArgsConstructor
@AssociatedWithEntity(entityName = Student.entityName)
public class Student {
    public static final String entityName = "Student";

    @Id
    @NonNull
    @Column(name = "user_id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "user_id")
    @NonNull
    private User user;

    @NonNull
    @NotBlank
    @Column(name = "student_number")
    private String studentNumber;

    @NonNull
    @Past
    @Column(name = "birth_date")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date birthDate;

    @NonNull
    @NotBlank
    @Column(name = "name")
    private String name;

    @NonNull
    @NotBlank
    @Column(name = "surname")
    private String surname;
}
