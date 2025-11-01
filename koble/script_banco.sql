CREATE DATABASE projeto;
USE projeto;

CREATE TABLE teacher_staff (
    teacher_staff_id BIGINT AUTO_INCREMENT,
    siape VARCHAR(60) NOT NULL UNIQUE,
    email VARCHAR(60) NOT NULL UNIQUE, 
    password VARCHAR(60) NOT NULL, 
    name VARCHAR(60) NOT NULL,
    phone VARCHAR(60),
    PRIMARY KEY (teacher_staff_id)
);

CREATE TABLE student (
    student_id BIGINT AUTO_INCREMENT,
    enrollment VARCHAR(60) NOT NULL UNIQUE, 
    name VARCHAR(60) NOT NULL,
    email VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    phone VARCHAR(60),
    PRIMARY KEY (student_id)
);

CREATE TABLE company (
    company_id BIGINT AUTO_INCREMENT,
    cnpj VARCHAR(60) NOT NULL UNIQUE,
    name VARCHAR(60) NOT NULL,
    email VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    phone VARCHAR(60),
    PRIMARY KEY (company_id)
);

CREATE TABLE external_user (
    external_user_id BIGINT AUTO_INCREMENT,
    name VARCHAR(60) NOT NULL,
    email VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    phone VARCHAR(60),
    PRIMARY KEY (external_user_id)
);

CREATE TABLE idea (
    idea_id BIGINT AUTO_INCREMENT,
    proposer VARCHAR(60) NOT NULL,
    target_audience VARCHAR(60) NOT NULL,
    justification TEXT NOT NULL, 
    title VARCHAR(60) NOT NULL,
    objective TEXT NOT NULL,
    subtitle VARCHAR(60) NOT NULL,
    area VARCHAR(60) NOT NULL,
    description TEXT NOT NULL, 
    type ENUM('Teaching','Research','Extension') NOT NULL, 
    student_id BIGINT,
    teacher_id BIGINT,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE SET NULL,
    FOREIGN KEY (teacher_id) REFERENCES teacher_staff(teacher_staff_id) ON DELETE SET NULL,
    PRIMARY KEY (idea_id)
);

	CREATE TABLE project (
    project_id BIGINT AUTO_INCREMENT,
    title VARCHAR(60) NOT NULL,
    subtitle VARCHAR(60) NOT NULL,
    coordinator VARCHAR(63) NOT NULL,
    description TEXT NOT NULL,
    timeline VARCHAR(255) NOT NULL,
    external_link VARCHAR(255), 
    duration VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    complementary_hours VARCHAR(255),
    scholarship_available BOOLEAN NOT NULL,
    scholarship_type VARCHAR(255),
    salary DECIMAL(10, 2), 
    requirements TEXT NOT NULL, 
    scholarship_quantity INT,
    
    project_type ENUM('Teaching','Research','Extension') NOT NULL,
    
    ALTER TABLE project
	MODIFY COLUMN project_type ENUM('Research', 'Extension', 'Educational', 'Teaching');
    

    PRIMARY KEY (project_id)
);
	

CREATE TABLE fellow (
    cpf VARCHAR(20) NOT NULL UNIQUE,
    lattes_curriculum VARCHAR(255),
    birth_date DATE NOT NULL,
    project_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, student_id)
);

CREATE TABLE participant (
    participant_id BIGINT AUTO_INCREMENT, 
    cpf VARCHAR(20) UNIQUE,
    name VARCHAR(63) NOT NULL,
    contact_number VARCHAR(30),
    lattes_curriculum VARCHAR(255),
    birth_date DATE, 
    project_id BIGINT NOT NULL, 
    
    PRIMARY KEY (participant_id), 
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE
);
    

CREATE TABLE teacher_idea (
    idea_id BIGINT,
    teacher_id BIGINT,
    FOREIGN KEY (teacher_id) REFERENCES teacher_staff(teacher_staff_id) ON DELETE CASCADE,
    FOREIGN KEY (idea_id) REFERENCES idea(idea_id) ON DELETE CASCADE,
    PRIMARY KEY (idea_id, teacher_id)
);

CREATE TABLE student_idea (
    idea_id BIGINT,
    student_id BIGINT,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (idea_id) REFERENCES idea(idea_id) ON DELETE CASCADE,
    PRIMARY KEY (idea_id, student_id)
);

CREATE TABLE company_project (
    project_id BIGINT,
    company_id BIGINT,
    FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, company_id)
);

	CREATE TABLE teacher_project (
    project_id BIGINT,
    teacher_id BIGINT,
    FOREIGN KEY (teacher_id) REFERENCES teacher_staff(teacher_staff_id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, teacher_id)
);

CREATE TABLE research (
    research_id BIGINT AUTO_INCREMENT,
    project_id BIGINT NOT NULL, 
    research_objective VARCHAR(300) NOT NULL,
    research_justification VARCHAR(600) NOT NULL,
    research_discipline VARCHAR(200) NOT NULL,
    PRIMARY KEY (research_id),
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE
);


CREATE TABLE educational (
    educational_id BIGINT,
    project_id BIGINT NOT NULL,
    educational_slots INTEGER NOT NULL,
    educational_justification VARCHAR(600) NOT NULL,
    educational_course VARCHAR(200) NOT NULL,
    PRIMARY KEY (educational_id),
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE
);

---

CREATE TABLE extension (
    extension_id BIGINT,
    project_id BIGINT NOT NULL,
    extension_target_audience VARCHAR(200) NOT NULL,
    extension_slots INTEGER NOT NULL,
    extension_selection_process VARCHAR(600) NOT NULL,
    PRIMARY KEY (extension_id),
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE
);
	

CREATE INDEX idx_idea_student ON idea(student_id);
CREATE INDEX idx_idea_teacher ON idea(teacher_id);
CREATE INDEX idx_participant_project ON participant(project_id);
CREATE INDEX idx_participant_student ON participant(student_id);
CREATE INDEX idx_participant_teacher ON participant(teacher_id);
CREATE INDEX idx_participant_external ON participant(external_user_id);
CREATE INDEX idx_fellow_project ON fellow(project_id);
CREATE INDEX idx_fellow_student ON fellow(student_id);
CREATE INDEX idx_project_type ON project(project_type);
CREATE INDEX idx_teacher_idea_teacher ON teacher_idea(teacher_id);
CREATE INDEX idx_student_idea_student ON student_idea(student_id);
CREATE INDEX idx_company_project_company ON company_project(company_id);
CREATE INDEX idx_teacher_project_teacher ON teacher_project(teacher_id);


USE projeto;
SELECT * FROM project;