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

//Importing the TeacherProject model, Crudl interface and MySqlConnection class.
import com.koble.koble.model.TeacherProject;
import com.koble.koble.model.Teacher;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;
//Importando a interface Crudl
import com.koble.koble.persistence.Crudl;

// A classe agora implementa a interface Crudl com o tipo genérico TeacherProject
@Repository
public class TeacherProjectDAO {
    //Creating a MySqlConnection attributes.
    private final MySqlConnection connection;

    //Class constructor.
    public TeacherProjectDAO(MySqlConnection connection){
        this.connection = connection;
    }

    // Método create() implementando a interface Crudl.
    // Alterado o tipo de retorno de String para TeacherProject.

    public TeacherProject create(TeacherProject teacherProject) {
        //Oppening the connection to the Database.
        this.connection.openConnection();
        //Insert SQL sentence for junction table (no ID needed - composite primary key)
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_TEACHERPROJECT + " (" + 
                     ConstantsDataBase.COLUMN_TEACHERID + ", " + 
                     ConstantsDataBase.COLUMN_PROJECTID + ") VALUES (?, ?)";

        try{
            //Creating a PreparedStatement to execute the SQL sentece.
            //PreparedStatemnt is an interface. Is instantied by an anonymous class.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            st.setLong(1, teacherProject.getTeacher().getId());
            st.setLong(2, teacherProject.getProject().getId());

            //Executing the insert operation.
            st.executeUpdate();
            return teacherProject; // Retorna a entidade criada
        }

        //SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e){
            e.printStackTrace();
            System.err.println("Error creating teacher project relationship: " + e.getMessage());
            return null; // Retorna null em caso de erro
        }

