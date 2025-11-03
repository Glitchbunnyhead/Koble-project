package com.koble.koble.persistence;

//Class that contains all the constants used in the Database.
//Main function is to define estructure.
//Only change if the Database structure chages.
 public final class ConstantsDataBase {

    //Tables names:
    public static final String TABLE_COMPANY = "company";
    public static final String TABLE_COMPANYPROJECT = "company_project";
    public static final String TABLE_EDUCATIONALPROJECT = "educational";
    public static final String TABLE_EXTERNALPERSON = "external_user";
    public static final String TABLE_EXTENTIONPROJECT = "extension";
    public static final String TABLE_FELLOW = "fellow";
    public static final String TABLE_IDEA = "idea";
    public static final String TABLE_PARTICIPANT = "participant";
    public static final String TABLE_PROJECT = "project";
    public static final String TABLE_RESEARCHPROJECT = "research";
    public static final String TABLE_STUDENT = "student";
    public static final String TABLE_STUDENTIDEA = "student_idea";
    public static final String TABLE_TEACHER = "teacher_staff";
    public static final String TABLE_TEACHERIDEA = "teacher_idea";

    //Common column names:
    public static final String COLUMN_AIM = "objective";
    public static final String COLUMN_BIRTHDATE = "birthdate";
    public static final String COLUMN_COMPANYID = "companyid";
    public static final String COLUMN_CPF = "cpf";
    public static final String COLUMN_DISCIPLINE = "discipline";
    public static final String COLUMN_EXTERNALPERSONID = "external_user_id";
    public static final String COLUMN_EXTENTIONID = "extention_id";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IDEAID = "idea_id";
    public static final String COLUMN_JUSTIFICATION = "justification";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_LATTESCURRICULUM = "lattes_curriculum";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PROJECT_ID = "project_id";
    public static final String COLUMN_RESEARCH_ID = "research_id";
    public static final String COLUMN_SLOTS = "slots";
    public static final String COLUMN_STUDENTID = "student_id";
    public static final String COLUMN_SUBTITLE = "subtitle";
    public static final String COLUMN_TARGETAUDIENCE = "target_audience";
    public static final String COLUMN_TEACHINGID = "educational_id";
    public static final String COLUMN_TEACHERID = "teacher_id";
    public static final String COLUMN_TITLE = "title";
    
    //Specific column names:
    // Table: teacher_staff
    public static final String TEACHER_COLUNA_ID = "teacher_staff_id";
    public static final String TEACHER_COLUNA_SIAPE = "siape";

    // Table: student
    public static final String STUDENT_COLUNA_ENROLLMENT = "enrollment";
    public static final String STUDENT_COLUNA_ID = "student_id";

    // Table: company
    public static final String COMPANY_COLUNA_CNPJ = "cnpj";
    public static final String COMPANY_COLUNA_ID = "company_id";

    // Table: external_user
    public static final String EXTERNAL_PERSON_COLUNA_ID = "external_user_id";

    // Table: idea
    public static final String IDEA_COLUNA_AREA = "area";
    public static final String IDEA_COLUNA_DESCRIPTION = "description";
    public static final String IDEA_COLUNA_ID = "idea_id";
    public static final String IDEA_COLUNA_PROPOSER = "proposer";
    public static final String IDEA_COLUNA_TYPE = "type";

    // Table: project (columns matched to script_banco.sql)
    public static final String PROJECT_COLUNA_ID = "project_id";
    public static final String PROJECT_COLUNA_TITLE = "title";
    public static final String PROJECT_COLUNA_SUBTITLE = "subtitle";
    public static final String PROJECT_COLUNA_COORDINATOR = "coordinator";
    public static final String PROJECT_COLUNA_DESCRIPTION = "description";
    // timeline in the DB is named "timeline"
    public static final String PROJECT_COLUNA_TIMELINE = "timeline";
    public static final String PROJECT_COLUNA_EXTERNAL_LINK = "external_link";
    public static final String PROJECT_COLUNA_DURATION = "duration";
    public static final String PROJECT_COLUNA_IMAGE = "image";
    public static final String PROJECT_COLUNA_COMPLEMENTARY_HOURS = "complementary_hours";
    public static final String PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE = "scholarship_available";
    public static final String PROJECT_COLUNA_SCHOLARSHIP_TYPE = "scholarship_type";
    public static final String PROJECT_COLUNA_SALARY = "salary";
    public static final String PROJECT_COLUNA_REQUIREMENTS = "requirements";
    public static final String PROJECT_COLUNA_SCHOLARSHIP_QUANTITY = "scholarship_quantity";
    public static final String PROJECT_COLUNA_TYPE = "project_type";
    
    // Research project specific columns
    public static final String PROJECT_COLUNA_RESEARCH_OBJECTIVE = "research_objective";
    public static final String PROJECT_COLUNA_RESEARCH_JUSTIFICATION = "research_justification";
    public static final String PROJECT_COLUNA_RESEARCH_DISCIPLINE = "research_discipline";
    
    // Extension project specific columns
    public static final String PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE = "extension_target_audience";
    public static final String PROJECT_COLUNA_EXTENSION_SLOTS = "extension_slots";
    public static final String PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS = "extension_selection_process";
    
    // Educational project specific columns
    public static final String PROJECT_COLUNA_EDUCATIONAL_SLOTS = "educational_slots";
    public static final String PROJECT_COLUNA_EDUCATIONAL_JUSTIFICATION = "educational_justification";
    public static final String PROJECT_COLUNA_EDUCATIONAL_COURSE = "educational_course";

    // Table: participant
    public static final String PARTICIPANT_COLUNA_CONTACT_NUMBER = "contact_number";
    public static final String PARTICIPANT_COLUNA_ID = "participant_id";

    //To avoid instantiation
    private ConstantsDataBase() {
        throw new UnsupportedOperationException();
    }


}
