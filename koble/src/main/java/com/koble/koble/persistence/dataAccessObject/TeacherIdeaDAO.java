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

//Importing the TeacherIdea model, Crudl interface and MySqlConnection class.
import com.koble.koble.model.TeacherIdea;
import com.koble.koble.model.Idea;
import com.koble.koble.model.Teacher;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class TeacherIdeaDAO {
    //Creating a MySqlConnection attributes.
    private final MySqlConnection connection;

    //Class constructor.
    public TeacherIdeaDAO(MySqlConnection connection){
        this.connection = connection;
    }

    //Method to create a new register in the Database (MySql code for do this action):
    //Note: This method creates a relationship between teacher and idea in junction table
    public String create(TeacherIdea teacherIdea) {
        //Oppening the connection to the Database.
        this.connection.openConnection();
        //Insert SQL sentence for junction table (no ID needed - composite primary key)
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_TEACHERIDEA + " (" + 
                     ConstantsDataBase.COLUMN_IDEAID + ", " + 
                     ConstantsDataBase.COLUMN_TEACHERID + ") VALUES (?, ?)";

        try{
            //Creating a PreparedStatement to execute the SQL sentece.
            //PreparedStatemnt is an interface. Is instantied by an anonymous class.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            st.setLong(1, teacherIdea.getIdea().getId());
            st.setLong(2, teacherIdea.getTeacher().getId());

            //Executing the insert operation.
            st.executeUpdate();

        }

        //SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e){
            e.printStackTrace();
            return "Error creating teacher idea relationship: " + e.getMessage(); 

        }

        //Closing the connection to the Database.
        //Finally block is always executed,regardless if an exception is thrown or not.
        finally{
            connection.closeConnection();
        }

        return "Teacher idea relationship created successfully";
    }

    //---------------REVIEW THE INSTACIATION LOGIC---------------------------
    // Method to delete a register from the Database.
    // For junction tables, deletion is typically done by the composite key (teacher_id + idea_id)
    public String delete(long teacherId, long ideaId) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence using composite key
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_TEACHERIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_TEACHERID + " = ? AND " +
                ConstantsDataBase.COLUMN_IDEAID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the values of the PreparedStatement (teacher and idea IDs).
            st.setLong(1, teacherId); 
            st.setLong(2, ideaId); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting teacher idea relationship: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "Teacher idea relationship deleted successfully";
    }

    // Method to delete all ideas for a specific teacher
    public String deleteAllByTeacherId(long teacherId) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_TEACHERIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_TEACHERID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (teacher ID).
            st.setLong(1, teacherId); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting teacher ideas: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "All teacher ideas deleted successfully";
    }

    // Method to delete all teachers for a specific idea
    public String deleteAllByIdeaId(long ideaId) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_TEACHERIDEA +
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
            return "Error deleting idea teachers: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "All idea teachers deleted successfully";
    }

    // Method to check if a specific teacher-idea relationship exists
    public boolean exists(long teacherId, long ideaId) {
        boolean exists = false;
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT COUNT(*) FROM " + ConstantsDataBase.TABLE_TEACHERIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_TEACHERID + " = ? AND " +
                ConstantsDataBase.COLUMN_IDEAID + " = ?";
                
        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the values of the PreparedStatement (teacher and idea IDs).
            st.setLong(1, teacherId); 
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
            System.out.println("Error checking teacher idea relationship: " + e.getMessage());
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns true if the relationship exists, false otherwise.
        return exists;
    }
    // Method to list all teacher-idea relationships from the Database.
    public List<TeacherIdea> listAll() {
        // List to store all TeacherIdea objects.
        List<TeacherIdea> teacherIdeas = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_TEACHERIDEA;

        try {
            // Creating a Statement to execute the SQL sentence (No parameters needed, so a simple Statement is sufficient, 
            // but sticking to PreparedStatement for consistency is also fine).
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new TeacherIdea object for each row.
                TeacherIdea teacherIdea = new TeacherIdea();
                
                // Note: These would need corresponding DAO methods to load Idea and Teacher objects
                // For now, creating empty objects - this should be improved with proper loading
                Idea idea = new Idea();
                idea.setId(rs.getLong(ConstantsDataBase.COLUMN_IDEAID));
                teacherIdea.setIdea(idea);
                
                Teacher teacher = new Teacher();
                teacher.setId(rs.getLong(ConstantsDataBase.COLUMN_TEACHERID));
                teacherIdea.setTeacher(teacher);
                
                // Adding the teacherIdea object to the list.
                teacherIdeas.add(teacherIdea);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading teacher ideas: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of teacher ideas (can be empty).
        return teacherIdeas;
    }

    // Additional method to find all ideas by teacher ID
    public List<TeacherIdea> findByTeacherId(long teacherId) {
        // List to store TeacherIdea objects for a specific teacher.
        List<TeacherIdea> teacherIdeas = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_TEACHERIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_TEACHERID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the teacher's ID).
            st.setLong(1, teacherId); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new TeacherIdea object for each row.
                TeacherIdea teacherIdea = new TeacherIdea();
                
                // Note: These would need corresponding DAO methods to load Idea and Teacher objects
                // For now, creating empty objects - this should be improved with proper loading
                Idea idea = new Idea();
                idea.setId(rs.getLong(ConstantsDataBase.COLUMN_IDEAID));
                teacherIdea.setIdea(idea);
                
                Teacher teacher = new Teacher();
                teacher.setId(rs.getLong(ConstantsDataBase.COLUMN_TEACHERID));
                teacherIdea.setTeacher(teacher);
                
                // Adding the teacherIdea object to the list.
                teacherIdeas.add(teacherIdea);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading teacher ideas by teacher ID: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of teacher ideas for the specific teacher (can be empty).
        return teacherIdeas;
    }

    // Additional method to find all teachers by idea ID
    public List<TeacherIdea> findByIdeaId(long ideaId) {
        // List to store TeacherIdea objects for a specific idea.
        List<TeacherIdea> teacherIdeas = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_TEACHERIDEA +
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
                // Instantiating a new TeacherIdea object for each row.
                TeacherIdea teacherIdea = new TeacherIdea();
                
                // Note: These would need corresponding DAO methods to load Idea and Teacher objects
                // For now, creating empty objects - this should be improved with proper loading
                Idea idea = new Idea();
                idea.setId(rs.getLong(ConstantsDataBase.COLUMN_IDEAID));
                teacherIdea.setIdea(idea);
                
                Teacher teacher = new Teacher();
                teacher.setId(rs.getLong(ConstantsDataBase.COLUMN_TEACHERID));
                teacherIdea.setTeacher(teacher);
                
                // Adding the teacherIdea object to the list.
                teacherIdeas.add(teacherIdea);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading teacher ideas by idea ID: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of teacher ideas for the specific idea (can be empty).
        return teacherIdeas;
    }
}
