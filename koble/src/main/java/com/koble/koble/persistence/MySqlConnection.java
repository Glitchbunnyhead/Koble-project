package com.koble.koble.persistence;

//Imports the MySql connection libraries.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Imports the spring annotation.
import org.springframework.stereotype.Component;

//Generic stereotype that marks the classes that Spring should create an instance.
@Component

//Class for the connection of Java application(through the persistence) with MySql database.
public class MySqlConnection {

    private Connection connection;

    //Declaring the MySql configurations from the class ConfigurationsJDBC.
    private final ConfigurationsJDBC CONFIGURATIONS_DATABASE;
    
    //Instance CONFIGURATIONS_DATABASE through the class constructor.
    public MySqlConnection(ConfigurationsJDBC CONFIGURATIONS_DATABASE){
        this.CONFIGURATIONS_DATABASE = CONFIGURATIONS_DATABASE;
    }

    //Method to open the Database-Persistence connection.
    public void openConnection(){

        try{
            //Verify if the connection are already opened for avoid unnecessary reconections.
            if (connection != null && !connection.isClosed()) {
                return; 
            }
            
            Class.forName(CONFIGURATIONS_DATABASE.getDriver());

            //Attribuates the values from CONFIGURATIONS_DATABASE for the connection.
            connection = DriverManager.getConnection(
                CONFIGURATIONS_DATABASE.getUrl(),
                CONFIGURATIONS_DATABASE.getUser(), 
                CONFIGURATIONS_DATABASE.getPassword()
            );
        }
        
        //Captures the exception and throw to the upper layers of the system.
        //With that, Spring shows the real case at the terminal.
        catch(ClassNotFoundException | SQLException e) {
            System.err.println("ERROR opening connection to DB:");
            e.printStackTrace(); 
            //That way, does not mask the real error with NullPointerException.
            throw new RuntimeException("Failed to connect to database.", e);
        }
    }

    //Method to close the Database-Persistence connection.
    public void closeConnection(){
        try{
            //Verify if the connection it's not null or already closed.
            if(connection != null && !connection.isClosed()){
                connection.close();
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    //Method to get the Database-Persistence connection.
    public Connection getConnection(){
        return connection;
    }
}
