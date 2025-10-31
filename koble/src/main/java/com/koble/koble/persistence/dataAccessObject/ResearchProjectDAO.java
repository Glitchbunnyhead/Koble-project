package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.ResearchProject; // Importa a Model de Pesquisa
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
    

    public ResearchProject create(ResearchProject researchProject, long idProject) throws SQLException {
        this.connection.openConnection();

        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_RESEARCHPROJECT + " ("
                + ConstantsDataBase.PROJECT_COLUNA_ID + ", "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE + ", "           // Assumindo as constantes
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION + ", " // Assumindo as constantes
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE + ") VALUES (?, ?, ?, ?)"; // Assumindo as constantes

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {

            // 1. O ID do Projeto Base (FK)
            st.setLong(1, idProject);
            // 2. Atributos Específicos de ResearchProject
            st.setString(2, researchProject.getObjective());
            st.setString(3, researchProject.getJustification());
            // Atenção: A Model usa 'course', mas o getter é 'getCourses()'. Usando 'getCourses()'
            st.setString(4, researchProject.getDiscipline()); 

            st.executeUpdate();

            System.out.println("Research Project created successfully");
            researchProject.setId(idProject); // Garante que o ID do projeto base seja mantido no objeto
            return researchProject;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating Research Project: " + e.getMessage());
            throw e; 
        } finally {
            connection.closeConnection();
        }
    }

    // --- D E L E T E ---
    
    /**
     * Deleta o registro da tabela de pesquisa usando o ID do projeto base.
     */
    public String delete(long id) {
        this.connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_RESEARCHPROJECT +
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting Research Project: " + e.getMessage();
        } finally {
            connection.closeConnection();
        }

        return "Research Project deleted successfully";
    }

    // --- U P D A T E ---
    
    /**
     * Atualiza os atributos adicionais do projeto de pesquisa.
     */
    public ResearchProject update(long id, ResearchProject researchProject) {
        if (id <= 0) {
            System.out.println("Error updating Research Project: Project ID is missing or invalid.");
            return null;
        }

        this.connection.openConnection();

        String sql = "UPDATE " + ConstantsDataBase.TABLE_RESEARCHPROJECT + " SET "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE + " = ? "
                + "WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {

            st.setString(1, researchProject.getObjective());
            st.setString(2, researchProject.getJustification());
            st.setString(3, researchProject.getDiscipline()); // Usando o getter 'getCourses()'
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
            System.out.println("Error updating Research Project: " + e.getMessage());
            return null;
        } finally {
            connection.closeConnection();
        }
    }

    // --- R E A D ---
               
    /**
     * Busca os atributos adicionais do projeto de pesquisa.
     */
    public ResearchProject read(long id) {
        ResearchProject researchProject  = null;
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_RESEARCHPROJECT +
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?;";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    researchProject = new ResearchProject();
                    // Seta o ID do Projeto Base
                    researchProject.setId(id);
                    // Seta os atributos específicos
                    researchProject.setObjective(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE));
                    researchProject.setJustification(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION));
                    researchProject.setDiscipline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading Research Project: " + e.getMessage());
        } finally {
            connection.closeConnection();
        }

        return researchProject;
    }

    // --- L I S T ---

    /**
     * Lista todos os registros de projetos de pesquisa.
     */
    public List<ResearchProject> listAll() {
        List<ResearchProject> researchProjects = new ArrayList<>();
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_RESEARCHPROJECT + ";";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                ResearchProject researchProject = new ResearchProject();
                // O ID do projeto base também deve ser carregado
                researchProject.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID)); 
                researchProject.setObjective(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_OBJECTIVE));
                researchProject.setJustification(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_JUSTIFICATION));
                researchProject.setDiscipline(rs.getString(ConstantsDataBase.PROJECT_COLUNA_RESEARCH_DISCIPLINE));

                researchProjects.add(researchProject);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error listing Research Projects: " + e.getMessage());
        } finally {
            connection.closeConnection();
        }

        return researchProjects;
    }
} 
    

