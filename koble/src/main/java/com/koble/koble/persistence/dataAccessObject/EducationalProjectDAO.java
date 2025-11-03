package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.EducationalProject;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EducationalProjectDAO {

    private final MySqlConnection connection;

    public EducationalProjectDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    public EducationalProject create(EducationalProject educationalProject, long idProject) {
        connection.openConnection();
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_EDUCATIONALPROJECT + " (" +
                ConstantsDataBase.PROJECT_COLUNA_ID + ", " +
                ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_SLOTS + ", " +
                ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_JUSTIFICATION + ", " +
                ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_COURSE + ") VALUES (?, ?, ?, ?)";
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, idProject);
            st.setInt(2, educationalProject.getSlots());
            st.setString(3, educationalProject.getJustification());
            st.setString(4, educationalProject.getCourse());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar Educational Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return educationalProject;
    }

    public String delete(long id) {
        connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_EDUCATIONALPROJECT +
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar Educational Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return "Educational Project deletado com sucesso";
    }

    public EducationalProject update(long id, EducationalProject educationalProject) {
        if (id <= 0) {
            throw new RuntimeException("Erro ao atualizar Educational Project: Project ID inválido.");
        }

        connection.openConnection();
        String sql = "UPDATE " + ConstantsDataBase.TABLE_EDUCATIONALPROJECT + " SET " +
                ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_SLOTS + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_JUSTIFICATION + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_COURSE + " = ? " +
                "WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setInt(1, educationalProject.getSlots());
            st.setString(2, educationalProject.getJustification());
            st.setString(3, educationalProject.getCourse());
            st.setLong(4, id);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Educational Project com ID " + id + " não encontrado para atualização.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar Educational Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return educationalProject;
    }

    public EducationalProject read(long id) {
        EducationalProject educationalProject = null;
        connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " p " +
                     "INNER JOIN " + ConstantsDataBase.TABLE_EDUCATIONALPROJECT + " e " +
                     "ON p.project_id = e.project_id " +
                     "WHERE p." + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    educationalProject = mapResultSetToEducationalProject(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler Educational Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return educationalProject;
    }

    public List<EducationalProject> listAll() {
        List<EducationalProject> educationalProjects = new ArrayList<>();
        connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " p " +
                     "INNER JOIN " + ConstantsDataBase.TABLE_EDUCATIONALPROJECT + " e " +
                     "ON p.project_id = e.project_id";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                educationalProjects.add(mapResultSetToEducationalProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Educational Projects: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return educationalProjects;
    }

    private EducationalProject mapResultSetToEducationalProject(ResultSet rs) throws SQLException {
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
        educationalProject.setSlots(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_SLOTS));
        educationalProject.setJustification(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_JUSTIFICATION));
        educationalProject.setCourse(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EDUCATIONAL_COURSE));
        return educationalProject;
    }
}
