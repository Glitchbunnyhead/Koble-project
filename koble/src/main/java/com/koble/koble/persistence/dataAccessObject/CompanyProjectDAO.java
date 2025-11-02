
package com.koble.koble.persistence.dataAccessObject;

//Importing Java utilitys.
import java.util.ArrayList;
import java.util.List;

//Importing Java SQL classes for database operations.
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Importing Spring's Repository annotation to indicate that this class is a DAO component.
import org.springframework.stereotype.Repository;

//Importing the CompanyProject model, and MySqlConnection class.
import com.koble.koble.model.CompanyProject;
import com.koble.koble.model.Project;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;
import com.koble.koble.model.Company;;

@Repository
public class CompanyProjectDAO {
    //Creating a MySqlConnection attributes.
    private final MySqlConnection connection;
    private final ProjectDAO projectDAO;
    private final CompanyDAO companyDAO;
    //Class constructor.
    public CompanyProjectDAO(MySqlConnection connection, ProjectDAO projectDAO, CompanyDAO companyDAO){
        this.connection = connection;
        this.projectDAO = projectDAO;
        this.companyDAO = companyDAO;

    }

    //Method to create a new register in the Database (MySql code for do this action):
    //Note: This method creates a relationship between company and project in junction table
    public CompanyProject create(CompanyProject companyProject) {
        //Oppening the connection to the Database.
        this.connection.openConnection();
        //Insert SQL sentence for junction table (no ID needed - composite primary key)
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_COMPANYPROJECT + " (" + 
                     "company_id, " + 
                     "project_id) VALUES (?, ?)";

        try{
            //Creating a PreparedStatement to execute the SQL sentece.
            //PreparedStatemnt is an interface. Is instantied by an anonymous class.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            st.setLong(1, companyProject.getCompanyId());
            st.setLong(2, companyProject.getProjectId());

            //Executing the insert operation.
            st.executeUpdate();

        }

        //SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("Error creating company project relationship: " + e.getMessage()); 
            return null;
        }

        //Closing the connection to the Database.
        //Finally block is always executed,regardless if an exception is thrown or not.
        finally{
            connection.closeConnection();
        }

        System.out.println("Company project relationship created successfully");
        return companyProject;
    }

    // Method to delete a register from the Database.
    // For junction tables, deletion is typically done by the composite key (company_id + project_id)
    public String delete(long companyId, long projectId) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence using composite key
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT +
                " WHERE company_id = ? AND " +
                "project_id = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the values of the PreparedStatement (company and project IDs).
            st.setLong(1, companyId); 
            st.setLong(2, projectId); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting company project relationship: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "Company project relationship deleted successfully";
    }

    // Method to delete all projects for a specific company
    public String deleteAllByCompanyId(long companyId) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT +
                " WHERE company_id = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (company ID).
            st.setLong(1, companyId); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting company projects: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "All company projects deleted successfully";
    }

    // Method to delete all companies for a specific project
    public String deleteAllByProjectId(long projectId) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT +
                " WHERE project_id = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (project ID).
            st.setLong(1, projectId); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting project companies: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "All project companies deleted successfully";
    }

    // Method to check if a specific company-project relationship exists
    public boolean exists(long companyId, long projectId) {
        boolean exists = false;
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT COUNT(*) FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT +
                " WHERE company_id = ? AND " +
                "project_id = ?";
                
        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the values of the PreparedStatement (company and project IDs).
            st.setLong(1, companyId); 
            st.setLong(2, projectId); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Checking if the relationship exists.
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }

        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error checking company project relationship: " + e.getMessage());
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns true if the relationship exists, false otherwise.
        return exists;
    }

        // Method to list all company-project relationships from the Database.
    public List<CompanyProject> listAll() {
        // List to store all CompanyProject objects.
        List<CompanyProject> companyProjects = new ArrayList<>();
        // Opening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT;

        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                CompanyProject companyProject = new CompanyProject();
                companyProject.setCompanyId(rs.getLong("company_id"));
                companyProject.setProjectId(rs.getLong("project_id"));
                companyProjects.add(companyProject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading company projects: " + e.getMessage());        
        } finally {
            connection.closeConnection();
        }

        return companyProjects;
    }

    // Additional method to find all projects by company ID
 public List<Project> findProjectsByCompanyId(long companyId) {
    List<Project> projects = new ArrayList<>();
    this.connection.openConnection();
    
    String sql = "SELECT " + ConstantsDataBase.PROJECT_COLUNA_ID + " FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT +
            " WHERE company_id = ?;";

    try {
        PreparedStatement st = connection.getConnection().prepareStatement(sql);

        // O PreparedStatement também deveria idealmente estar em um try-with-resources,
        // mas focando apenas nas chaves do seu trecho:
        st.setLong(1, companyId);

        try (ResultSet rs = st.executeQuery()) {
        // 2. Iterar sobre todos os project_id encontrados
        while (rs.next()) {
            long projectId = rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID); // *Ajuste: usar a constante para o nome da coluna é uma boa prática*
            
            
            Project project = projectDAO.read(projectId);
            
            // 4. Adicionar à lista se o projeto foi encontrado (pode ser null se o projeto original foi deletado)
            if (project != null) {
                projects.add(project);
            }
        } // <--- Fim do bloco 'try' do ResultSet (try-with-resources)
        } // <--- Fechamento do 'try-with-resources'

    } catch (SQLException e) { // <--- Início do 'catch' para o 'try' principal
        e.printStackTrace();
        System.out.println("Error reading projects by company ID: " + e.getMessage());        
    } 
    finally {
        connection.closeConnection();
    }
 
    return projects;
}


    // Additional method to find all companies by project ID
    public List<Company> findCompaniesByProjectId(long projectId) {
    List<Company> companies = new ArrayList<>();
    this.connection.openConnection();
        
    String sql = "SELECT " + ConstantsDataBase.COMPANY_COLUNA_ID + " FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT +
            " WHERE project_id = ?;";

    try {
        PreparedStatement st = connection.getConnection().prepareStatement(sql);
        
        st.setLong(1, projectId);

        // 2. Usar try-with-resources para o ResultSet: garante que ele será fechado.
        try (ResultSet rs = st.executeQuery()) {
            
            // 3. Iterar e processar DENTRO do escopo do ResultSet aberto
            while (rs.next()) {
                long companyId = rs.getLong(ConstantsDataBase.COMPANY_COLUNA_ID); 
                
                
                Company company = companyDAO.read(companyId);
                
                if (company != null) {
                    companies.add(company);
                }
            }
        } 
        // Nota: O PreparedStatement 'st' é fechado automaticamente pelo try-with-resources.

    } catch (SQLException e) { 
        e.printStackTrace();
        // Ajuste a mensagem para refletir a busca correta
        System.out.println("Error listing companies by project ID: " + e.getMessage());        
        // Relança a exceção como RuntimeException para o Spring gerenciar

    } finally {
        // 4. FECHAR CONEXÃO: Fecha após todo o processamento
        this.connection.closeConnection();
    }
    
    return companies;
}
}