        //Closing the connection to the Database.
        //Finally block is always executed,regardless if an exception is thrown or not.
        finally{
            connection.closeConnection();
        }
    }
    
    // Método delete(T entity) implementando a interface Crudl.
    // O método original 'delete(long teacherId, long projectId)' foi adaptado e mantido
    // como a implementação do delete da interface.

  
    // Implementação de read(long idEntity) da interface Crudl.
    // Não aplicável/lógico para uma tabela de junção com chave composta.

    public TeacherProject read(long idEntity) {
        // Para uma tabela de junção, a leitura por um único ID não é lógica.
        System.out.println("Operation not applicable for junction table DAO by single ID: read(long idEntity)");
        return null;
    }

    // Implementação de update(long idEntity, T entity) da interface Crudl.
    // Não aplicável/lógico para uma tabela de junção com chave composta.

    public TeacherProject update(long idEntity, TeacherProject entity) {
        // Para uma tabela de junção, a atualização por um único ID não é lógica.
        throw new UnsupportedOperationException("Update by single ID is not supported for TeacherProject junction table.");
    }


    // Método listAll() implementando a interface Crudl.

    public List<TeacherProject> listAll() {
        // List to store all TeacherProject objects.
        List<TeacherProject> teacherProjects = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_TEACHERPROJECT;

        try {
            // Creating a Statement to execute the SQL sentence (No parameters needed, so a simple Statement is sufficient, 
            // but sticking to PreparedStatement for consistency is also fine).
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new TeacherProject object for each row.
                TeacherProject teacherProject = new TeacherProject();
                
                // Note: These would need corresponding DAO methods to load Teacher and Project objects
                // For now, creating empty objects - this should be improved with proper loading
                Teacher teacher = new Teacher();
                teacher.setId(rs.getLong(ConstantsDataBase.COLUMN_TEACHERID));
                teacherProject.setTeacher(teacher);
                
                // Cannot instantiate abstract Project class directly
                // This should be improved to load actual project via ProjectDAO
                teacherProject.setProject(null);
                
                // Adding the teacherProject object to the list.
                teacherProjects.add(teacherProject);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading teacher projects: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of teacher projects (can be empty).
        return teacherProjects;
    }


    // Método original para delete por IDs (mantido como método auxiliar da DAO, mas não implementa a interface).
    public String delete(long teacherId, long projectId) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence using composite key
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_TEACHERPROJECT +
                " WHERE " + ConstantsDataBase.COLUMN_TEACHERID + " = ? AND " +
                ConstantsDataBase.COLUMN_PROJECTID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the values of the PreparedStatement (teacher and project IDs).
            st.setLong(1, teacherId); 
            st.setLong(2, projectId); 

            // Executing the delete operation.
            st.executeUpdate();
        }

        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting teacher project relationship: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "Teacher project relationship deleted successfully";
    }

    // Method to delete all projects for a specific teacher (mantido)
    public String deleteAllByTeacherId(long teacherId) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_TEACHERPROJECT +
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
            return "Error deleting teacher projects: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "All teacher projects deleted successfully";
    }

    // Method to delete all teachers for a specific project (mantido)
    public String deleteAllByProjectId(long projectId) {
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Delete SQL sentence
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_TEACHERPROJECT +
                " WHERE " + ConstantsDataBase.COLUMN_PROJECTID + " = ?";

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
            return "Error deleting project teachers: " + e.getMessage(); 
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        return "All project teachers deleted successfully";
    }

    // Method to check if a specific teacher-project relationship exists (mantido)
    public boolean exists(long teacherId, long projectId) {
        boolean exists = false;
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT COUNT(*) FROM " + ConstantsDataBase.TABLE_TEACHERPROJECT +
                " WHERE " + ConstantsDataBase.COLUMN_TEACHERID + " = ? AND " +
                ConstantsDataBase.COLUMN_PROJECTID + " = ?";
                
        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the values of the PreparedStatement (teacher and project IDs).
            st.setLong(1, teacherId); 
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
            System.out.println("Error checking teacher project relationship: " + e.getMessage());
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns true if the relationship exists, false otherwise.
        return exists;
    }

    // Additional method to find all projects by teacher ID (mantido)
    public List<TeacherProject> findProjectsByTeacherId(long teacherId) {
        // List to store TeacherProject objects for a specific teacher.
        List<TeacherProject> teacherProjects = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_TEACHERPROJECT +
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
                // Instantiating a new TeacherProject object for each row.
                TeacherProject teacherProject = new TeacherProject();
                
                // Note: These would need corresponding DAO methods to load Teacher and Project objects
                // For now, creating empty objects - this should be improved with proper loading
                Teacher teacher = new Teacher();
                teacher.setId(rs.getLong(ConstantsDataBase.COLUMN_TEACHERID));
                teacherProject.setTeacher(teacher);
                
                // Cannot instantiate abstract Project class directly
                // This should be improved to load actual project via ProjectDAO
                teacherProject.setProject(null);
                
                // Adding the teacherProject object to the list.
                teacherProjects.add(teacherProject);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading projects by teacher ID: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of teacher projects for the specific teacher (can be empty).
        return teacherProjects;
    }

    // Additional method to find all teachers by project ID (mantido)
    public List<TeacherProject> findTeachersByProjectId(long projectId) {
        // List to store TeacherProject objects for a specific project.
        List<TeacherProject> teacherProjects = new ArrayList<>();
        // Oppening the connection to the Database.
        this.connection.openConnection();
        // Select SQL sentence.
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_TEACHERPROJECT +
                " WHERE " + ConstantsDataBase.COLUMN_PROJECTID + " = ?";

        try {
            // Creating a PreparedStatement to execute the SQL sentence.
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            
            // Setting the value of the PreparedStatement (the project's ID).
            st.setLong(1, projectId); 
            
            // Executing the query and getting the result set.
            ResultSet rs = st.executeQuery();

            // Looping through all results.
            while (rs.next()) {
                // Instantiating a new TeacherProject object for each row.
                TeacherProject teacherProject = new TeacherProject();
                
                // Note: These would need corresponding DAO methods to load Teacher and Project objects
                // For now, creating empty objects - this should be improved with proper loading
                Teacher teacher = new Teacher();
                teacher.setId(rs.getLong(ConstantsDataBase.COLUMN_TEACHERID));
                teacherProject.setTeacher(teacher);
                
                // Cannot instantiate abstract Project class directly
                // This should be improved to load actual project via ProjectDAO
                teacherProject.setProject(null);
                
                // Adding the teacherProject object to the list.
                teacherProjects.add(teacherProject);
            }

        }
        // SQLException is an exception that is thrown when there is an error with the SQL code.
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading teachers by project ID: " + e.getMessage());        
        }

        // Closing the connection to the Database.
        finally {
            connection.closeConnection();
        }

        // Returns the list of teacher projects for the specific project (can be empty).
        return teacherProjects;
    }
}