package com.koble.koble.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

@Component

public class MySqlConnection {

    private Connection connection;

    private final ConfigurationsJDBC CONFIGURATIONS_DATABASE;
    
    public MySqlConnection(ConfigurationsJDBC CONFIGURATIONS_DATABASE){
        this.CONFIGURATIONS_DATABASE = CONFIGURATIONS_DATABASE;
    }

    public void openConnection(){

        try{
            if (connection != null && !connection.isClosed()) {
                return; 
            }
            
            Class.forName(CONFIGURATIONS_DATABASE.getDriver());

            connection = DriverManager.getConnection(
                CONFIGURATIONS_DATABASE.getUrl(),
                CONFIGURATIONS_DATABASE.getUser(), 
                CONFIGURATIONS_DATABASE.getPassword()
            );
        }
        
        catch(ClassNotFoundException | SQLException e) {
            System.err.println("ERROR opening connection to DB:");
            e.printStackTrace(); 
            //That way, does not mask the real error with NullPointerException.
            throw new RuntimeException("Failed to connect to database.", e);
        }
    }

    public void closeConnection(){
        try{
            if(connection != null && !connection.isClosed()){
                connection.close();
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection(){
        return connection;
    }
}
