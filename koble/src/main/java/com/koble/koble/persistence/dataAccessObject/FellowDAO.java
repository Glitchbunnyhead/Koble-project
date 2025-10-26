
package com.koble.koble.persistence.dataAccessObject;

//Importing Java utilitys.
import java.util.ArrayList;
import java.util.List;

//Importing Java SQL classes for database operations.
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

//Importing Spring's Repository annotation to indicate that this class is a DAO component.
import org.springframework.stereotype.Repository;

//Importing the Fellow model, Crudl interface and MySqlConnection class.
import com.koble.koble.model.Fellow;
import com.koble.koble.model.Student;
import com.koble.koble.model.Project;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class FellowDAO implements Crudl<Fellow> {
    //Creating a MySqlConnection attributes.
    private final MySqlConnection connection;
    private final StudentDAO studentDAO;
    private final ProjectDAO projectDAO;

    //Class constructor with dependency injection.
    public FellowDAO(MySqlConnection connection, StudentDAO studentDAO, ProjectDAO projectDAO){
        this.connection = connection;
        this.studentDAO = studentDAO;
        this.projectDAO = projectDAO;
    }

    @Override
    //Method to create a new register in the Database (MySql code for do this action):
    public Fellow create(Fellow fellow) {
        // Validate input
        if (fellow == null) {
            System.out.println("Error creating fellow: Fellow object is null");
            return null;
        }
        if (fellow.getStudent() == null) {
            System.out.println("Error creating fellow: Student is required");
            return null;

        }
        
        if (fellow.getProject() == null) {
            System.out.println("Error creating fellow: Project is required");
            return null;
        }
        
        //Opening the connection to the Database.
        this.connection.openConnection();
        //Insert SQL sentence.
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_FELLOW + " (" + 
                     ConstantsDataBase.COLUMN_STUDENTID + ", " + 
                     ConstantsDataBase.COLUMN_PROJECTID + ", " + 
                     ConstantsDataBase.COLUMN_CPF + ", " + 
                     ConstantsDataBase.COLUMN_LATTESCURRICULUM + ", " + 
                     ConstantsDataBase.COLUMN_BIRTHDATE + ") VALUES (?, ?, ?, ?, ?)";

        try{
            //Creating a PreparedStatement to execute the SQL sentence.
            //PreparedStatement is an interface. Is instantiated by an anonymous class.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            st.setLong(1, fellow.getStudent().getId());
            st.setLong(2, fellow.getProject().getId());
            st.setString(3, fellow.getCpf());
            st.setString(4, fellow.getLattesCurriculum());
            
            // Handle potential null birth date
            if (fellow.getBirthDate() != null) {
                st.setDate(5, new Date(fellow.getBirthDate().getTime()));
            } else {
                st.setDate(5, null);
            }

            //Executing the insert operation.
            st.executeUpdate();

        }
        //SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("Error creating fellow: " + e.getMessage()); 
            return null;
        }
        //Closing the connection to the Database.
        //Finally block is always executed, regardless if an exception is thrown or not.
        finally{
            connection.closeConnection();
        }

        System.out.println("Fellow created successfully");
        return fellow;
    }

    @Override
    // Method to delete a register from the Database.
    public String delete(long id) {
        // Validate input
        if (id <= 0) {
            return "Error deleting fellow: Invalid ID";
        }
        
        // Opening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence.
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_FELLOW +
                " WHERE " + ConstantsDataBase.COLUMN_ID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the fellow's ID).
            st.setLong(1, id); 

            // Executing the delete operation.
            int rowsAffected = st.executeUpdate();
            
            if (rowsAffected == 0) {
                return "No fellow found with ID: " + id;
            }
        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting fellow: " + e.getMessage(); 
        }
        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "Fellow deleted successfully";
    }

    @Override
    // Method to update an existing register in the Database.
    public Fellow update(long id,Fellow fellow) {
        // Validate input
        if (fellow == null) {
            System.out.println("Error updating fellow: Fellow object is null");
            return null;
        }
        if (fellow.getId() <= 0) {
            System.out.println("Error updating fellow: Invalid Fellow ID");
            return null;
        }
        if (fellow.getStudent() == null) {
            System.out.println("Error updating fellow: Student is required");
            return null;
        }
        if (fellow.getProject() == null) {
            System.out.println("Error updating fellow: Project is required");
            return null;
        }
        
        // Opening the connection to the Database.
        this.connection.openConnection();
        // Update SQL sentence.
        String sql = "UPDATE " + ConstantsDataBase.TABLE_FELLOW +
                " SET " + ConstantsDataBase.COLUMN_STUDENTID + "=?, " +
                ConstantsDataBase.COLUMN_PROJECTID + "=?, " +
                ConstantsDataBase.COLUMN_CPF + "=?, " +
                ConstantsDataBase.COLUMN_LATTESCURRICULUM + "=?, " +
                ConstantsDataBase.COLUMN_BIRTHDATE + "=? WHERE " +
                ConstantsDataBase.COLUMN_ID + "=?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the new values of the PreparedStatement.
            st.setLong(1, fellow.getStudent().getId());
            st.setLong(2, fellow.getProject().getId());
            st.setString(3, fellow.getCpf());
            st.setString(4, fellow.getLattesCurriculum());
            
            // Handle potential null birth date
            if (fellow.getBirthDate() != null) {
                st.setDate(5, new Date(fellow.getBirthDate().getTime()));
            } else {
                st.setDate(5, null);
            }
            
            // Setting the value for the WHERE clause (the fellow's ID).
            st.setLong(6, id); 
            
            // Executing the update operation.
            int rowsAffected = st.executeUpdate();
            
            if (rowsAffected == 0) {
                System.out.println("No fellow found with ID: " + fellow.getId());
            }
        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating fellow: " + e.getMessage()); 
            return null;
        }
        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        System.out.println("Fellow updated successfully");
        return fellow;
    }

    @Override
    // Method to read a register from the Database by ID.
    public Fellow read(long id) {
        // Validate input
        if (id <= 0) {
            return null;
        }
        
        Fellow fellow = null;
        // Opening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_FELLOW +
                " WHERE " + ConstantsDataBase.COLUMN_ID + " = ?";
                
        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the fellow's ID).
            st.setLong(1, id); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Checking if a result was found.
            if (rs.next()) {
                // Instantiating a new Fellow object with the retrieved data.
                fellow = new Fellow();
                fellow.setId(rs.getLong(ConstantsDataBase.COLUMN_ID));
                
                // Load related Student and Project objects using their DAOs
                long studentId = rs.getLong(ConstantsDataBase.COLUMN_STUDENTID);
                long projectId = rs.getLong(ConstantsDataBase.COLUMN_PROJECTID);
                
                // Load full Student object
                Student student = studentDAO.read(studentId);
                if (student == null) {
                    // Create minimal student if not found
                    student = new Student();
                    student.setId(studentId);
                }
                fellow.setStudent(student);
                
                // Load full Project object
                Project project = projectDAO.read(projectId);
                if (project == null) {
                    // Create minimal project if not found - using ResearchProject as default
                    project = new com.koble.koble.model.ResearchProject();
                    project.setId(projectId);
                }
                fellow.setProject(project);
                
                fellow.setCpf(rs.getString(ConstantsDataBase.COLUMN_CPF));
                fellow.setLattesCurriculum(rs.getString(ConstantsDataBase.COLUMN_LATTESCURRICULUM));
                fellow.setBirthDate(rs.getDate(ConstantsDataBase.COLUMN_BIRTHDATE));
            }
        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading fellow: " + e.getMessage());
        }
        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the Fellow object or null if not found/error.
        return fellow;
    }

    @Override
    // Method to list all registers from the Database.
    public List<Fellow> listAll() {
        // List to store all Fellow objects.
        List<Fellow> fellows = new ArrayList<>();
        // Opening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_FELLOW;

        try {
            // Creating a PreparedStatement to execute the SQL sentence
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new Fellow object for each row.
                Fellow fellow = new Fellow();
                fellow.setId(rs.getLong(ConstantsDataBase.COLUMN_ID));
                
                // Load related Student and Project objects using their DAOs
                long studentId = rs.getLong(ConstantsDataBase.COLUMN_STUDENTID);
                long projectId = rs.getLong(ConstantsDataBase.COLUMN_PROJECTID);
                
                // Load full Student object
                Student student = studentDAO.read(studentId);
                if (student == null) {
                    // Create minimal student if not found
                    student = new Student();
                    student.setId(studentId);
                }
                fellow.setStudent(student);
                
                // Load full Project object
                Project project = projectDAO.read(projectId);
                if (project == null) {
                    // Create minimal project if not found - using ResearchProject as default
                    project = new com.koble.koble.model.ResearchProject();
                    project.setId(projectId);
                }
                fellow.setProject(project);
                
                fellow.setCpf(rs.getString(ConstantsDataBase.COLUMN_CPF));
                fellow.setLattesCurriculum(rs.getString(ConstantsDataBase.COLUMN_LATTESCURRICULUM));
                fellow.setBirthDate(rs.getDate(ConstantsDataBase.COLUMN_BIRTHDATE));
                
                // Adding the fellow object to the list.
                fellows.add(fellow);
            }
        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading fellows: " + e.getMessage());        
        }
        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of fellows (can be empty).
        return fellows;
    }
}
