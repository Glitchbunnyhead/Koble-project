package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.ResearchProject;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ResearchProjectDAO {

    private final MySqlConnection connection;

    public ResearchProjectDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    public ResearchProject create(ResearchProject researchProject, long projectId) {
        if (researchProject == null || projectId <= 0)
            throw new IllegalArgumentException("ResearchProject inválido ou ID do projeto inválido");

        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_RESEARCHPROJECT + " ("
                + ConstantsDataBase.PROJECT_COLUNA_ID + ", "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE + ", "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION + ", "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE + ") VALUES (?, ?, ?, ?)";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, projectId);
            st.setString(2, researchProject.getObjective());
            st.setString(3, researchProject.getJustification());
            st.setString(4, researchProject.getDiscipline());

            st.executeUpdate();
            researchProject.setId(projectId);
            return researchProject;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar ResearchProject: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    public String delete(long projectId) {
        if (projectId <= 0) throw new IllegalArgumentException("ID do projeto inválido");

        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_RESEARCHPROJECT
                + " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, projectId);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0 ? "Research Project deletado com sucesso"
                                    : "Research Project não encontrado ou já deletado";

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar ResearchProject: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    // --- UPDATE ---
    public ResearchProject update(long projectId, ResearchProject researchProject) {
        if (projectId <= 0 || researchProject == null)
            throw new IllegalArgumentException("ResearchProject inválido ou ID do projeto inválido");

        String sql = "UPDATE " + ConstantsDataBase.TABLE_RESEARCHPROJECT + " SET "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE + " = ? "
                + "WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setString(1, researchProject.getObjective());
            st.setString(2, researchProject.getJustification());
            st.setString(3, researchProject.getDiscipline());
            st.setLong(4, projectId);

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0 ? researchProject : null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar ResearchProject: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    public ResearchProject read(long projectId) {
        if (projectId <= 0) return null;

        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " p "
                + "INNER JOIN " + ConstantsDataBase.TABLE_RESEARCHPROJECT + " r "
                + "ON p." + ConstantsDataBase.PROJECT_COLUNA_ID + " = r." + ConstantsDataBase.PROJECT_COLUNA_ID
                + " WHERE p." + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, projectId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? mapResultSetToResearchProject(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler ResearchProject: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    public List<ResearchProject> listAll() {
        List<ResearchProject> researchProjects = new ArrayList<>();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " p "
                + "INNER JOIN " + ConstantsDataBase.TABLE_RESEARCHPROJECT + " r "
                + "ON p." + ConstantsDataBase.PROJECT_COLUNA_ID + " = r." + ConstantsDataBase.PROJECT_COLUNA_ID;

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                researchProjects.add(mapResultSetToResearchProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ResearchProjects: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return researchProjects;
    }

    private ResearchProject mapResultSetToResearchProject(ResultSet rs) throws SQLException {
        ResearchProject rp = new ResearchProject();
        rp.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID));
        rp.setTimeline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TIMELINE));
        rp.setExternalLink(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK));
        rp.setDuration(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DURATION));
        rp.setImage(rs.getString(ConstantsDataBase.PROJECT_COLUNA_IMAGE));
        rp.setComplementHours(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COMPLEMENTARY_HOURS));
        rp.setScholarshipAvailable(rs.getBoolean(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_AVAILABLE));
        rp.setScholarshipType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_TYPE));
        rp.setSalary(rs.getDouble(ConstantsDataBase.PROJECT_COLUNA_SALARY));
        rp.setRequirements(rs.getString(ConstantsDataBase.PROJECT_COLUNA_REQUIREMENTS));
        rp.setScholarshipQuantity(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_SCHOLARSHIP_QUANTITY));
        rp.setTitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TITLE));
        rp.setSubtitle(rs.getString(ConstantsDataBase.PROJECT_COLUNA_SUBTITLE));
        rp.setCoordinator(rs.getString(ConstantsDataBase.PROJECT_COLUNA_COORDINATOR));
        rp.setDescription(rs.getString(ConstantsDataBase.PROJECT_COLUNA_DESCRIPTION));
        rp.setType(rs.getString(ConstantsDataBase.PROJECT_COLUNA_TYPE));

        rp.setObjective(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE));
        rp.setJustification(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION));
        rp.setDiscipline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE));

        return rp;
    }
}
