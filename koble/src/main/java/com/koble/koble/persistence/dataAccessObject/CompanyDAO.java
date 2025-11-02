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

//Importing the Company model, Crudl interface and MySqlConnection class.
import com.koble.koble.model.Company;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class CompanyDAO implements Crudl<Company> {
    //Creating a MySqlConnection attributes.
    private final MySqlConnection connection;

    //Class constructor.
    public CompanyDAO(MySqlConnection connection){
        this.connection = connection;
    }

    @Override
    //Method to create a new register in the Database (MySql code for do this action):
    public Company create(Company company) {
        //Oppening the connection to the Database.
        this.connection.openConnection();
        //Insert SQL sentence.
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_COMPANY +
                " (" + ConstantsDataBase.COMPANY_COLUNA_CNPJ + ", " +
                ConstantsDataBase.COLUMN_NAME + ", " +
                ConstantsDataBase.COLUMN_EMAIL + ", " +
                ConstantsDataBase.COLUMN_PASSWORD + ", " +
                ConstantsDataBase.COLUMN_PHONE + ") VALUES (?, ?, ?, ?, ?)";

        try{
            //Creating a PreparedStatement to execute the SQL sentece.
            //PreparedStatemnt is an interface. Is instantied by an anonymous class.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            //Setting the values of the PreparedStatement(cnpj,name,email,password,phoneNumber).
            st.setString(1, company.getCnpj());
            st.setString(2, company.getName());
            st.setString(3, company.getEmail());
            st.setString(4, company.getPassword());
            st.setString(5, company.getPhoneNumber());

            //Executing the insert operation.
            st.executeUpdate();

        }

        //SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("Error creating company: " + e.getMessage()); 

        }

        //Closing the connection to the Database.
        //Finally block is always executed,regardless if an exception is thrown or not.
        finally{
            connection.closeConnection();
        }

        System.out.println("Company created successfully");
        return company;
    }

    @Override
    // Method to delete a register from the Database.
    public String delete(long id) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence.
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_COMPANY +
                " WHERE " + ConstantsDataBase.COMPANY_COLUNA_ID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the company's ID).
            st.setLong(1, id); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting company: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "Company deleted successfully";
    }


    @Override
    // Method to update an existing register in the Database.
    public Company update(long id, Company company) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Update SQL sentence.
        String sql = "UPDATE " + ConstantsDataBase.TABLE_COMPANY +
                " SET " + ConstantsDataBase.COMPANY_COLUNA_CNPJ + "=?, " +
                ConstantsDataBase.COLUMN_NAME + "=?, " +
                ConstantsDataBase.COLUMN_EMAIL + "=?, " +
                ConstantsDataBase.COLUMN_PASSWORD + "=?, " +
                ConstantsDataBase.COLUMN_PHONE + "=? WHERE " +
                ConstantsDataBase.COMPANY_COLUNA_ID + "=?;";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Ensure 'company' is not null before accessing its methods.
            if (company != null) {
                // Setting the new values of the PreparedStatement.
                st.setString(1, company.getCnpj());
                st.setString(2, company.getName());
                st.setString(3, company.getEmail());
                st.setString(4, company.getPassword());
                st.setString(5, company.getPhoneNumber());
                
                // Setting the value for the WHERE clause (the company's ID).
                st.setLong(6, id); 
                
                // Executing the update operation.
                st.executeUpdate();
            } else {
                System.out.println("Error updating company: company object is null");
                return null;
            }
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating company: " + e.getMessage()); 
            return null;
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        System.out.println ("Company updated successfully");
        return company;
    }


   @Override
    // Method to read a register from the Database by ID.
    public Company read(long id) {
        Company company = null;
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
         String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_COMPANY +
                " WHERE " + ConstantsDataBase.COMPANY_COLUNA_ID + " = ?";
                
        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the company's ID).
            st.setLong(1, id); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Checking if a result was found.
            if (rs.next()) {
                // Instantiating a new Company object with the retrieved data.
                company = new Company();
                company.setId(rs.getLong(ConstantsDataBase.COMPANY_COLUNA_ID));
                company.setCnpj(rs.getString(ConstantsDataBase.COMPANY_COLUNA_CNPJ));
                company.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                company.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                company.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                company.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
            }

        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading company: " + e.getMessage());
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the Company object or null if not found/error.
        return company;
    }


    @Override
    // Method to list all registers from the Database.
    public List<Company> listAll() {
        // List to store all Company objects.
        List<Company> companies = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_COMPANY;

        try {
            // Creating a Statement to execute the SQL sentence (No parameters needed, so a simple Statement is sufficient, 
            // but sticking to PreparedStatement for consistency is also fine).
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new Company object for each row.
                Company company = new Company();
                company.setId(rs.getLong(ConstantsDataBase.COMPANY_COLUNA_ID));
                company.setCnpj(rs.getString(ConstantsDataBase.COMPANY_COLUNA_CNPJ));
                company.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                company.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                company.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                company.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
                
                // Adding the company object to the list.
                companies.add(company);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading company: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of companies (can be empty).
        return companies;
    }



    public boolean exists(long companyId) {
        boolean exists = false;
        this.connection.openConnection();
        String sql = "SELECT 1 FROM " + ConstantsDataBase.TABLE_COMPANY + " WHERE " + ConstantsDataBase.COMPANY_COLUNA_ID + " = ?";
        
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, companyId);
            try (ResultSet rs = st.executeQuery()) {
                exists = rs.next(); // Retorna true se houver pelo menos uma linha
            }
        } catch (SQLException e) {
            System.out.println("Error checking project existence:" + e.getMessage());
            e.printStackTrace();
        } finally {
            this.connection.closeConnection();
        }
        return exists;
}
}


