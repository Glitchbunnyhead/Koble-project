package com.koble.koble.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koble.koble.model.CompanyProject;
import com.koble.koble.persistence.dataAccessObject.CompanyProjectDAO;


//It is an annotation that combine two other spring annotation: @Controller and @ResponseBody.
//@Controller recieves the responsible for receiving user requests, processing them and returning a response.
//@ResponseBody indicates that the response is connected to the body of the HTTP protocole(JSON).
//@RestController works like an simplified way of the RestApi.
@RestController
//@RequestMapping maps the HTTP requests and calls the referenced function.
@RequestMapping("/api/company_project")
public class CompanyProjectController{

     private final CompanyProjectDAO companyProjectDAO;

    //Annotation for dependence injection.
    @Autowired
    public CompanyProjectController(CompanyProjectDAO companyProjectDAO){
        this.companyProjectDAO = companyProjectDAO;
    }

      //----- CREATE NEW COMPANY METHOD -------
    //Mapping for the HTTP Post verbe.
    @PostMapping
    //Response Entity is a class that controls the reply HTTP(header,body and status). 
    public ResponseEntity<CompanyProject> createCompany(@RequestBody CompanyProject companyProject){
        CompanyProject newCompanyProject = companyProjectDAO.create(companyProject);
        //Return the status 201 Created and the object created.
        return new ResponseEntity<>(newCompanyProject, HttpStatus.CREATED);
    }

    //----- LIST COMPANY PROJECT RELATIONSHIPS METHOD -------
    @GetMapping
    public ResponseEntity<List<CompanyProject>> listAllCompanyProjects(){
        List<CompanyProject> companyProjects = companyProjectDAO.listAll();
        // Retorna 200 OK e a lista de relações.
        return ResponseEntity.ok(companyProjects);
    }

    //----- FIND PROJECTS BY COMPANY ID METHOD -------
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<CompanyProject>> findProjectsByCompanyId(@PathVariable Long companyId){
        List<CompanyProject> companyProjects = companyProjectDAO.findProjectsByCompanyId(companyId);
        
        if (companyProjects.isEmpty()) {
            //Return 404 Not Found if no relationships are found for the given company ID.
            return ResponseEntity.notFound().build();
        } 
        
        // Return 200 OK and the list of relationships.
        return ResponseEntity.ok(companyProjects);
    }

    //----- FIND COMPANIES BY PROJECT ID METHOD -------
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<CompanyProject>> findCompaniesByProjectId(@PathVariable Long projectId){
        List<CompanyProject> companyProjects = companyProjectDAO.findCompaniesByProjectId(projectId);
        
        if (companyProjects.isEmpty()) {
            //Return 404 Not Found if no relationships are found for the given project ID.
            return ResponseEntity.notFound().build();
        } 
        
        //Return 200 OK and the list of relationships.
        return ResponseEntity.ok(companyProjects);
    }

    //----- DELETE COMPANY PROJECT RELATIONSHIP BY ID METHOD -------
    @DeleteMapping("/{companyId}/{projectId}")
    public ResponseEntity<String> deleteCompanyProject(@PathVariable long companyId, @PathVariable long projectId) {
        
        // Verify if the relationship exists before attempting deletion.
        if (!companyProjectDAO.exists(companyId, projectId)) {
            return new ResponseEntity<>("Company project relationship not found", HttpStatus.NOT_FOUND);
        }

        //Proceed to delete the relationship.
        String status = companyProjectDAO.delete(companyId, projectId);
        
        
        if (status.toLowerCase().contains("success") || 
            status.toLowerCase().contains("deleted")) {
            
            //Return the status 200 OK and the success message.
            return ResponseEntity.ok(status); 
            
        } else if (status.toLowerCase().contains("not found")) {
            // If the relationship was not found, it returns 404 Not Found.
            return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
        }
        
        //Genereal error with the data base.
        else if (status.toLowerCase().contains("error")) {
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        else {
            //Fallback for unexpected cases.
            return ResponseEntity.ok(status);
        }
    }

    //----- DELETE ALL PROJECTS BY COMPANY ID METHOD ------
    @DeleteMapping("/company/{companyId}")
    public ResponseEntity<String> deleteAllProjectsByCompanyId(@PathVariable long companyId) {
        // Verify if there are relationships for the given company ID.
        // If non exist, return 404 Not Found.
        String status = companyProjectDAO.deleteAllByCompanyId(companyId);
        

        if (status.toLowerCase().contains("success") || status.toLowerCase().contains("deleted")) {
            return ResponseEntity.ok(status);

        //If no relationships were found to delete.
        } else if (status.toLowerCase().contains("error")) {
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);

        //Fallback for unexpected cases.
        } else {
             return ResponseEntity.ok(status);
        }
    }

    //----- DELETE ALL COMPANIES BY PROJECT ID METHOD ------
    @DeleteMapping("/project/{projectId}")
    public ResponseEntity<String> deleteAllCompaniesByProjectId(@PathVariable long projectId) {
        // If the project exists, proceed to delete all relationships.
        String status = companyProjectDAO.deleteAllByProjectId(projectId);
        
        if (status.toLowerCase().contains("success") || status.toLowerCase().contains("deleted")) {
            return ResponseEntity.ok(status);
        //If no relationships were found to delete.
        } else if (status.toLowerCase().contains("error")) {
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        //Fallback for unexpected cases.
        } else {
             return ResponseEntity.ok(status); 
        }
    }
}

