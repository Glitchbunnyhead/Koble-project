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
public class ProjectDAO {
    
    // Creating a MySqlConnection attribute
    private final MySqlConnection connection;

    // Class constructor
    public ProjectDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    //@Override
    public Project create(Project project) {
        // Opening the connection to the Database
        this.connection.openConnection();
        
        // Base SQL for common fields
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_PROJECT + " (" +
                ConstantsDataBase.PROJECT_COLUNA_TIMELINE + ", " +
                ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK + ", " +
                ConstantsDataBase.PROJECT_COLUNA_DURATION + ", " +
                ConstantsDataBase.PROJECT_COLUNA_IMAGE + ", " +
                ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS + ", " +
                ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE + ", " +
                ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_TYPE + ", " +
                ConstantsDataBase.PROJECT_COLUNA_SALARY + ", " +
                ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS + ", " +
                ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_QUANTITY + ", " +
                ConstantsDataBase.PROJECT_COLUNA_TITLE + ", " +
                ConstantsDataBase.PROJECT_COLUNA_SUBTITLE + ", " +
                ConstantsDataBase.PROJECT_COLUNA_COORDINATOR + ", " +
                ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION + ", " +
                ConstantsDataBase.PROJECT_COLUNA_TYPE;

        // Add type-specific columns based on project type
        if (project instanceof ResearchProject) {
            sql += ", " + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE + 
                   ", " + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION + 
                   ", " + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE;
        } else if (project instanceof ExtensionProject) {
            sql += ", " + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE + 
                   ", " + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS + 
                   ", " + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS;
        } else if (project instanceof EducationalProject) {
            sql += ", " + ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_SLOTS + 
                   ", " + ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_JUSTIFICATION + 
                   ", " + ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_COURSE;
        }

        sql += ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";

        // Add parameter placeholders for type-specific fields
        if (project instanceof ResearchProject || project instanceof ExtensionProject || project instanceof EducationalProject) {
            sql += ", ?, ?, ?";
        }
        sql += ")";

        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);

            // Set common parameters
            st.setString(1, project.getTimeline()); // cronograma
            st.setString(2, project.getLinkExtension());
            st.setString(3, project.getDuration());
            st.setString(4, project.getImage());
            st.setString(5, project.getComplementHours());
            st.setBoolean(6, project.isfellowship());
            st.setString(7, project.getType()); // Using type as scholarship_type for now
            st.setDouble(8, project.getfellowValue());
            st.setString(9, project.getRequirements());
            st.setInt(10, project.getFellowshipQuantity()); // Default scholarship_quantity
            st.setString(11, project.getTitle());
            st.setString(12, project.getSubtitle());
            st.setString(13, project.getCoordenator());
            st.setString(14, project.getDescription());
            st.setString(15, project.getType());

