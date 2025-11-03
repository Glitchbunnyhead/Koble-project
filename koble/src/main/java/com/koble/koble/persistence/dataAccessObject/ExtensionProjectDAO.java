package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.ExtensionProject;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExtensionProjectDAO {

    private final MySqlConnection connection;

    public ExtensionProjectDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    public ExtensionProject create(ExtensionProject extensionProject, long idProject) {
        connection.openConnection();
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_EXTENTIONPROJECT + " (" +
                ConstantsDataBase.PROJECT_COLUNA_ID + ", " +
                ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE + ", " +
                ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS + ", " +
                ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS + ") VALUES (?, ?, ?, ?)";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, idProject);
            st.setString(2, extensionProject.getTargetAudience());
            st.setInt(3, extensionProject.getSlots());
            st.setString(4, extensionProject.getSelectionProcess());
            st.executeUpdate();
            extensionProject.setId(idProject);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar Extension Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return extensionProject;
    }

    public String delete(long id) {
        connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_EXTENTIONPROJECT +
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar Extension Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return "Extension Project deletado com sucesso";
    }

    public ExtensionProject update(long id, ExtensionProject extensionProject) {
        if (id <= 0) {
            throw new RuntimeException("Erro ao atualizar Extension Project: Project ID inválido.");
        }

        connection.openConnection();
        String sql = "UPDATE " + ConstantsDataBase.TABLE_EXTENTIONPROJECT + " SET " +
                ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS + " = ?, " +
                ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS + " = ? " +
                "WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setString(1, extensionProject.getTargetAudience());
            st.setInt(2, extensionProject.getSlots());
            st.setString(3, extensionProject.getSelectionProcess());
            st.setLong(4, id);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Extension Project com ID " + id + " não encontrado para atualização.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar Extension Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return extensionProject;
    }

    public ExtensionProject read(long id) {
        ExtensionProject extensionProject = null;
        connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " p " +
                     "INNER JOIN " + ConstantsDataBase.TABLE_EXTENTIONPROJECT + " x " +
                     "ON p.project_id = x.project_id " +
                     "WHERE p." + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    extensionProject = mapResultSetToExtensionProject(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler Extension Project: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return extensionProject;
    }

    public List<ExtensionProject> listAll() {
        List<ExtensionProject> extensionProjects = new ArrayList<>();
        connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + " p " +
                     "INNER JOIN " + ConstantsDataBase.TABLE_EXTENTIONPROJECT + " x " +
                     "ON p.project_id = x.project_id";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                extensionProjects.add(mapResultSetToExtensionProject(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Extension Projects: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }

        return extensionProjects;
    }

    private ExtensionProject mapResultSetToExtensionProject(ResultSet rs) throws SQLException {
        ExtensionProject extensionProject = new ExtensionProject();
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
        return extensionProject;
    }
}
