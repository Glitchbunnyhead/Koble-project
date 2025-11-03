package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.*;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectDAO implements Crudl<Project> {

    private final MySqlConnection connection;

    public ProjectDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    @Override
    public Project create(Project project) {
        if (project == null) throw new IllegalArgumentException("Project inválido");

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

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating project failed, no ID obtained.");
                }
            }

            return project;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    @Override
    public String delete(long id) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido para deletar Project");

        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_PROJECT + " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0 ? "Project deleted successfully" : "Nenhum Project encontrado com ID: " + id;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    @Override
    public Project update(long id, Project project) {
        if (id <= 0 || project == null) throw new IllegalArgumentException("Project inválido ou ID inválido");

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
                + "WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?;";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
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
            return rowsAffected > 0 ? project : null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    @Override
    public Project read(long id) {
        if (id <= 0) return null;

        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        Project project = null;
        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    project = mapResultSetToProject(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return project;
    }

    @Override
    public List<Project> listAll() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT;

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Projects: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return projects;
    }

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        String type = rs.getString(ConstantsDataBase.PROJECT_COLUNA_TYPE);
        Project project;
        switch (type.toLowerCase()) {
            case "research":
                project = new ResearchProject();
                break;
            case "educational":
                project = new EducationalProject();
                break;
            case "extension":
                project = new ExtensionProject();
                break;
            default:
                throw new SQLException("Tipo de projeto desconhecido: " + type);
        }

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
        project.setCoordinator(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COORDINATOR));
        project.setDescription(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION));
        project.setType(type);

        return project;
    }

    public boolean exists(long projectId) {
        if (projectId <= 0) return false;

        String sql = "SELECT 1 FROM " + ConstantsDataBase.TABLE_PROJECT + " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";
        boolean exists = false;

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, projectId);
            try (ResultSet rs = st.executeQuery()) {
                exists = rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return exists;
    }
}
