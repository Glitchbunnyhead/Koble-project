package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.ResearchProject;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ResearchProjectDAO {

    private final MySqlConnection connection;

    public ResearchProjectDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    // --- C R E A T E ---
    // Note: Lançar SQLException é crucial para que o @Transactional saiba que houve um erro.
    public ResearchProject create(ResearchProject researchProject, long idProject) throws SQLException {
        this.connection.openConnection(); 

        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_RESEARCHPROJECT + " ("
                + ConstantsDataBase.PROJECT_COLUNA_ID + ", "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE + ", "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION + ", "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE + ") VALUES (?, ?, ?, ?)";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {

            st.setLong(1, idProject);
            st.setString(2, researchProject.getObjective());
            st.setString(3, researchProject.getJustification());
            st.setString(4, researchProject.getDiscipline()); 

            st.executeUpdate();

            System.out.println("Research Project created successfully");
            researchProject.setId(idProject);
            return researchProject;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating Research Project: " + e.getMessage());
            throw e; 
        } 
        finally { connection.closeConnection(); }
    }

    // --- D E L E T E ---
    public String delete(long id) {
        // REMOVIDO: this.connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_RESEARCHPROJECT +
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            int rowsAffected = st.executeUpdate();
            
            if (rowsAffected > 0) {
                return "Research Project deleted successfully";
            } else {
                return "Research Project not found or already deleted.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Lança RuntimeException para Rollback
            throw new RuntimeException("Error deleting Research Project: " + e.getMessage(), e);
        }
        // REMOVIDO: finally { connection.closeConnection(); }
    }

    // --- U P D A T E ---
    public ResearchProject update(long id, ResearchProject researchProject) {
        if (id <= 0) {
            System.out.println("Error updating Research Project: Project ID is missing or invalid.");
            return null;
        }

        // REMOVIDO: this.connection.openConnection();

        String sql = "UPDATE " + ConstantsDataBase.TABLE_RESEARCHPROJECT + " SET "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE + " = ? "
                + "WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {

            // CORRIGIDO: Usando os getters corretos da Model
            st.setString(1, researchProject.getObjective());
            st.setString(2, researchProject.getJustification());
            st.setString(3, researchProject.getDiscipline()); 
            st.setLong(4, id);


            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Research Project updated successfully with ID: " + id);
                return researchProject;
            } else {
                System.out.println("Research Project with Project ID " + id + " not found for update.");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Lança RuntimeException para Rollback
            throw new RuntimeException("Error updating Research Project: " + e.getMessage(), e);
        } 
        // REMOVIDO: finally { connection.closeConnection(); }
    }

    // --- R E A D ---
    public ResearchProject read(long id) {
        ResearchProject researchProject  = null;
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " p INNER JOIN " + ConstantsDataBase.TABLE_RESEARCHPROJECT + " r ON p.project_id = r.project_id WHERE p." + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?;";


        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    researchProject = new ResearchProject();
                    researchProject.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID));
                    researchProject.setTimeline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TIMELINE));
                    researchProject.setExternalLink(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK));
                    researchProject.setDuration(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DURATION));
                    researchProject.setImage(rs.getString(ConstantsDataBase.PROJECT_COLUNA_IMAGE));
                    researchProject.setComplementHours(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS));
                    researchProject.setScholarshipAvailable(rs.getBoolean(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE));
                    researchProject.setScholarshipType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_TYPE));
                    researchProject.setSalary(rs.getDouble(ConstantsDataBase.PROJECT_COLUNA_SALARY));
                    researchProject.setRequirements(rs.getString(ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS));
                    researchProject.setScholarshipQuantity(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_QUANTITY));
                    researchProject.setTitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TITLE));
                    researchProject.setSubtitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SUBTITLE));
                    researchProject.setCoordinator(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COORDINATOR));
                    researchProject.setDescription(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION));
                    researchProject.setType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TYPE));                  

                    researchProject.setObjective(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE));
                    researchProject.setJustification(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION));
                    researchProject.setDiscipline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Lança RuntimeException
            throw new RuntimeException("Error reading Research Project: " + e.getMessage(), e);
        }
        finally { connection.closeConnection(); }

        return researchProject;
    }

    // --- L I S T ---
    public List<ResearchProject> listAll() {
        List<ResearchProject> researchProjects = new ArrayList<>();
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " p INNER JOIN " + ConstantsDataBase.TABLE_RESEARCHPROJECT + " r ON p.project_id = r.project_id ;";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                ResearchProject researchProject = new ResearchProject();
                researchProject.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID));
                researchProject.setTimeline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TIMELINE));
                researchProject.setExternalLink(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK));
                researchProject.setDuration(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DURATION));
                researchProject.setImage(rs.getString(ConstantsDataBase.PROJECT_COLUNA_IMAGE));
                researchProject.setComplementHours(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS));
                researchProject.setScholarshipAvailable(rs.getBoolean(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE));
                researchProject.setScholarshipType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_TYPE));
                researchProject.setSalary(rs.getDouble(ConstantsDataBase.PROJECT_COLUNA_SALARY));
                researchProject.setRequirements(rs.getString(ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS));
                researchProject.setScholarshipQuantity(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_QUANTITY));
                researchProject.setTitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TITLE));
                researchProject.setSubtitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SUBTITLE));
                researchProject.setCoordinator(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COORDINATOR));
                researchProject.setDescription(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION));
                researchProject.setType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TYPE));  
                researchProject.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID)); 
                researchProject.setObjective(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE));
                researchProject.setJustification(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION));
                researchProject.setDiscipline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE));

                researchProjects.add(researchProject);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Lança RuntimeException
            throw new RuntimeException("Error listing Research Projects: " + e.getMessage(), e);
        }
         finally { connection.closeConnection(); }

        return researchProjects;
    }
}