-- Internship Coordinator
INSERT INTO users (id, email, password, user_role, created_date, modified_date)
VALUES ('00000000-0000-0000-0000-000000000000',
        'internshipcoordinator@iyte.edu.tr',
        '$2a$10$32BqbxYnaFY7R9yBcO/pOO6oDoI26r/4mztyjlGfZb3eNnBtuqSpi',
        'InternshipCoordinator',
        now(),
        now()); -- 123456

-- Department Secretary
INSERT INTO users (id, email, password, user_role, created_date, modified_date)
VALUES ('00000000-0000-0000-0000-000000000001',
        'departmentsecretary@iyte.edu.tr',
        '$2a$10$32BqbxYnaFY7R9yBcO/pOO6oDoI26r/4mztyjlGfZb3eNnBtuqSpi',
        'DepartmentSecretary',
        now(),
        now()); -- 123456
