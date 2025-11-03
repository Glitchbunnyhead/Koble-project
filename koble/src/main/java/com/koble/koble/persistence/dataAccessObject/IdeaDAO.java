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

//Importing the Idea model, Crudl interface and MySqlConnection class.
import com.koble.koble.model.Idea;
import com.koble.koble.model.Student;
import com.koble.koble.model.Teacher;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class IdeaDAO implements Crudl<Idea> {
    //Creating a MySqlConnection attributes and DAOs for related entities.
    private final MySqlConnection connection;
    private final StudentDAO studentDAO;
    private final TeacherDAO teacherDAO;

    //Class constructor with dependency injection for related DAOs.
    public IdeaDAO(MySqlConnection connection, StudentDAO studentDAO, TeacherDAO teacherDAO){
        this.connection = connection;
        this.studentDAO = studentDAO;
        this.teacherDAO = teacherDAO;
    }

    @Override
    //Method to create a new register in the Database (MySql code for do this action):
    public Idea create(Idea idea) {
        //Oppening the connection to the Database.
        this.connection.openConnection();
        //Insert SQL sentence.
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_IDEA + " (" + 
                     ConstantsDataBase.IDEA_COLUNA_PROPOSER + ", " + 
                     ConstantsDataBase.COLUMN_TARGETAUDIENCE + ", " + 
                     ConstantsDataBase.COLUMN_JUSTIFICATION + ", " + 
                     ConstantsDataBase.COLUMN_TITLE + ", " + 
                     ConstantsDataBase.COLUMN_AIM + ", " + 
                     ConstantsDataBase.COLUMN_SUBTITLE + ", " + 
                     ConstantsDataBase.IDEA_COLUNA_AREA + ", " + 
                     ConstantsDataBase.IDEA_COLUNA_DESCRIPTION + ", " + 
                     ConstantsDataBase.IDEA_COLUNA_TYPE + ", " + 
                     ConstantsDataBase.COLUMN_TEACHERID + ", " + 
                     ConstantsDataBase.COLUMN_STUDENTID + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try{
            //Creating a PreparedStatement to execute the SQL sentece.
            //PreparedStatemnt is an interface. Is instantied by an anonymous class.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            st.setString(1, idea.getProposer());
            st.setString(2, idea.getTargetAudience());
            st.setString(3, idea.getJustification());
            st.setString(4, idea.getTitle());
            st.setString(5, idea.getAim());
            st.setString(6, idea.getSubtitle());
            st.setString(7, idea.getArea());
            st.setString(8, idea.getDescription());
            st.setString(9, idea.getType());

            // Teacher and Student are now stored as IDs in the Idea model
            if (idea.getTeacherId() > 0) {
                st.setLong(10, idea.getTeacherId());
            } else {
                st.setNull(10, java.sql.Types.BIGINT);
            }

            if (idea.getStudentId() > 0) {
                st.setLong(11, idea.getStudentId());
            } else {
                st.setNull(11, java.sql.Types.BIGINT);
            }

            //Executing the insert operation.
            st.executeUpdate();

        }

        //SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("Error creating idea: " + e.getMessage()); 
            return null;

        }

        //Closing the connection to the Database.
        //Finally block is always executed,regardless if an exception is thrown or not.
        finally{
            connection.closeConnection();
        }

        System.out.println ("Idea created successfully");
        return idea;
    }

    @Override
    // Method to delete a register from the Database.
    public String delete(long id) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence.
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_IDEA +
                " WHERE " + ConstantsDataBase.IDEA_COLUNA_ID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the idea's ID).
            st.setLong(1, id); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting idea: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "Idea deleted successfully";
    }

    @Override
    // Method to update an existing register in the Database.
    public Idea update(long id, Idea idea) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Update SQL sentence.
        String sql = "UPDATE " + ConstantsDataBase.TABLE_IDEA +
                " SET " + ConstantsDataBase.IDEA_COLUNA_PROPOSER + "=?, " +
                ConstantsDataBase.COLUMN_TARGETAUDIENCE + "=?, " +
                ConstantsDataBase.COLUMN_JUSTIFICATION + "=?, " +
                ConstantsDataBase.COLUMN_TITLE + "=?, " +
                ConstantsDataBase.COLUMN_AIM + "=?, " +
                ConstantsDataBase.COLUMN_SUBTITLE + "=?, " +
                ConstantsDataBase.IDEA_COLUNA_AREA + "=?, " +
                ConstantsDataBase.IDEA_COLUNA_DESCRIPTION + "=?, " +
                ConstantsDataBase.IDEA_COLUNA_TYPE + "=?, " +
                ConstantsDataBase.COLUMN_TEACHERID + "=?, " +
                ConstantsDataBase.COLUMN_STUDENTID + "=? WHERE " +
                ConstantsDataBase.IDEA_COLUNA_ID + "=?;";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Ensure 'idea' is not null before accessing its methods.
            if (idea != null) {
                // Setting the new values of the PreparedStatement.
                st.setString(1, idea.getProposer());
                st.setString(2, idea.getTargetAudience());
                st.setString(3, idea.getJustification());
                st.setString(4, idea.getTitle());
                st.setString(5, idea.getAim());
                st.setString(6, idea.getSubtitle());
                st.setString(7, idea.getArea());
                st.setString(8, idea.getDescription());
                st.setString(9, idea.getType());
                
                // Teacher and Student are now stored as IDs in the Idea model
                if (idea.getTeacherId() > 0) {
                    st.setLong(10, idea.getTeacherId());
                } else {
                    st.setNull(10, java.sql.Types.BIGINT);
                }

                if (idea.getStudentId() > 0) {
                    st.setLong(11, idea.getStudentId());
                } else {
                    st.setNull(11, java.sql.Types.BIGINT);
                }
                
                // Setting the value for the WHERE clause (the idea's ID).
                st.setLong(12, id); 
                
                // Executing the update operation.
                st.executeUpdate();
            } else {
                System.out.println("Error updating idea: object is null");
                return null;
            }
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating idea: " + e.getMessage()); 
            return null;
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        System.out.println("Idea updated successfully");
        return idea;
    }

    @Override
    // Method to read a register from the Database by ID.
    public Idea read(long id) {
        Idea idea = null;
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
         String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_IDEA +
                " WHERE " + ConstantsDataBase.IDEA_COLUNA_ID + " = ?";
                
        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the idea's ID).
            st.setLong(1, id); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Checking if a result was found.
            if (rs.next()) {
                // Instantiating a new Idea object with the retrieved data.
                idea = new Idea();
                idea.setId(rs.getLong(ConstantsDataBase.IDEA_COLUNA_ID));
                idea.setProposer(rs.getString(ConstantsDataBase.IDEA_COLUNA_PROPOSER));
                idea.setTargetAudience(rs.getString(ConstantsDataBase.COLUMN_TARGETAUDIENCE));
                idea.setJustification(rs.getString(ConstantsDataBase.COLUMN_JUSTIFICATION));
                idea.setTitle(rs.getString(ConstantsDataBase.COLUMN_TITLE));
                idea.setAim(rs.getString(ConstantsDataBase.COLUMN_AIM));
                idea.setSubtitle(rs.getString(ConstantsDataBase.COLUMN_SUBTITLE));
                idea.setArea(rs.getString(ConstantsDataBase.IDEA_COLUNA_AREA));
                idea.setDescription(rs.getString(ConstantsDataBase.IDEA_COLUNA_DESCRIPTION));
                idea.setType(rs.getString(ConstantsDataBase.IDEA_COLUNA_TYPE));
                
                // Note: These would need corresponding DAO methods to load Teacher and Student objects
                // For now, creating empty objects if IDs are present - this should be improved with proper loading
                long teacherId = rs.getLong(ConstantsDataBase.COLUMN_TEACHERID);
                if (!rs.wasNull()) {
                    idea.setTeacherId(teacherId);
                }

                long studentId = rs.getLong(ConstantsDataBase.COLUMN_STUDENTID);
                if (!rs.wasNull()) {
                    idea.setStudentId(studentId);
                }
            }

        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading idea: " + e.getMessage());
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the Idea object or null if not found/error.
        return idea;
    }

    @Override
    // Method to list all registers from the Database.
    public List<Idea> listAll() {
        // List to store all Idea objects.
        List<Idea> ideas = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_IDEA + ";";

        try {
            // Creating a Statement to execute the SQL sentence (No parameters needed, so a simple Statement is sufficient, 
            // but sticking to PreparedStatement for consistency is also fine).
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new Idea object for each row.
                Idea idea = new Idea();
                idea.setId(rs.getLong(ConstantsDataBase.IDEA_COLUNA_ID));
                idea.setProposer(rs.getString(ConstantsDataBase.IDEA_COLUNA_PROPOSER));
                idea.setTargetAudience(rs.getString(ConstantsDataBase.COLUMN_TARGETAUDIENCE));
                idea.setJustification(rs.getString(ConstantsDataBase.COLUMN_JUSTIFICATION));
                idea.setTitle(rs.getString(ConstantsDataBase.COLUMN_TITLE));
                idea.setAim(rs.getString(ConstantsDataBase.COLUMN_AIM));
                idea.setSubtitle(rs.getString(ConstantsDataBase.COLUMN_SUBTITLE));
                idea.setArea(rs.getString(ConstantsDataBase.IDEA_COLUNA_AREA));
                idea.setDescription(rs.getString(ConstantsDataBase.IDEA_COLUNA_DESCRIPTION));
                idea.setType(rs.getString(ConstantsDataBase.IDEA_COLUNA_TYPE));
                
                // Note: These would need corresponding DAO methods to load Teacher and Student objects
                // For now, creating empty objects if IDs are present - this should be improved with proper loading
                long teacherId = rs.getLong(ConstantsDataBase.COLUMN_TEACHERID);
                if (!rs.wasNull()) {
                    idea.setTeacherId(teacherId);
                }

                long studentId = rs.getLong(ConstantsDataBase.COLUMN_STUDENTID);
                if (!rs.wasNull()) {
                    idea.setStudentId(studentId);
                }
                
                // Adding the idea object to the list.
                ideas.add(idea);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading ideas: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of ideas (can be empty).
        return ideas;
    }

    /**
     * Convenience: return a Student by studentId value.
     */
    public Student getStudentById(long studentId) {
        if (studentId <= 0) return null;
        return studentDAO.read(studentId);
    }

    /**
     * Convenience: return a Teacher by teacherId value.
     */
    public Teacher getTeacherById(long teacherId) {
        if (teacherId <= 0) return null;
        return teacherDAO.read(teacherId);
    }

    /**
     * Return the Student associated with the given Idea (by idea.getStudentId()).
     */
    public Student getStudentByIdea(Idea idea) {
        if (idea == null) return null;
        return getStudentById(idea.getStudentId());
    }

    /**
     * Return the Teacher associated with the given Idea (by idea.getTeacherId()).
     */
    public Teacher getTeacherByIdea(Idea idea) {
        if (idea == null) return null;
        return getTeacherById(idea.getTeacherId());
    }


}
