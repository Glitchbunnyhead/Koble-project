package com.koble.koble.persistence.dataAccessObject;

//Importing Java utilitys.
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.koble.koble.model.ExternalPerson;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class ExternalPersonDAO implements Crudl<ExternalPerson> {
    //Creating a MySqlConnection attributes.
    private final MySqlConnection connection;

    //Class constructor.
    public ExternalPersonDAO(MySqlConnection connection){
        this.connection = connection;
    }

    @Override
    //Method to create a new register in the Database (MySql code for do this action):
    public ExternalPerson create(ExternalPerson externalPerson) {
        //Oppening the connection to the Database.
        this.connection.openConnection();
        //Insert SQL sentence.
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_EXTERNALPERSON + " (" + ConstantsDataBase.COLUMN_NAME + ", " + ConstantsDataBase.COLUMN_EMAIL + ", " + ConstantsDataBase.COLUMN_PASSWORD + ", " + ConstantsDataBase.COLUMN_PHONE + ") VALUES (?, ?, ?, ?)";

        try{
            //Creating a PreparedStatement to execute the SQL sentece.
            //PreparedStatemnt is an interface. Is instantied by an anonymous class.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            st.setString(1, externalPerson.getName());
            st.setString(2, externalPerson.getEmail());
            st.setString(3, externalPerson.getPassword());
            st.setString(4, externalPerson.getPhoneNumber());

            //Executing the insert operation.
            st.executeUpdate();

        }

        //SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("Error creating external user: " + e.getMessage()); 
            return null;
        }

        //Closing the connection to the Database.
        //Finally block is always executed,regardless if an exception is thrown or not.
        finally{
            connection.closeConnection();
        }

        System.out.println("External user created successfully");
        return externalPerson;
    }

    @Override
    // Method to delete a register from the Database.
    public String delete(long id) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence.
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_EXTERNALPERSON +
                " WHERE " + ConstantsDataBase.EXTERNAL_PERSON_COLUNA_ID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the external person's ID).
            st.setLong(1, id); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting external user: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "External user deleted successfully";
    }


    @Override
    // Method to update an existing register in the Database.
    public ExternalPerson update(long id, ExternalPerson externalPerson) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Update SQL sentence.
        String sql = "UPDATE " + ConstantsDataBase.TABLE_EXTERNALPERSON +
                " SET " + ConstantsDataBase.COLUMN_NAME + "=?, " +
                ConstantsDataBase.COLUMN_EMAIL + "=?, " +
                ConstantsDataBase.COLUMN_PASSWORD + "=?, " +
                ConstantsDataBase.COLUMN_PHONE + "=? WHERE " +
                ConstantsDataBase.EXTERNAL_PERSON_COLUNA_ID + "=?;";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Ensure 'externalPerson' is not null before accessing its methods.
            if (externalPerson != null) {
                // Setting the new values of the PreparedStatement.
                st.setString(1, externalPerson.getName());
                st.setString(2, externalPerson.getEmail());
                st.setString(3, externalPerson.getPassword());
                st.setString(4, externalPerson.getPhoneNumber());
                
                // Setting the value for the WHERE clause (the external person's ID).
                st.setLong(5, id); 
                
                // Executing the update operation.
                st.executeUpdate();
            } else {
                System.out.println("Error updating external user: object is null");
            }
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating external user: " + e.getMessage()); 
            return null;
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        System.out.println("External user updated successfully");
        return externalPerson;
    }


   @Override
    // Method to read a register from the Database by ID.
    public ExternalPerson read(long id) {
        ExternalPerson externalPerson = null;
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
         String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_EXTERNALPERSON +
                " WHERE " + ConstantsDataBase.EXTERNAL_PERSON_COLUNA_ID + " = ?";
                
        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the external person's ID).
            st.setLong(1, id); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Checking if a result was found.
            if (rs.next()) {
                // Instantiating a new ExternalPerson object with the retrieved data.
                externalPerson = new ExternalPerson();
                externalPerson.setId(rs.getLong(ConstantsDataBase.EXTERNAL_PERSON_COLUNA_ID));
                externalPerson.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                externalPerson.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                externalPerson.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                externalPerson.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
            }

        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading external user: " + e.getMessage());
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the ExternalPerson object or null if not found/error.
        return externalPerson;
    }


    @Override
    // Method to list all registers from the Database.
    public List<ExternalPerson> listAll() {
        // List to store all ExternalPerson objects.
        List<ExternalPerson> externalPeople = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_EXTERNALPERSON;

        try {
            // Creating a Statement to execute the SQL sentence (No parameters needed, so a simple Statement is sufficient, 
            // but sticking to PreparedStatement for consistency is also fine).
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new ExternalPerson object for each row.
                ExternalPerson externalPerson = new ExternalPerson();
                externalPerson.setId(rs.getLong(ConstantsDataBase.EXTERNAL_PERSON_COLUNA_ID));
                externalPerson.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                externalPerson.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                externalPerson.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                externalPerson.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
                
                // Adding the externalPerson object to the list.
                externalPeople.add(externalPerson);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading external users: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of external people (can be empty).
        return externalPeople;
    }
}


