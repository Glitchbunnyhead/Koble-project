package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.*;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class EducationalProjectDAO {

    private MySqlConnection connection;

    public EducationalProjectDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    public EducationalProject create(EducationalProject educationalProject, long idProject) throws SQLException{
        this.connection.openConnection();
       
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_EDUCATIONALPROJECT + " ("
                + ConstantsDataBase.PROJECT_COLUNA_ID + ", "
                +ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_SLOTS + ", "
                +ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_JUSTIFICATION + ", "
                +ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_COURSE + ") VALUES (?, ?, ?, ?)";


        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);

            st.setLong(1, idProject);
            st.setInt(2, educationalProject.getSlots());
            st.setString(3, educationalProject.getJustification());
            st.setString(4, educationalProject.getCourse());

            st.executeUpdate();
            
            System.out.println("Educatioanl Project created successfully");
            return educationalProject;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating  Educational Project: " + e.getMessage());
            return null;
        } finally {
            connection.closeConnection();
        }
    }

    public String delete(long id) {
        this.connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_EDUCATIONALPROJECT + 
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, id);
            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting Educational Project: " + e.getMessage();
        } finally {
            connection.closeConnection();
        }

        return "Educational Project deleted successfully";
    }


    public EducationalProject update(long id, EducationalProject educationalProject) {
        if (id <= 0) {
            System.out.println("Error updating Educational Project: Project ID is missing or invalid.");
            return null;
        }
        
        this.connection.openConnection();
        
        String sql = "UPDATE " + ConstantsDataBase.TABLE_EDUCATIONALPROJECT + " SET "
                + ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_SLOTS + " = ?, "
                +ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_JUSTIFICATION + "= ?,"
                +ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_COURSE + " = ? "
                + "WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);

            st.setInt(1, educationalProject.getSlots());
            st.setString(2, educationalProject.getJustification());
            st.setString(3, educationalProject.getCourse());
            st.setLong(4, id);


            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Educational Project updated successfully with ID: " + id);
                return educationalProject;
            } else {
                System.out.println("Educational Project with Project ID " + id + " not found for update.");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating project: " + e.getMessage());
            return null;
        } finally {
            connection.closeConnection();
        }
    }
               
    public EducationalProject read(long id) {
        EducationalProject educationalProject  = null;
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " p INNER JOIN " + ConstantsDataBase.TABLE_EDUCATIONALPROJECT + " e ON p.project_id = e.project_id WHERE p." + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?;";

        try {
        
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    educationalProject = new EducationalProject();
                    educationalProject.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID));
                    educationalProject.setTimeline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TIMELINE));
                    educationalProject.setExternalLink(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK));
                    educationalProject.setDuration(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DURATION));
                    educationalProject.setImage(rs.getString(ConstantsDataBase.PROJECT_COLUNA_IMAGE));
                    educationalProject.setComplementHours(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS));
                    educationalProject.setScholarshipAvailable(rs.getBoolean(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE));
                    educationalProject.setScholarshipType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_TYPE));
                    educationalProject.setSalary(rs.getDouble(ConstantsDataBase.PROJECT_COLUNA_SALARY));
                    educationalProject.setRequirements(rs.getString(ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS));
                    educationalProject.setScholarshipQuantity(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_QUANTITY));
                    educationalProject.setTitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TITLE));
                    educationalProject.setSubtitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SUBTITLE));
                    educationalProject.setCoordinator(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COORDINATOR));
                    educationalProject.setDescription(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION));
                    educationalProject.setType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TYPE));
                    educationalProject.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID));
                    educationalProject.setSlots(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_SLOTS));
                    educationalProject.setJustification(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_JUSTIFICATION));
                    educationalProject.setCourse(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_COURSE));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading project: " + e.getMessage());
        } finally {
            connection.closeConnection();
        }

        return educationalProject;
    }


    public List<EducationalProject> listAll() {
    List<EducationalProject> educationalProjects = new ArrayList<>();
    this.connection.openConnection();
    String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " p INNER JOIN " + ConstantsDataBase.TABLE_EDUCATIONALPROJECT + " e ON p.project_id = e.project_id ;";

    PreparedStatement st = null;
    ResultSet rs = null;

    try {
        st = connection.getConnection().prepareStatement(sql);

        // Executing the query and getting the result set.
        rs = st.executeQuery();

        // Looping through all results and creating Project instances via helper.
        while (rs.next()) {

            EducationalProject educationalProject = new EducationalProject();
            educationalProject.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID));
            educationalProject.setTimeline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TIMELINE));
            educationalProject.setExternalLink(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK));
            educationalProject.setDuration(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DURATION));
            educationalProject.setImage(rs.getString(ConstantsDataBase.PROJECT_COLUNA_IMAGE));
            educationalProject.setComplementHours(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS));
            educationalProject.setScholarshipAvailable(rs.getBoolean(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE));
            educationalProject.setScholarshipType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_TYPE));
            educationalProject.setSalary(rs.getDouble(ConstantsDataBase.PROJECT_COLUNA_SALARY));
            educationalProject.setRequirements(rs.getString(ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS));
            educationalProject.setScholarshipQuantity(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_QUANTITY));
            educationalProject.setTitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TITLE));
            educationalProject.setSubtitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SUBTITLE));
            educationalProject.setCoordinator(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COORDINATOR));
            educationalProject.setDescription(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION));
            educationalProject.setType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TYPE));

            educationalProject.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID));
            educationalProject.setSlots(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_SLOTS));
            educationalProject.setJustification(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_JUSTIFICATION));
            educationalProject.setCourse(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_COURSE));

            educationalProjects.add(educationalProject);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error listing  Educational Projects: " + e.getMessage());
    } finally {
        // Close ResultSet and PreparedStatement if they were opened.
        try {
            if (rs != null) rs.close();
        } catch (SQLException ignored) {}
        try {
            if (st != null) st.close();
        } catch (SQLException ignored) {}
        connection.closeConnection();
    }

    return educationalProjects;
}

    }


