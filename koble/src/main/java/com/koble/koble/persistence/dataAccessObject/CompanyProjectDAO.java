package com.koble.koble.persistence.dataAccessObject;

import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.koble.koble.model.Company;
import com.koble.koble.model.CompanyProject;
import com.koble.koble.model.Project;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class CompanyProjectDAO {

    private final MySqlConnection connection;
    private final ProjectDAO projectDAO;
    private final CompanyDAO companyDAO;

    public CompanyProjectDAO(MySqlConnection connection, ProjectDAO projectDAO, CompanyDAO companyDAO){
        this.connection = connection;
        this.projectDAO = projectDAO;
        this.companyDAO = companyDAO;
    }

    public CompanyProject create(CompanyProject companyProject) {
        connection.openConnection();
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_COMPANYPROJECT +
                     " (company_id, project_id) VALUES (?, ?)";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, companyProject.getCompanyId());
            st.setLong(2, companyProject.getProjectId());
            st.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Erro ao criar relação empresa-projeto: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return companyProject;
    }

    public String delete(long companyId, long projectId) {
        connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT +
                     " WHERE company_id = ? AND project_id = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, companyId);
            st.setLong(2, projectId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar relação empresa-projeto: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return "Relação empresa-projeto deletada com sucesso";
    }

    public String deleteAllByCompanyId(long companyId) {
        connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT + " WHERE company_id = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, companyId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar projetos da empresa: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return "Todos os projetos da empresa deletados com sucesso";
    }

    public String deleteAllByProjectId(long projectId) {
        connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT + " WHERE project_id = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, projectId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar empresas do projeto: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return "Todas as empresas do projeto deletadas com sucesso";
    }

    public boolean exists(long companyId, long projectId) {
        boolean exists = false;
        connection.openConnection();
        String sql = "SELECT COUNT(*) FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT +
                     " WHERE company_id = ? AND project_id = ?";
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, companyId);
            st.setLong(2, projectId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar relação empresa-projeto: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return exists;
    }

    public List<CompanyProject> listAll() {
        List<CompanyProject> companyProjects = new ArrayList<>();
        connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT;
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                CompanyProject companyProject = new CompanyProject();
                companyProject.setCompanyId(rs.getLong("company_id"));
                companyProject.setProjectId(rs.getLong("project_id"));
                companyProjects.add(companyProject);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar relações empresa-projeto: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return companyProjects;
    }

    public List<Project> findProjectsByCompanyId(long companyId) {
        List<Project> projects = new ArrayList<>();
        connection.openConnection();
        String sql = "SELECT " + ConstantsDataBase.PROJECT_COLUNA_ID + " FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT +
                     " WHERE company_id = ?";
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, companyId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    long projectId = rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID);
                    Project project = projectDAO.read(projectId);
                    if (project != null) {
                        projects.add(project);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar projetos por empresa: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return projects;
    }

    public List<Company> findCompaniesByProjectId(long projectId) {
        List<Company> companies = new ArrayList<>();
        connection.openConnection();
        String sql = "SELECT " + ConstantsDataBase.COMPANY_COLUNA_ID + " FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT +
                     " WHERE project_id = ?";
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, projectId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    long companyId = rs.getLong(ConstantsDataBase.COMPANY_COLUNA_ID);
                    Company company = companyDAO.read(companyId);
                    if (company != null) {
                        companies.add(company);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresas por projeto: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return companies;
    }
}
