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

//Importing the StudentIdea model, and MySqlConnection class.
import com.koble.koble.model.StudentIdea;
import com.koble.koble.model.Idea;
import com.koble.koble.model.Student;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class StudentIdeaDAO {
    //Creating a MySqlConnection attributes.
    private final MySqlConnection connection;

    //Class constructor.
    public StudentIdeaDAO(MySqlConnection connection){
        this.connection = connection;
    }

    //Method to create a new register in the Database (MySql code for do this action):
    //Note: This method creates a relationship between student and idea in junction table
    public String create(StudentIdea studentIdea) {
        //Oppening the connection to the Database.
        this.connection.openConnection();
        //Insert SQL sentence for junction table (no ID needed - composite primary key)
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_STUDENTIDEA + " (" + 
                     ConstantsDataBase.COLUMN_IDEAID + ", " + 
                     ConstantsDataBase.COLUMN_STUDENTID + ") VALUES (?, ?)";

        try{
            //Creating a PreparedStatement to execute the SQL sentece.
            //PreparedStatemnt is an interface. Is instantied by an anonymous class.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            st.setLong(1, studentIdea.getIdea().getId());
            st.setLong(2, studentIdea.getStudent().getId());

            //Executing the insert operation.
            st.executeUpdate();

        }

        //SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e){
            e.printStackTrace();
            return "Error creating student idea relationship: " + e.getMessage(); 

        }

        //Closing the connection to the Database.
        //Finally block is always executed,regardless if an exception is thrown or not.
        finally{
            connection.closeConnection();
        }

        return "Student idea relationship created successfully";
    }

    //---------------REVIEW THE INSTACIATION LOGIC---------------------------
    // Method to delete a register from the Database.
    // For junction tables, deletion is typically done by the composite key (student_id + idea_id)
    public String delete(long studentId, long ideaId) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence using composite key
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_STUDENTID + " = ? AND " +
                ConstantsDataBase.COLUMN_IDEAID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the values of the PreparedStatement (student and idea IDs).
            st.setLong(1, studentId); 
            st.setLong(2, ideaId); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting student idea relationship: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "Student idea relationship deleted successfully";
    }

    // Method to delete all ideas for a specific student
    public String deleteAllByStudentId(long studentId) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_STUDENTID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (student ID).
            st.setLong(1, studentId); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting student ideas: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "All student ideas deleted successfully";
    }

    // Method to delete all students for a specific idea
    public String deleteAllByIdeaId(long ideaId) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_IDEAID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (idea ID).
            st.setLong(1, ideaId); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting idea students: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "All idea students deleted successfully";
    }

    // Method to check if a specific student-idea relationship exists
    public boolean exists(long studentId, long ideaId) {
        boolean exists = false;
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT COUNT(*) FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_STUDENTID + " = ? AND " +
                ConstantsDataBase.COLUMN_IDEAID + " = ?";
                
        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the values of the PreparedStatement (student and idea IDs).
            st.setLong(1, studentId); 
            st.setLong(2, ideaId); 
            
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
            System.out.println("Error checking student idea relationship: " + e.getMessage());
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns true if the relationship exists, false otherwise.
        return exists;
    }

    // Method to list all student-idea relationships from the Database.
    public List<StudentIdea> listAll() {
        // List to store all StudentIdea objects.
        List<StudentIdea> studentIdeas = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_STUDENTIDEA;

        try {
            // Creating a Statement to execute the SQL sentence (No parameters needed, so a simple Statement is sufficient, 
            // but sticking to PreparedStatement for consistency is also fine).
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new StudentIdea object for each row.
                StudentIdea studentIdea = new StudentIdea();
                
                // Note: These would need corresponding DAO methods to load Idea and Student objects
                // For now, creating empty objects - this should be improved with proper loading
                Idea idea = new Idea();
                idea.setId(rs.getLong(ConstantsDataBase.COLUMN_IDEAID));
                studentIdea.setIdea(idea);
                
                Student student = new Student();
                student.setId(rs.getLong(ConstantsDataBase.COLUMN_STUDENTID));
                studentIdea.setStudent(student);
                
                // Adding the studentIdea object to the list.
                studentIdeas.add(studentIdea);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading student ideas: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of student ideas (can be empty).
        return studentIdeas;
    }

    // Additional method to find all ideas by student ID
    public List<StudentIdea> findByStudentId(long studentId) {
        // List to store StudentIdea objects for a specific student.
        List<StudentIdea> studentIdeas = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_STUDENTID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the student's ID).
            st.setLong(1, studentId); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new StudentIdea object for each row.
                StudentIdea studentIdea = new StudentIdea();
                
                // Note: These would need corresponding DAO methods to load Idea and Student objects
                // For now, creating empty objects - this should be improved with proper loading
                Idea idea = new Idea();
                idea.setId(rs.getLong(ConstantsDataBase.COLUMN_IDEAID));
                studentIdea.setIdea(idea);
                
                Student student = new Student();
                student.setId(rs.getLong(ConstantsDataBase.COLUMN_STUDENTID));
                studentIdea.setStudent(student);
                
                // Adding the studentIdea object to the list.
                studentIdeas.add(studentIdea);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading student ideas by student ID: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of student ideas for the specific student (can be empty).
        return studentIdeas;
    }

    // Additional method to find all students by idea ID
    public List<StudentIdea> findByIdeaId(long ideaId) {
        // List to store StudentIdea objects for a specific idea.
        List<StudentIdea> studentIdeas = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_IDEAID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the idea's ID).
            st.setLong(1, ideaId); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new StudentIdea object for each row.
                StudentIdea studentIdea = new StudentIdea();
                
                // Note: These would need corresponding DAO methods to load Idea and Student objects
                // For now, creating empty objects - this should be improved with proper loading
                Idea idea = new Idea();
                idea.setId(rs.getLong(ConstantsDataBase.COLUMN_IDEAID));
                studentIdea.setIdea(idea);
                
                Student student = new Student();
                student.setId(rs.getLong(ConstantsDataBase.COLUMN_STUDENTID));
                studentIdea.setStudent(student);
                
                // Adding the studentIdea object to the list.
                studentIdeas.add(studentIdea);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading student ideas by idea ID: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of student ideas for the specific idea (can be empty).
        return studentIdeas;
    }
}
