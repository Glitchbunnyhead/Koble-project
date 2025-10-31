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
    
    // A conexão ainda é injetada
    private final MySqlConnection connection;

    public ProjectDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    // --- C R E A T E ---
    @Override
    public Project create(Project project) {
        this.connection.openConnection(); 
        // --- C R E A T E ---
    
  // >>>>>>>>>> BLOCO DE DEBUG COMPLETO <<<<<<<<<<
    System.out.println("--- DEBUG ProjectDAO Create (15 Campos) ---");
    // Ordem 1 (STRING): timeline
    System.out.println("1. TIMELINE: " + project.getTimeline()); 
    // Ordem 2 (STRING): external_link
    System.out.println("2. EXTERNAL_LINK: " + project.getExternalLink());
    // Ordem 3 (STRING): duration (NOT NULL)
    System.out.println("3. DURATION: " + project.getDuration());
    // Ordem 4 (STRING): image
    System.out.println("4. IMAGE: " + project.getImage());
    // Ordem 5 (STRING): complementary_hours
    System.out.println("5. COMPLEMENTARY_HOURS: " + project.getComplementHours());
    // Ordem 6 (BOOLEAN): scholarship_available (NOT NULL)
    System.out.println("6. SCHOLARSHIP_AVAILABLE: " + project.isScholarshipAvailable()); 
    // Ordem 7 (STRING): scholarship_type
    System.out.println("7. SCHOLARSHIP_TYPE: " + project.getScholarshipType());
    // Ordem 8 (DOUBLE/DECIMAL): salary
    System.out.println("8. SALARY: " + project.getSalary());
    // Ordem 9 (STRING): requirements (NOT NULL)
    System.out.println("9. REQUIREMENTS: " + project.getRequirements());
    // Ordem 10 (INT): scholarship_quantity
    System.out.println("10. SCHOLARSHIP_QUANTITY: " + project.getScholarshipQuantity());
    // Ordem 11 (STRING): title (NOT NULL)
    System.out.println("11. TITLE: " + project.getTitle());
    // Ordem 12 (STRING): subtitle (NOT NULL)
    System.out.println("12. SUBTITLE: " + project.getSubtitle());
    // Ordem 13 (STRING): coordinator (NOT NULL)
    System.out.println("13. COORDINATOR: " + project.getCoordinator());
    // Ordem 14 (STRING): description (NOT NULL)
    System.out.println("14. DESCRIPTION: " + project.getDescription());
    // Ordem 15 (ENUM/STRING): project_type (NOT NULL)
    System.out.println("15. TYPE: " + project.getType());
    System.out.println("----------------------------------------------");
    // >>>>>>>>>> FIM BLOCO DE DEBUG COMPLETO <<<<<<<<<<


    // ... (restante do SQL) ...
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

        // Usando try-with-resources para garantir o fechamento de PreparedStatement/ResultSet
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            st.setString(1, project.getTimeline());
            st.setString(2, project.getExternalLink()); // Usando getLinkExtension() conforme a model
            st.setString(3, project.getDuration());
            st.setString(4, project.getImage());
            st.setString(5, project.getComplementHours());
            st.setBoolean(6, project.isScholarshipAvailable()); // Usando isScholarship() conforme a model
            st.setString(7, project.getScholarshipType());
            st.setDouble(8, project.getSalary());
            st.setString(9, project.getRequirements());
            st.setInt(10, project.getScholarshipQuantity());
            st.setString(11, project.getTitle());
            st.setString(12, project.getSubtitle());
            st.setString(13, project.getCoordinator()); // Usando getCoordenator() conforme a model
            st.setString(14, project.getDescription());
            st.setString(15, project.getType());

            int affectedRows = st.executeUpdate();

            try (ResultSet generatedKeys = st.getGeneratedKeys()) { 
            // ... (sua lógica para obter o ID gerado)
            if (generatedKeys.next()) {
                long generatedId = generatedKeys.getLong(1);
                project.setId(generatedId);
            } else {
                throw new SQLException("Creating project failed, no ID obtained.");
            }

            System.out.println("Project created successfully with ID: " + project.getId());
            return project;
        }
        } catch (SQLException e) {
            System.out.println("Error creating project: " + e.getMessage()); // Mensagem de erro do banco
            e.printStackTrace(); // Stack trace completo
            throw new RuntimeException("Database error during project creation.", e);
        } 
         finally { connection.closeConnection(); }
    
    }


    // --- D E L E T E ---
    @Override
    public String delete(long id) {
        // REMOVIDO: this.connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_PROJECT + 
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            st.executeUpdate();
            return "Project deleted successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            // Lançamos a exceção aqui também para que a transação possa ser revertida
            throw new RuntimeException("Database error during project deletion.", e);
        } 
        // REMOVIDO: finally { connection.closeConnection(); }
    }

    // --- U P D A T E ---
    @Override
    public Project update(long id, Project project) {
        if (id <= 0) {
            System.out.println("Error updating project: Project ID is missing or invalid.");
            return null;
        }
        
        // REMOVIDO: this.connection.openConnection();
        
        String sql = "UPDATE " + ConstantsDataBase.TABLE_PROJECT + " SET "
                + ConstantsDataBase.PROJECT_COLUNA_TIMELINE + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_EXTERNAL_LINK + " = ?, "
                // ... (restante das colunas) ...
                + ConstantsDataBase.PROJECT_COLUNA_TYPE + " = ? "
                + "WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

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

            if (rowsAffected > 0) {
                System.out.println("Project updated successfully with ID: " + id);
                return project;
            } else {
                System.out.println("Project with ID " + id + " not found for update.");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error during project update.", e);
        }
        // REMOVIDO: finally { connection.closeConnection(); }
    }

    // --- R E A D ---
    @Override
    public Project read(long id) {
        Project project = null;
        // REMOVIDO: this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + 
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?;";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
        
            st.setLong(1, id);
            try(ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    project = createProjectFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error during project reading.", e);
        } 
        // REMOVIDO: finally { connection.closeConnection(); }

        return project;
    }

    // --- L I S T ALL ---
    @Override
    public List<Project> listAll() {
        List<Project> projects = new ArrayList<>();
        // REMOVIDO: this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PROJECT + ";";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) { // Usando try-with-resources para RS também

            while (rs.next()) {
                Project project = createProjectFromResultSet(rs);
                projects.add(project);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error during project listing.", e);
        } 
        // REMOVIDO: finally { // ... close connection and statements }

        return projects;
    }


    // --- H E L P E R ---
    private Project createProjectFromResultSet(ResultSet rs) throws SQLException {
        // (Lógica de polimorfismo na leitura mantida da sua correção anterior)
        String projectType = rs.getString(ConstantsDataBase.PROJECT_COLUNA_TYPE);
        Project project;
        
        if ("Research".equalsIgnoreCase(projectType)) {
            project = new ResearchProject();
        } else if ("Teaching".equalsIgnoreCase(projectType)) {
            project = new EducationalProject();
        } else if ("Extension".equalsIgnoreCase(projectType)) {
            project = new ExtensionProject();
        } else {
            throw new SQLException("Tipo de projeto desconhecido no banco de dados: " + projectType);
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
        project.setType(projectType);
        
        return project;
    }
}