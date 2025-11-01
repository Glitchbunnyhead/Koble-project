package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.ExtensionProject;
import com.koble.koble.model.ResearchProject;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ExtensionProjectDAO {

    private final MySqlConnection connection;

    // Construtor com injeção de dependência da conexão
    public ExtensionProjectDAO(MySqlConnection connection) {
        this.connection = connection;
    }


    public ExtensionProject create(ExtensionProject extensionProject, long idProject) throws SQLException {
        this.connection.openConnection();

        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_EXTENTIONPROJECT + " ("
                + ConstantsDataBase.PROJECT_COLUNA_ID + ", "
                + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE + ", " 
                + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS + ", "           
                + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS + ") VALUES (?, ?, ?, ?)"; 

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {

            st.setLong(1, idProject); 
            st.setString(2, extensionProject.getTargetAudience());
            st.setInt(3, extensionProject.getSlots());
            st.setString(4, extensionProject.getSelectionProcess());

            st.executeUpdate();

            System.out.println("Extension Project created successfully");
            extensionProject.setId(idProject);
            return extensionProject;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating Extension Project: " + e.getMessage());
            throw e; 
        } finally {
            connection.closeConnection();
        }
    }


    public String delete(long id) {
        this.connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_EXTENTIONPROJECT +
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting Extension Project: " + e.getMessage();
        } finally {
            connection.closeConnection();
        }

        return "Extension Project deleted successfully";
    }

    public ExtensionProject update(long id, ExtensionProject extensionProject) {
        if (id <= 0) {
            System.out.println("Error updating Extension Project: Project ID is missing or invalid.");
            return null;
        }

        this.connection.openConnection();

        String sql = "UPDATE " + ConstantsDataBase.TABLE_EXTENTIONPROJECT + " SET "
                + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS + " = ? "
                + "WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?;";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {

            st.setString(1, extensionProject.getTargetAudience());
            st.setInt(2, extensionProject.getSlots());
            st.setString(3, extensionProject.getSelectionProcess());
            st.setLong(4, id);


            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Extension Project updated successfully with ID: " + id);
                return extensionProject;
            } else {
                System.out.println("Extension Project with Project ID " + id + " not found for update.");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating Extension Project: " + e.getMessage());
            return null;
        } finally {
            connection.closeConnection();
        }
    }


    public ExtensionProject read(long id) {
        ExtensionProject extensionProject  = null;
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " p INNER JOIN " + ConstantsDataBase.TABLE_EXTENTIONPROJECT + " x ON p.project_id = x.project_id WHERE p." + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?;";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    extensionProject = new ExtensionProject();
                    extensionProject.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID));
                    extensionProject.setTimeline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TIMELINE));
                    extensionProject.setExternalLink(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK));
                    extensionProject.setDuration(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DURATION));
                    extensionProject.setImage(rs.getString(ConstantsDataBase.PROJECT_COLUNA_IMAGE));
                    extensionProject.setComplementHours(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS));
                    extensionProject.setScholarshipAvailable(rs.getBoolean(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE));
                    extensionProject.setScholarshipType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_TYPE));
                    extensionProject.setSalary(rs.getDouble(ConstantsDataBase.PROJECT_COLUNA_SALARY));
                    extensionProject.setRequirements(rs.getString(ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS));
                    extensionProject.setScholarshipQuantity(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_QUANTITY));
                    extensionProject.setTitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TITLE));
                    extensionProject.setSubtitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SUBTITLE));
                    extensionProject.setCoordinator(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COORDINATOR));
                    extensionProject.setDescription(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION));
                    extensionProject.setType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TYPE));                    extensionProject.setId(id);
                    extensionProject.setTargetAudience(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE));
                    extensionProject.setSlots(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS));
                    extensionProject.setSelectionProcess(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading Extension Project: " + e.getMessage());
        } finally {
            connection.closeConnection();
        }

        return extensionProject;
    }

    public List<ExtensionProject> listAll() {
        List<ExtensionProject> extensionProjects = new ArrayList<>();
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " p INNER JOIN " + ConstantsDataBase.TABLE_EXTENTIONPROJECT + " x ON p.project_id = x.project_id  ;";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                ExtensionProject extensionProject = new ExtensionProject();
                extensionProject = new ExtensionProject();
                extensionProject.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID));
                extensionProject.setTimeline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TIMELINE));
                extensionProject.setExternalLink(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK));
                extensionProject.setDuration(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DURATION));
                extensionProject.setImage(rs.getString(ConstantsDataBase.PROJECT_COLUNA_IMAGE));
                extensionProject.setComplementHours(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS));
                extensionProject.setScholarshipAvailable(rs.getBoolean(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE));
                extensionProject.setScholarshipType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_TYPE));
                extensionProject.setSalary(rs.getDouble(ConstantsDataBase.PROJECT_COLUNA_SALARY));
                extensionProject.setRequirements(rs.getString(ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS));
                extensionProject.setScholarshipQuantity(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_QUANTITY));
                extensionProject.setTitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TITLE));
                extensionProject.setSubtitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SUBTITLE));
                extensionProject.setCoordinator(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COORDINATOR));
                extensionProject.setDescription(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION));
                extensionProject.setType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TYPE));  
                extensionProject.setTargetAudience(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE));
                extensionProject.setSlots(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS));
                extensionProject.setSelectionProcess(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS));

                extensionProjects.add(extensionProject);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error listing Extension Projects: " + e.getMessage());
        } finally {
            connection.closeConnection();
        }

        return extensionProjects;
    }
}