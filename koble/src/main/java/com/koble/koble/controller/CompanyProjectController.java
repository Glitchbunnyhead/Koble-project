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
import com.koble.koble.model.Company;
import com.koble.koble.model.Project;
import com.koble.koble.persistence.dataAccessObject.CompanyDAO;
import com.koble.koble.persistence.dataAccessObject.CompanyProjectDAO;
import com.koble.koble.persistence.dataAccessObject.ProjectDAO;

@RestController
@RequestMapping("/api/company_project")
public class CompanyProjectController{

    private final CompanyProjectDAO companyProjectDAO;
    private final CompanyDAO companyDAO;
    private final ProjectDAO projectDAO;

    @Autowired
    public CompanyProjectController(CompanyProjectDAO companyProjectDAO, CompanyDAO companyDAO, ProjectDAO projectDAO){
        this.companyProjectDAO = companyProjectDAO;
        this.companyDAO = companyDAO;
        this.projectDAO = projectDAO;
    }

    @PostMapping
    public ResponseEntity<CompanyProject> createCompanyProject(@RequestBody CompanyProject companyProject){
        
        // 1. Validar se a Company existe
        if (!companyDAO.exists(companyProject.getCompanyId())) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); 
        }

        // 2. Validar se o Project existe
        if (!projectDAO.exists(companyProject.getProjectId())) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        // 3. Tentar criar o relacionamento no BD
        CompanyProject newCompanyProject = companyProjectDAO.create(companyProject);
        
        if (newCompanyProject == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<>(newCompanyProject, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<CompanyProject>> listAllCompanyProjects(){
        List<CompanyProject> companyProjects = companyProjectDAO.listAll();
        return ResponseEntity.ok(companyProjects);
    }


    @GetMapping("/projects/{companyId}")
    public ResponseEntity<List<Project>> findProjectsByCompanyId(@PathVariable Long companyId){
        List<Project> projects = companyProjectDAO.findProjectsByCompanyId(companyId);
        
        if (projects.isEmpty()) {
            return ResponseEntity.notFound().build();
        } 
        
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/companies/{projectId}")
    public ResponseEntity<List<Company>> findCompaniesByProjectId(@PathVariable Long projectId){
        List<Company> companies = companyProjectDAO.findCompaniesByProjectId(projectId);
        
        if (companies.isEmpty()) {
            return ResponseEntity.notFound().build();
        } 
        
        return ResponseEntity.ok(companies);
    }

    @DeleteMapping("/{companyId}/{projectId}")
    public ResponseEntity<String> deleteCompanyProject(@PathVariable long companyId, @PathVariable long projectId) {
        
        if (!companyProjectDAO.exists(companyId, projectId)) {
            return new ResponseEntity<>("Company project relationship not found", HttpStatus.NOT_FOUND);
        }

        String status = companyProjectDAO.delete(companyId, projectId);
        
        
        if (status.toLowerCase().contains("success") || 
            status.toLowerCase().contains("deleted")) {
            
            return ResponseEntity.ok(status); 
            
        } else if (status.toLowerCase().contains("not found")) {
            return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
        }
        
        else if (status.toLowerCase().contains("error")) {
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        else {
            return ResponseEntity.ok(status);
        }
    }

    @DeleteMapping("/company/{companyId}")
    public ResponseEntity<String> deleteAllProjectsByCompanyId(@PathVariable long companyId) {
        
        String status = companyProjectDAO.deleteAllByCompanyId(companyId);
        

        if (status.toLowerCase().contains("success") || status.toLowerCase().contains("deleted")) {
            return ResponseEntity.ok(status);

        } else if (status.toLowerCase().contains("error")) {
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);

        } else {
             return ResponseEntity.ok(status);
        }
    }

    @DeleteMapping("/project/{projectId}")
    public ResponseEntity<String> deleteAllCompaniesByProjectId(@PathVariable long projectId) {
        
        String status = companyProjectDAO.deleteAllByProjectId(projectId);
        
        if (status.toLowerCase().contains("success") || status.toLowerCase().contains("deleted")) {
            return ResponseEntity.ok(status);
        } else if (status.toLowerCase().contains("error")) {
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
             return ResponseEntity.ok(status); 
        }
    }
}