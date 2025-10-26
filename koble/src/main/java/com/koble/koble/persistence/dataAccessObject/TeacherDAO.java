



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

//Importing the Teacher model, Crudl interface and MySqlConnection class.
import com.koble.koble.model.Teacher;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class TeacherDAO implements Crudl<Teacher> {
    //Creating a MySqlConnection attributes.
    private final MySqlConnection connection;

    //Class constructor.
    public TeacherDAO(MySqlConnection connection){
        this.connection = connection;
    }

    @Override
    //Method to create a new register in the Database (MySql code for do this action):
    public Teacher create(Teacher teacher) {
        //Oppening the connection to the Database.
        this.connection.openConnection();
        //Insert SQL sentence.
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_TEACHER + " (" + 
                     ConstantsDataBase.TEACHER_COLUNA_SIAPE + ", " + 
                     ConstantsDataBase.COLUMN_EMAIL + ", " + 
                     ConstantsDataBase.COLUMN_NAME + ", " + 
                     ConstantsDataBase.COLUMN_PASSWORD + ", " + 
                     ConstantsDataBase.COLUMN_PHONE + ") VALUES (?, ?, ?, ?, ?)";

        try{
            //Creating a PreparedStatement to execute the SQL sentece.
            //PreparedStatemnt is an interface. Is instantied by an anonymous class.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            st.setString(1, teacher.getSiape());
            st.setString(2, teacher.getEmail());
            st.setString(3, teacher.getName());
            st.setString(4, teacher.getPassword());
            st.setString(5, teacher.getPhoneNumber());

            //Executing the insert operation.
            st.executeUpdate();

        }

        //SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("Error creating teacher: " + e.getMessage()); 
            return null;
        }

        //Closing the connection to the Database.
        //Finally block is always executed,regardless if an exception is thrown or not.
        finally{
            connection.closeConnection();
        }

        System.out.println("Teacher created successfully");
        return teacher;
    }

    @Override
    // Method to delete a register from the Database.
    public String delete(long id) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence.
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_TEACHER +
                " WHERE " + ConstantsDataBase.TEACHER_COLUNA_ID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the teacher's ID).
            st.setLong(1, id); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting teacher: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "Teacher deleted successfully";
    }

    @Override
    // Method to update an existing register in the Database.
    public Teacher update(long id, Teacher teacher) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Update SQL sentence.
        String sql = "UPDATE " + ConstantsDataBase.TABLE_TEACHER +
                " SET " + ConstantsDataBase.TEACHER_COLUNA_SIAPE + "=?, " +
                ConstantsDataBase.COLUMN_EMAIL + "=?, " +
                ConstantsDataBase.COLUMN_NAME + "=?, " +
                ConstantsDataBase.COLUMN_PASSWORD + "=?, " +
                ConstantsDataBase.COLUMN_PHONE + "=? WHERE " +
                ConstantsDataBase.TEACHER_COLUNA_ID + "=?;";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Ensure 'teacher' is not null before accessing its methods.
            if (teacher != null) {
                // Setting the new values of the PreparedStatement.
                st.setString(1, teacher.getSiape());
                st.setString(2, teacher.getEmail());
                st.setString(3, teacher.getName());
                st.setString(4, teacher.getPassword());
                st.setString(5, teacher.getPhoneNumber());
                
                // Setting the value for the WHERE clause (the teacher's ID).
                st.setLong(6, id); 
                
                // Executing the update operation.
                st.executeUpdate();
            } else {
                System.out.println("Error updating teacher: object is null");
                return null;
            }
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating teacher: " + e.getMessage()); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        System.out.println("Teacher updated successfully");
        return teacher;
    }

    @Override
    // Method to read a register from the Database by ID.
    public Teacher read(long id) {
        Teacher teacher = null;
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
         String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_TEACHER +
                " WHERE " + ConstantsDataBase.TEACHER_COLUNA_ID + " = ?";
                
        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the teacher's ID).
            st.setLong(1, id); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Checking if a result was found.
            if (rs.next()) {
                // Instantiating a new Teacher object with the retrieved data.
                teacher = new Teacher();
                teacher.setId(rs.getLong(ConstantsDataBase.TEACHER_COLUNA_ID));
                teacher.setSiape(rs.getString(ConstantsDataBase.TEACHER_COLUNA_SIAPE));
                teacher.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                teacher.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                teacher.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                teacher.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
            }

        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading teacher: " + e.getMessage());
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the Teacher object or null if not found/error.
        return teacher;
    }

    @Override
    // Method to list all registers from the Database.
    public List<Teacher> listAll() {
        // List to store all Teacher objects.
        List<Teacher> teachers = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_TEACHER;

        try {
            // Creating a Statement to execute the SQL sentence (No parameters needed, so a simple Statement is sufficient, 
            // but sticking to PreparedStatement for consistency is also fine).
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new Teacher object for each row.
                Teacher teacher = new Teacher();
                teacher.setId(rs.getLong(ConstantsDataBase.TEACHER_COLUNA_ID));
                teacher.setSiape(rs.getString(ConstantsDataBase.TEACHER_COLUNA_SIAPE));
                teacher.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                teacher.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                teacher.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                teacher.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
                
                // Adding the teacher object to the list.
                teachers.add(teacher);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading teachers: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of teachers (can be empty).
        return teachers;
    }

}