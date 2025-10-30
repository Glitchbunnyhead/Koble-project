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
public class ProjectDAO implements Crudl<Project> {
    
    // Creating a MySqlConnection attribute
    private final MySqlConnection connection;

    // Class constructor
    public ProjectDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    @Override
    public Project create(Project project) {
        this.connection.openConnection();
        
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_PROJECT + " ("
                + ConstantsDataBase.PROJECT_COLUNA_TIMELINE + ", "
                + ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK + ", "
                + ConstantsDataBase.PROJECT_COLUNA_DURATION + ", "
                + ConstantsDataBase.PROJECT_COLUNA_IMAGE + ", "
                + ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS + ", "
                + ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE + ", "
                + ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_TYPE + ", "
                + ConstantsDataBase.PROJECT_COLUNA_SALARY + ", "
                + ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS + ", "
                + ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_QUANTITY + ", "
                + ConstantsDataBase.PROJECT_COLUNA_TITLE + ", "
                + ConstantsDataBase.PROJECT_COLUNA_SUBTITLE + ", "
                + ConstantsDataBase.PROJECT_COLUNA_COORDINATOR + ", "
                + ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION + ", "
                + ConstantsDataBase.PROJECT_COLUNA_TYPE + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, project.getTimeline());
            st.setString(2, project.getExternalLink());
            st.setString(3, project.getDuration());
            st.setString(4, project.getImage());
            st.setString(5, project.getComplementHours());
            st.setBoolean(6, project.isScholarshipAvailable());
            st.setString(7, project.getScholarshipType());
            st.setDouble(8, project.getSalary());
            st.setString(9, project.getRequirements());
            st.setInt(10, project.getScholarshipQuantity());
            st.setString(11, project.getTitle());
            st.setString(12, project.getSubtitle());
            st.setString(13, project.getCoordinator());
            st.setString(14, project.getDescription());
            st.setString(15, project.getType());

            st.executeUpdate();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    project.setId(rs.getLong(1)); 
                }
            }

            System.out.println("Project created successfully with ID: " + project.getId());
            return project;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating project: " + e.getMessage());
            return null;
        } finally {
            connection.closeConnection();
        }
    }
}

    @Override
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

    @Override
    public Project update(long id, Project project) {
        if (id <= 0) {
            System.out.println("Error updating project: Project ID is missing or invalid.");
            return null;
        }
        
        this.connection.openConnection();
        
        String sql = "UPDATE " + ConstantsDataBase.TABLE_PROJECT + " SET "
                + ConstantsDataBase.PROJECT_COLUNA_TIMELINE + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_DURATION + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_IMAGE + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_TYPE + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_SALARY + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_QUANTITY + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_TITLE + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_SUBTITLE + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_COORDINATOR + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_TYPE + " = ? "
                + "WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);

            st.setString(1, project.getTimeline());
            st.setString(2, project.getExternalLink());
            st.setString(3, project.getDuration());
            st.setString(4, project.getImage());
            st.setString(5, project.getComplementHours());
            st.setBoolean(6, project.isScholarshipAvailable());
            st.setString(7, project.getScholarshipType());
            st.setDouble(8, project.getSalary());
            st.setString(9, project.getRequirements());
            st.setInt(10, project.getScholarshipQuantity());
            st.setString(11, project.getTitle());
            st.setString(12, project.getSubtitle());
            st.setString(13, project.getCoordinator());
            st.setString(14, project.getDescription());
            st.setString(15, project.getType());
            st.setLong(16, id);

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Project updated successfully with ID: " + id);
                return project;
            } else {
                System.out.println("Project with ID " + id + " not found for update.");
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

    @Override
    public Project read(long id) {
        Project project = null;
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + 
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?;";

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

@Override
public List<Project> listAll() {
    List<Project> projects = new ArrayList<>();
    this.connection.openConnection();
    String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + ";";

    PreparedStatement st = null;
    ResultSet rs = null;

    try {
        st = connection.getConnection().prepareStatement(sql);

        // Executing the query and getting the result set.
        rs = st.executeQuery();

        // Looping through all results and creating Project instances via helper.
        while (rs.next()) {
            Project project = createProjectFromResultSet(rs);
            projects.add(project);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error listing projects: " + e.getMessage());
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

    return projects;
}


private Project createProjectFromResultSet(ResultSet rs) throws SQLException {
        Project project = new Project() {}; // Creating anonymous subclass since Project is abstract
        
        // Set all the fields from the base Project class
        project.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID));
        project.setTimeline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TIMELINE));
        project.setExternalLink(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK));
        project.setDuration(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DURATION));
        project.setImage(rs.getString(ConstantsDataBase.PROJECT_COLUNA_IMAGE));
        project.setComplementHours(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS));
        project.setScholarshipAvailable(rs.getBoolean(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE));
        project.setScholarshipType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_TYPE));
        project.setSalary(rs.getDouble(ConstantsDataBase.PROJECT_COLUNA_SALARY));
        project.setRequirements(rs.getString(ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS));
        project.setScholarshipQuantity(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_QUANTITY));
        project.setTitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TITLE));
        project.setSubtitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SUBTITLE));
        project.setCoordinator (rs.getString(ConstantsDataBase.PROJECT_COLUNA_COORDINATOR));
        project.setDescription(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION));
        project.setType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TYPE));
        
        return project;
    }
}