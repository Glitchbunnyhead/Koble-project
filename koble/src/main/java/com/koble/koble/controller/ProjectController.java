package com.koble.koble.controller;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koble.koble.model.Company;
import com.koble.koble.model.Project;
import com.koble.koble.persistence.dataAccessObject.ProjectDAO;

//It is an annotation that combine two other spring annotation: @Controller and @ResponseBody.
//@Controller recieves the responsible for receiving user requests, processing them and returning a response.
//@ResponseBody indicates that the response is connected to the body of the HTTP protocole(JSON).
//@RestController works like an simplified way of the RestApi.
@RestController
@RequestMapping("api/project")
public class ProjectController {

    private final ProjectDAO projectDAO;

    @Autowired
    public ProjectController(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    //----- CREATE NEW PROJECT METHOD -------
    @PostMapping 
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
    
        Project newProject = projectDAO.create(project);
        
        if (newProject != null) {
            //Return status 201 Created and the object created.
            return new ResponseEntity<>(newProject, HttpStatus.CREATED);
        } else {
            //Return status 500 Internal Server Error if the oject was not created.
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //----- LIST ALL PROJECT METHOD -------
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectDAO.listAll();
        // Returns 200 OK.
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    //----- GET PROJECT BY ID METHOD -------
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable("id") long id) {
        Project project = projectDAO.read(id);
        
        if (project != null) {
            // Returns 200 OK
            return new ResponseEntity<>(project, HttpStatus.OK);
        } else {
            // Returns 404 Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //----- UPDATE PROJECT BY ID METHOD -------
    public ResponseEntity<Project> updateProject(@PathVariable long id ,@RequestBody Project project){
    Project updateProject = projectDAO.update(id, project);

    //If project is not null, it means that the object was updated, and return 200 OK.
    if(updateProject != null){
        return ResponseEntity.ok(updateProject);
    }

    //If project is null, the system assumes it was a Bad Request case and return 400 Bad Request or 500 Internal Server Error.
    else{
        return ResponseEntity.notFound().build();
    }
}

    //----- DELETE PROJECT BY ID METHOD -------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable long id){
     //Receives the anwser of the method.
        String status = projectDAO.delete(id);
        
        if (status.toLowerCase().contains("success") || 
            status.toLowerCase().contains("deleted")) {
            
            //Return status 204 No Content (but well succed opperetion).
            return ResponseEntity.ok(status); 
            
        } 
        else {
            //Return status 500 Internal Server Error if the object was not deleted.
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}