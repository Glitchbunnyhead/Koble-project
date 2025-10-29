package com.koble.koble.persistence.dataAccessObject;

//Importing Java utilitys.
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.koble.koble.model.Student;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class StudentDAO implements Crudl<Student> {
    //Creating a MySqlConnection attributes.
    private final MySqlConnection connection;

    //Class constructor.
    public StudentDAO(MySqlConnection connection){
        this.connection = connection;
    }

    @Override
    //Method to create a new register in the Database (MySql code for do this action):
    public Student create(Student student) {
        //Oppening the connection to the Database.
        this.connection.openConnection();
        //Insert SQL sentence.
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_STUDENT + " (" + 
                     ConstantsDataBase.STUDENT_COLUNA_ENROLLMENT + ", " + 
                     ConstantsDataBase.COLUMN_NAME + ", " + 
                     ConstantsDataBase.COLUMN_EMAIL + ", " + 
                     ConstantsDataBase.COLUMN_PASSWORD + ", " + 
                     ConstantsDataBase.COLUMN_PHONE + ", " + 
                     ConstantsDataBase.COLUMN_BIRTHDATE + ") VALUES (?, ?, ?, ?, ?, ?)";

        try{
            //Creating a PreparedStatement to execute the SQL sentece.
            //PreparedStatemnt is an interface. Is instantied by an anonymous class.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            st.setString(1, student.getRegistration());
            st.setString(2, student.getName());
            st.setString(3, student.getEmail());
            st.setString(4, student.getPassword());
            st.setString(5, student.getPhoneNumber());
            st.setDate(6, new Date(student.getBirthDate().getTime()));

            //Executing the insert operation.
            st.executeUpdate();

        }

        //SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("Error creating student: " + e.getMessage()); 
            return null;
        }

        //Closing the connection to the Database.
        //Finally block is always executed,regardless if an exception is thrown or not.
        finally{
            connection.closeConnection();
        }

        System.out.println("Student created successfully");
        return student;
    }

    @Override
    // Method to delete a register from the Database.
    public String delete(long id) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence.
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_STUDENT +
                " WHERE " + ConstantsDataBase.STUDENT_COLUNA_ID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the student's ID).
            st.setLong(1, id); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting student: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "Student deleted successfully";
    }

    @Override
    //-----------THE METHOD IT'S NOT WORKING-----------------------------
    // Method to update an existing register in the Database.
    public Student update(long id, Student student) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Update SQL sentence.
        String sql = "UPDATE " + ConstantsDataBase.TABLE_STUDENT +
                " SET " + ConstantsDataBase.STUDENT_COLUNA_ENROLLMENT + "=?, " +
                ConstantsDataBase.COLUMN_NAME + "=?, " +
                ConstantsDataBase.COLUMN_EMAIL + "=?, " +
                ConstantsDataBase.COLUMN_PASSWORD + "=?, " +
                ConstantsDataBase.COLUMN_PHONE + "=?, " +
                ConstantsDataBase.COLUMN_BIRTHDATE + "=? WHERE " +
                ConstantsDataBase.STUDENT_COLUNA_ID + "=?;";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Ensure 'student' is not null before accessing its methods.
            if (student != null) {
                // Setting the new values of the PreparedStatement.
                st.setString(1, student.getRegistration());
                st.setString(2, student.getName());
                st.setString(3, student.getEmail());
                st.setString(4, student.getPassword());
                st.setString(5, student.getPhoneNumber());
                st.setDate(6, new Date(student.getBirthDate().getTime()));
                
                // Setting the value for the WHERE clause (the student's ID).
                st.setLong(7, id); 
                
                // Executing the update operation.
                st.executeUpdate();
            } else {
                System.out.println("Error updating student: object is null");
                return null;
            }
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating student: " + e.getMessage());
            return null ;
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        System.out.println("Student updated successfully");
        return student;
    }

    @Override
    // Method to read a register from the Database by ID.
    public Student read(long id) {
        Student student = null;
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
         String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_STUDENT +
                " WHERE " + ConstantsDataBase.STUDENT_COLUNA_ID + " = ?";
                
        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the student's ID).
            st.setLong(1, id); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Checking if a result was found.
            if (rs.next()) {
                // Instantiating a new Student object with the retrieved data.
                student = new Student();
                student.setId(rs.getLong(ConstantsDataBase.STUDENT_COLUNA_ID));
                student.setRegistration(rs.getString(ConstantsDataBase.STUDENT_COLUNA_ENROLLMENT));
                student.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                student.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                student.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                student.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
                student.setBirthDate(rs.getDate(ConstantsDataBase.COLUMN_BIRTHDATE));
            }

        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading student: " + e.getMessage());
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the Student object or null if not found/error.
        return student;
    }


    @Override
    // Method to list all registers from the Database.
    public List<Student> listAll() {
        // List to store all Student objects.
        List<Student> students = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_STUDENT;

        try {
            // Creating a Statement to execute the SQL sentence (No parameters needed, so a simple Statement is sufficient, 
            // but sticking to PreparedStatement for consistency is also fine).
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new Student object for each row.
                Student student = new Student();
                student.setId(rs.getLong(ConstantsDataBase.STUDENT_COLUNA_ID));
                student.setRegistration(rs.getString(ConstantsDataBase.STUDENT_COLUNA_ENROLLMENT));
                student.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                student.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                student.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                student.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
                student.setBirthDate(rs.getDate(ConstantsDataBase.COLUMN_BIRTHDATE));
                
                // Adding the student object to the list.
                students.add(student);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading students: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of students (can be empty).
        return students;
    }
}
