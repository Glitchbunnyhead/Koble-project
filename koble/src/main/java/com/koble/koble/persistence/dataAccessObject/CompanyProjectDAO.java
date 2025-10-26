
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
import com.koble.koble.model.Company;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class CompanyProjectDAO {
    //Creating a MySqlConnection attributes.
    private final MySqlConnection connection;

    //Class constructor.
    public CompanyProjectDAO(MySqlConnection connection){
        this.connection = connection;
    }

    //Method to create a new register in the Database (MySql code for do this action):
    //Note: This method creates a relationship between company and project in junction table
    public String create(CompanyProject companyProject) {
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
            
            st.setLong(1, companyProject.getCompany().getId());
            st.setLong(2, companyProject.getProject().getId());

            //Executing the insert operation.
            st.executeUpdate();

        }

        //SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e){
            e.printStackTrace();
            return "Error creating company project relationship: " + e.getMessage(); 

        }

        //Closing the connection to the Database.
        //Finally block is always executed,regardless if an exception is thrown or not.
        finally{
            connection.closeConnection();
        }

        return "Company project relationship created successfully";
    }

    //---------------REVIEW THE INSTACIATION LOGIC---------------------------
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
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT;

        try {
            // Creating a Statement to execute the SQL sentence (No parameters needed, so a simple Statement is sufficient, 
            // but sticking to PreparedStatement for consistency is also fine).
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new CompanyProject object for each row.
                CompanyProject companyProject = new CompanyProject();
                
                // Note: These would need corresponding DAO methods to load Company and Project objects
                // For now, creating empty objects - this should be improved with proper loading
                Company company = new Company();
                company.setId(rs.getLong("company_id"));
                companyProject.setCompany(company);
                
                // Cannot instantiate abstract Project class directly
                // This should be improved to load actual project via ProjectDAO
                companyProject.setProject(null);
                
                // Adding the companyProject object to the list.
                companyProjects.add(companyProject);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading company projects: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of company projects (can be empty).
        return companyProjects;
    }

    // Additional method to find all projects by company ID
    public List<CompanyProject> findProjectsByCompanyId(long companyId) {
        // List to store CompanyProject objects for a specific company.
        List<CompanyProject> companyProjects = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT +
                " WHERE company_id = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the company's ID).
            st.setLong(1, companyId); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new CompanyProject object for each row.
                CompanyProject companyProject = new CompanyProject();
                
                // Note: These would need corresponding DAO methods to load Company and Project objects
                // For now, creating empty objects - this should be improved with proper loading
                Company company = new Company();
                company.setId(rs.getLong("company_id"));
                companyProject.setCompany(company);
                
                // Cannot instantiate abstract Project class directly
                // This should be improved to load actual project via ProjectDAO
                companyProject.setProject(null);
                
                // Adding the companyProject object to the list.
                companyProjects.add(companyProject);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading projects by company ID: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of company projects for the specific company (can be empty).
        return companyProjects;
    }

    // Additional method to find all companies by project ID
    public List<CompanyProject> findCompaniesByProjectId(long projectId) {
        // List to store CompanyProject objects for a specific project.
        List<CompanyProject> companyProjects = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_COMPANYPROJECT +
                " WHERE project_id = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the project's ID).
            st.setLong(1, projectId); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new CompanyProject object for each row.
                CompanyProject companyProject = new CompanyProject();
                
                // Note: These would need corresponding DAO methods to load Company and Project objects
                // For now, creating empty objects - this should be improved with proper loading
                Company company = new Company();
                company.setId(rs.getLong("company_id"));
                companyProject.setCompany(company);
                
                // Cannot instantiate abstract Project class directly
                // This should be improved to load actual project via ProjectDAO
                companyProject.setProject(null);
                
                // Adding the companyProject object to the list.
                companyProjects.add(companyProject);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading companies by project ID: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of company projects for the specific project (can be empty).
        return companyProjects;
    }
}