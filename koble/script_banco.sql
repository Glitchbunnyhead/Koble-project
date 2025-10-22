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

create table research(
    research_id bigint AUTO_INCREMENT,
    objective varchar(2000) NOT NULL,
    justification varchar(2000) NOT NULL,
    discipline varchar(60) NOT NULL,
    PRIMARY KEY (research_id)
);

create table extension(
    extension_id bigint auto_increment,
    target_audience varchar(255) NOT NULL,
    vacancies int NOT NULL,
    selection varchar(255) NOT NULL,
    primary key (extension_id)
);

create table teaching(
    teaching_id bigint auto_increment,
    vacancies int NOT NULL,
    justification varchar(2000) NOT NULL,
    discipline varchar(60) NOT NULL,
    primary key (teaching_id)
);

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
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (teacher_id) REFERENCES teacher_staff(teacher_staff_id),
    PRIMARY KEY (idea_id),
    CONSTRAINT chk_valid_type CHECK (type IN('Teaching','Research','Extension'))
);

create table project(
    project_id bigint auto_increment,
    cronograma varchar(255) NOT NULL,
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
    extension_id bigint,
    research_id bigint,
    teaching_id bigint,
    primary key (project_id),
    foreign key (extension_id) references extension(extension_id),
    foreign key (research_id) references research(research_id),
    foreign key (teaching_id) references teaching(teaching_id),
    constraint uq_project_type unique(extension_id, research_id, teaching_id)
);

create table fellow(
    cpf varchar(20) NOT NULL UNIQUE,
    lattes_curriculum varchar(255),
    birth_date DATE NOT NULL,
    project_id bigint NOT NULL,
    student_id bigint NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    primary key (project_id, student_id) 
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
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (external_user_id) REFERENCES external_user(external_user_id),
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (teacher_id) REFERENCES teacher_staff(teacher_staff_id),
    constraint uq_participant unique(teacher_id, project_id, external_user_id, student_id)
);

create table teacher_idea(
    idea_id bigint,
    teacher_id bigint,
    FOREIGN KEY (teacher_id) REFERENCES teacher_staff(teacher_staff_id),
    FOREIGN KEY (idea_id) REFERENCES idea(idea_id),
    primary key (idea_id, teacher_id)
);

create table student_idea(
    idea_id bigint,
    student_id bigint,
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (idea_id) REFERENCES idea(idea_id),
    primary key (idea_id, student_id)
);

create table company_project(
    project_id bigint,
    company_id bigint,
    FOREIGN KEY (company_id) REFERENCES company(company_id),
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    primary key (project_id, company_id)
);
create table teacher_project(
    project_id bigint,
    teacher_id bigint,
    FOREIGN KEY (teacher_id) REFERENCES teacher_staff(teacher_staff_id),
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    primary key (project_id, teacher_id)
);
