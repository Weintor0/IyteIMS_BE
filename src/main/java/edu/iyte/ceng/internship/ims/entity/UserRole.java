package edu.iyte.ceng.internship.ims.entity;

import lombok.Getter;

public enum UserRole {
    Student("ROLE_STUDENT"),
    Firm("ROLE_FIRM"),
    InternshipCoordinator("ROLE_INTERNSHIP_COORDINATOR"),
    DepartmentSecretary("ROLE_DEPARTMENT_SECRETARY");

    @Getter
    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }
}