            // Set type-specific parameters
            int paramIndex = 16;
            if (project instanceof ResearchProject) {
                ResearchProject research = (ResearchProject) project;
                st.setString(paramIndex++, research.getAim());
                st.setString(paramIndex++, research.getJustification());
                st.setString(paramIndex, research.getCourses());
            } else if (project instanceof ExtensionProject) {
                ExtensionProject extension = (ExtensionProject) project;
                st.setString(paramIndex++, extension.getTargetAudience());
                st.setInt(paramIndex++, extension.getSlots());
                st.setString(paramIndex, extension.getSelectionProcess());
            } else if (project instanceof EducationalProject) {
                EducationalProject educational = (EducationalProject) project;
                st.setInt(paramIndex++, educational.getSlots());
                st.setString(paramIndex++, educational.getJustification());
                st.setString(paramIndex, educational.getCourse());
            }

            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating project: " + e.getMessage());
            return null;
        } finally {
            connection.closeConnection();
        }

        System.out.println("Project created successfully");
        return project;
    }

    //@Override
    public String delete(long id) {
        this.connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_PROJECT + 
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting project: " + e.getMessage();
        } finally {
            connection.closeConnection();
        }

        return "Project deleted successfully";
    }

    //@Override
   public Project update(long id, Project project) {
        // Verify that the project ID was provided and valid (primitive long can't be null)
        if (project.getId() <= 0) {
            System.out.println("Error updating project: Project ID is missing or invalid.");
            return null;
        }
        
        // Opening the connection to the Database.
        this.connection.openConnection();
        
        String sql = "UPDATE " + ConstantsDataBase.TABLE_PROJECT + " SET " +
                ConstantsDataBase.PROJECT_COLUNA_TIMELINE + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_DURATION + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_IMAGE + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_TYPE + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_SALARY + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_QUANTITY + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_TITLE + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_SUBTITLE + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_COORDINATOR + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_TYPE + " = ?"; // 15º parâmetro

        // Add type-specific fields
        if (project instanceof ResearchProject) {
            sql += ", " + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE + " = ?, " + 
                   ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION + " = ?, " + 
                   ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE + " = ?"; 
        } else if (project instanceof ExtensionProject) {
            sql += ", " + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE + " = ?, " + 
                   ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS + " = ?, " + 
                   ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS + " = ?"; 
        } else if (project instanceof EducationalProject) {
            sql += ", " + ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_SLOTS + " = ?, " + 
                   ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_JUSTIFICATION + " = ?, " + 
                   ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_COURSE + " = ?"; 
        }
        
        //Where clause to identify the record to update.
        sql += " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?"; 

        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);

            // Parameters common to all project types.
            st.setString(1, project.getTimeline());
            st.setString(2, project.getLinkExtension());
            st.setString(3, project.getDuration());
            st.setString(4, project.getImage());
            st.setString(5, project.getComplementHours());
            st.setBoolean(6, project.isfellowship());
            st.setString(7, project.getFellowshipType()); // scholarship_type
            st.setDouble(8, project.getfellowValue());
            st.setString(9, project.getRequirements());
            st.setInt(10, project.getFellowshipQuantity()); // scholarship_quantity 
            st.setString(11, project.getTitle());
            st.setString(12, project.getSubtitle());
            st.setString(13, project.getCoordenator());
            st.setString(14, project.getDescription());
            st.setString(15, project.getType()); // project_type

            // Especific parameters based on project type.
            int paramIndex = 16;
            if (project instanceof ResearchProject) {
                ResearchProject research = (ResearchProject) project;
                st.setString(paramIndex++, research.getAim());
                st.setString(paramIndex++, research.getJustification());
                st.setString(paramIndex++, research.getCourses());
            } else if (project instanceof ExtensionProject) {
                ExtensionProject extension = (ExtensionProject) project;
                st.setString(paramIndex++, extension.getTargetAudience());
                st.setInt(paramIndex++, extension.getSlots());
                st.setString(paramIndex++, extension.getSelectionProcess());
            } else if (project instanceof EducationalProject) {
                EducationalProject educational = (EducationalProject) project;
                st.setInt(paramIndex++, educational.getSlots());
                st.setString(paramIndex++, educational.getJustification());
                st.setString(paramIndex++, educational.getCourse());
            }

            //ID parameter for the WHERE clause.
            st.setLong(paramIndex,id);

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Project updated successfully with ID: " + project.getId());
                return project;
            } else {
                System.out.println("Project with ID " + project.getId() + " not found for update.");
                return null; 
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating project: " + e.getMessage());
            return null;
        } finally {
            //Closing the connection.
            connection.closeConnection();
        }
   }

    //@Override
    public Project read(long id) {
        Project project = null;
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + 
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try {
        
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            //If a record is found, create the appropriate Project subclass.
            if (rs.next()) {
                project = createProjectFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading project: " + e.getMessage());
        } finally {
            connection.closeConnection();
        }

        return project;
    }

    //@Override
    public List<Project> listAll() {
        List<Project> projects = new ArrayList<>();
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT;

        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Project project = createProjectFromResultSet(rs);
                if (project != null) {
                    projects.add(project);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading projects: " + e.getMessage());
        } finally {
            connection.closeConnection();
        }

        return projects;
    }

    /**
     * Helper method to create the appropriate Project subclass from ResultSet
     */
    private Project createProjectFromResultSet(ResultSet rs) throws SQLException {
        String projectType = rs.getString(ConstantsDataBase.PROJECT_COLUNA_TYPE);
        Project project;

        // Create appropriate subclass based on type
        switch (projectType) {
            case "Research":
                ResearchProject research = new ResearchProject();
                research.setAim(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE));
                research.setJustification(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION));
                research.setCourses(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE));
                project = research;
                break;

            case "Extension":
                ExtensionProject extension = new ExtensionProject();
                extension.setTargetAudience(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE));
                extension.setSlots(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS));
                extension.setSelectionProcess(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS));
                project = extension;
                break;

            case "Educational":
                EducationalProject educational = new EducationalProject();
                educational.setSlots(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_SLOTS));
                educational.setJustification(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_JUSTIFICATION));
                educational.setCourse(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_COURSE));
                project = educational;
                break;

            default:
                return null; // Unknown type
        }

        // Set common properties
        project.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID));
        project.setTimeline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TIMELINE));
        project.setLinkExtension(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK));
        project.setDuration(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DURATION));
        project.setImage(rs.getString(ConstantsDataBase.PROJECT_COLUNA_IMAGE));
        project.setComplementHours(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS));
        project.setfellowship(rs.getBoolean(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE));
        project.setfellowValue(rs.getDouble(ConstantsDataBase.PROJECT_COLUNA_SALARY));
        project.setRequirements(rs.getString(ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS));
        project.setTitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TITLE));
        project.setSubtitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SUBTITLE));
        project.setCoordenator(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COORDINATOR));
        project.setDescription(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION));
        project.setType(projectType);

        return project;
    }
}
