CREATE DATABASE IF NOT EXISTS koble;
USE koble;

create table teacher_staff(
    teacher_staff_id BIGINT AUTO_INCREMENT,
    siape varchar(60) NOT NULL UNIQUE,
    email varchar(60) NOT NULL UNIQUE, 
    password varchar(60) NOT NULL, 
    name varchar(60) NOT NULL,
    phone varchar(60),
    PRIMARY KEY (teacher_staff_id)
);

create table student(
    student_id BIGINT AUTO_INCREMENT,
    enrollment varchar(60) NOT NULL UNIQUE,
    name varchar(60) NOT NULL,
    email varchar(60) NOT NULL UNIQUE,
    password varchar(60) NOT NULL,
    phone varchar(60),
    PRIMARY KEY (student_id)
);

create table company(
    company_id BIGINT AUTO_INCREMENT,
    cnpj varchar(60) NOT NULL UNIQUE,
    name varchar(60) NOT NULL,
    email varchar(60) NOT NULL UNIQUE,
    password varchar(60) NOT NULL,
    phone varchar(60),
    PRIMARY KEY (company_id)
);

-- Project inheritance using Single Table approach
-- All project-specific attributes are now in the main project table

create table external_user(
    external_user_id BIGINT AUTO_INCREMENT,
    name varchar(60) NOT NULL,
    email varchar(60) NOT NULL UNIQUE,
    password varchar(60) NOT NULL,
    phone varchar(60),
    PRIMARY KEY (external_user_id)
);

create table idea(
    idea_id BIGINT AUTO_INCREMENT,
    proposer varchar(60) NOT NULL,
    target_audience varchar(60) NOT NULL,
    justification varchar(60) NOT NULL,
    title varchar(60) NOT NULL,
    objective varchar(60) NOT NULL,
    subtitle varchar(60) NOT NULL,
    area varchar(60) NOT NULL,
    description varchar(60) NOT NULL,
    type varchar(60) NOT NULL,
    student_id BIGINT,
    teacher_id BIGINT,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE SET NULL,
    FOREIGN KEY (teacher_id) REFERENCES teacher_staff(teacher_staff_id) ON DELETE SET NULL,
    PRIMARY KEY (idea_id),
    CONSTRAINT chk_valid_type CHECK (type IN('Teaching','Research','Extension'))
);

create table project(
    project_id bigint auto_increment,
    timeline varchar(255) NOT NULL,
    external_link varchar(255) NOT NULL,
    duration varchar(255) NOT NULL,
    image varchar(255),
    complementary_hours varchar(255) NOT NULL,
    scholarship_available boolean NOT NULL,
    scholarship_type varchar(255),
    salary decimal,
    requirements varchar(255) NOT NULL,
    scholarship_quantity int,
    title varchar(60) NOT NULL,
    subtitle varchar(60) NOT NULL,
    coordinator varchar(63) NOT NULL,
    description varchar(255) NOT NULL,
    project_type varchar(20) NOT NULL DEFAULT 'Research',
    
    -- Research project specific fields
    research_objective varchar(2000),
    research_justification varchar(2000),
    research_discipline varchar(60),
    
    -- Extension project specific fields
    extension_target_audience varchar(255),
    extension_slots int,
    extension_selection_process varchar(255),
    
    -- Educational project specific fields  
    educational_slots int,
    educational_justification varchar(2000),
    educational_course varchar(60),
    
    primary key (project_id),
    constraint chk_project_type CHECK (project_type IN('Research','Extension','Educational'))
);

create table fellow(
    cpf varchar(20) NOT NULL UNIQUE,
    lattes_curriculum varchar(255),
    birth_date DATE NOT NULL,
    project_id bigint NOT NULL,
    student_id bigint NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
    primary key (project_id, student_id),
    constraint uq_fellow_project_student unique(project_id, student_id)
);

create table participant(
    participant_id bigint auto_increment,
    cpf varchar(20) NOT NULL UNIQUE,
    name varchar(63) NOT NULL,
    contact_number varchar(30),
    lattes_curriculum varchar(255),
    birth_date DATE NOT NULL,
    student_id bigint,
    teacher_id bigint,
    project_id bigint NOT NULL,
    external_user_id bigint,
    primary key (participant_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE SET NULL,
    FOREIGN KEY (external_user_id) REFERENCES external_user(external_user_id) ON DELETE SET NULL,
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teacher_staff(teacher_staff_id) ON DELETE SET NULL,
    constraint uq_participant_project unique(project_id, student_id, teacher_id, external_user_id)
);

create table teacher_idea(
    idea_id bigint,
    teacher_id bigint,
    FOREIGN KEY (teacher_id) REFERENCES teacher_staff(teacher_staff_id) ON DELETE CASCADE,
    FOREIGN KEY (idea_id) REFERENCES idea(idea_id) ON DELETE CASCADE,
    primary key (idea_id, teacher_id),
    constraint uq_teacher_idea unique(teacher_id, idea_id)
);

create table student_idea(
    idea_id bigint,
    student_id bigint,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (idea_id) REFERENCES idea(idea_id) ON DELETE CASCADE,
    primary key (idea_id, student_id),
    constraint uq_student_idea unique(student_id, idea_id)
);

create table company_project(
    project_id bigint,
    company_id bigint,
    FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
    primary key (project_id, company_id),
    constraint uq_company_project unique(company_id, project_id)
);

create table teacher_project(
    project_id bigint,
    teacher_id bigint,
    FOREIGN KEY (teacher_id) REFERENCES teacher_staff(teacher_staff_id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE,
    primary key (project_id, teacher_id),
    constraint uq_teacher_project unique(teacher_id, project_id)
);

-- Add indexes for better query performance
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
