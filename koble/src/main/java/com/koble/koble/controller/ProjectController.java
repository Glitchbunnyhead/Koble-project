package com.koble.koble.controller;

import com.koble.koble.model.Company;
import com.koble.koble.model.EducationalProject;
import com.koble.koble.model.ExtensionProject;
import com.koble.koble.model.Project;
import com.koble.koble.model.ResearchProject;
import com.koble.koble.persistence.dataAccessObject.EducationalProjectDAO;
import com.koble.koble.persistence.dataAccessObject.ExtensionProjectDAO;
import com.koble.koble.persistence.dataAccessObject.ProjectDAO;
import com.koble.koble.persistence.dataAccessObject.ResearchProjectDAO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project") 
public class ProjectController { 
    
    private final ProjectDAO projectDAO;
    private final ResearchProjectDAO researchDAO;
    private final EducationalProjectDAO educationalDAO;
    private final ExtensionProjectDAO extensionDAO;

    @Autowired
    public ProjectController(ProjectDAO projectDAO, ResearchProjectDAO researchDAO, EducationalProjectDAO educationalDAO, ExtensionProjectDAO extensionDAO) {
        this.projectDAO = projectDAO;
        this.researchDAO = researchDAO;
        this.educationalDAO = educationalDAO;
        this.extensionDAO = extensionDAO;
    }

    @PostMapping("/research")
    @Transactional 
    public ResponseEntity<ResearchProject> createResearchProject(@RequestBody ResearchProject researchProject) {
        
        Project rootProject = null;
        try {
            rootProject = projectDAO.create(researchProject);
        } catch (RuntimeException e) {
            System.err.println("Erro creating the root project. Rollback will be executed.");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if (rootProject == null || rootProject.getId() == 0) {
             return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        long projectId = rootProject.getId(); 

        try {
            ResearchProject createdResearch = researchDAO.create(researchProject, projectId);
            
            return new ResponseEntity<>(createdResearch, HttpStatus.CREATED);

        } catch (Exception e) {
            System.err.println("Error creating the research project. Rollback will be executed.");
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/educational")
    @Transactional 
    public ResponseEntity<EducationalProject> createEducationalProject(@RequestBody EducationalProject educationalProject) {
        
        Project rootProject = null;
        try {
            rootProject = projectDAO.create(educationalProject);
        } catch (RuntimeException e) {
  
            System.err.println("Erro creating the root project. Rollback will be executed.");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if (rootProject == null || rootProject.getId() == 0) {
             return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        long projectId = rootProject.getId(); 

        try { 
            EducationalProject createdEducational = educationalDAO.create(educationalProject, projectId);
            
            return new ResponseEntity<>(createdEducational, HttpStatus.CREATED);

        } catch (Exception e) {

            System.err.println("Error creating the educational project. Rollback will be executed.");
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/extension")
    @Transactional 
    public ResponseEntity<ExtensionProject> createExtensionProject(@RequestBody ExtensionProject extensionProject) {
        
        Project rootProject = null;
        try {
            rootProject = projectDAO.create(extensionProject);
        } catch (RuntimeException e) {
  
            System.err.println("Erro creating the root project. Rollback will be executed.");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if (rootProject == null || rootProject.getId() == 0) {
             return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        long projectId = rootProject.getId(); 

        try { 
            ExtensionProject createdExtension = extensionDAO.create(extensionProject, projectId);
            
            return new ResponseEntity<>(createdExtension, HttpStatus.CREATED);

        } catch (Exception e) {

            System.err.println("Error creating the extension project. Rollback will be executed.");
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/research/{id}")
    public ResponseEntity<ResearchProject> readResearchProject(@PathVariable Long id){
        ResearchProject researchProject = researchDAO.read(id);

        if (researchDAO != null) {
            return ResponseEntity.ok(researchProject);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/educational/{id}")
    public ResponseEntity<EducationalProject> readEducationalProject(@PathVariable Long id){
        EducationalProject educationaProject = educationalDAO.read(id);

        if (researchDAO != null) {
            return ResponseEntity.ok(educationaProject);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/extension/{id}")
    public ResponseEntity<ExtensionProject> readExtensionProject(@PathVariable Long id){
        ExtensionProject extensionProject = extensionDAO.read(id);

        if (researchDAO != null) {
            return ResponseEntity.ok(extensionProject);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/research")
    public ResponseEntity<List<ResearchProject>> readAllResearchProjects(){
        List<ResearchProject> researchProjects = researchDAO.listAll();

        return ResponseEntity.ok(researchProjects);
    }

        @GetMapping("/educational")
    public ResponseEntity<List<EducationalProject>> readAllEducationalProjects(){
        List<EducationalProject> educationalProjects = educationalDAO.listAll();

        return ResponseEntity.ok(educationalProjects);
    }

        @GetMapping("/extension")
    public ResponseEntity<List<ExtensionProject>> readAllExtentionProjects(){
        List<ExtensionProject> extensionProjects = extensionDAO.listAll();

        return ResponseEntity.ok(extensionProjects);
    }

    @GetMapping
    public ResponseEntity<List<Project>> readAllProjects(){
        List<Project> allProjects = projectDAO.listAll();

        return ResponseEntity.ok(allProjects);
    }


    @PutMapping("/research/{id}")
    @Transactional
    public ResponseEntity<ResearchProject> updateResearchProject(@PathVariable long id ,@RequestBody ResearchProject newProject){
        Project rootProject = projectDAO.update(id, newProject);
        ResearchProject updateResearchProject = researchDAO.update(id, newProject);

        if(updateResearchProject != null){
            return ResponseEntity.ok(updateResearchProject);
        }

        else{
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/educational/{id}")
    @Transactional
    public ResponseEntity<EducationalProject> updateEducationalProject(@PathVariable long id ,@RequestBody EducationalProject newProject){
        Project rootProject = projectDAO.update(id, newProject);
        EducationalProject updateEducationalProject = educationalDAO.update(id, newProject);

        if(updateEducationalProject != null){
            return ResponseEntity.ok(updateEducationalProject);
        }

        else{
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/extension/{id}")
    @Transactional
    public ResponseEntity<ExtensionProject> updateExtensionProject(@PathVariable long id ,@RequestBody ExtensionProject newProject){
        Project rootProject = projectDAO.update(id, newProject);
        ExtensionProject updateExtensionProject = extensionDAO.update(id, newProject);

        if(updateExtensionProject != null){
            return ResponseEntity.ok(updateExtensionProject);
        }

        else{
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/research/{id}")
    @Transactional
    public ResponseEntity<String> deleteResearch(@PathVariable long id){
        String statusResearch = researchDAO.delete(id);

        if (statusResearch.toLowerCase().contains("success") || 
            statusResearch.toLowerCase().contains("deleted")) {
            
            System.out.println(statusResearch); 

            String statusProject = projectDAO.delete(id);
            if (statusResearch.toLowerCase().contains("success") || 
            statusResearch.toLowerCase().contains("deleted")) { 
                return ResponseEntity.ok(statusProject);
            }

            else if (statusResearch.toLowerCase().contains("not found")) {
            return new ResponseEntity<>(statusResearch, HttpStatus.NOT_FOUND);
        }

            else if (statusResearch.toLowerCase().contains("error")) {
                return new ResponseEntity<>(statusResearch, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            else {
                return ResponseEntity.ok(statusResearch);
            }

            }
        else if (statusResearch.toLowerCase().contains("not found")) {
            return new ResponseEntity<>(statusResearch, HttpStatus.NOT_FOUND);
        }

        else if (statusResearch.toLowerCase().contains("error")) {
            return new ResponseEntity<>(statusResearch, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        else {
            return ResponseEntity.ok(statusResearch);
        }
}

@DeleteMapping("/educational/{id}")
    @Transactional
    public ResponseEntity<String> deleteEducational(@PathVariable long id){
        String statusEducational = educationalDAO.delete(id);

        if (statusEducational.toLowerCase().contains("success") || 
            statusEducational.toLowerCase().contains("deleted")) {
            
            System.out.println(statusEducational); 

            String statusProject = projectDAO.delete(id);
            if (statusEducational.toLowerCase().contains("success") || 
            statusEducational.toLowerCase().contains("deleted")) { 
                return ResponseEntity.ok(statusProject);
            }

            else if (statusEducational.toLowerCase().contains("not found")) {
            return new ResponseEntity<>(statusEducational, HttpStatus.NOT_FOUND);
        }

            else if (statusEducational.toLowerCase().contains("error")) {
                return new ResponseEntity<>(statusEducational, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            else {
                return ResponseEntity.ok(statusEducational);
            }

            }
        else if (statusEducational.toLowerCase().contains("not found")) {
            return new ResponseEntity<>(statusEducational, HttpStatus.NOT_FOUND);
        }

        else if (statusEducational.toLowerCase().contains("error")) {
            return new ResponseEntity<>(statusEducational, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        else {
            return ResponseEntity.ok(statusEducational);
        }
    }


    @DeleteMapping("/extension/{id}")
    @Transactional
    public ResponseEntity<String> deleteExtension(@PathVariable long id){
        String statusExtension = educationalDAO.delete(id);

        if (statusExtension.toLowerCase().contains("success") || 
            statusExtension.toLowerCase().contains("deleted")) {
            
            System.out.println(statusExtension); 

            String statusProject = projectDAO.delete(id);
            if (statusExtension.toLowerCase().contains("success") || 
            statusExtension.toLowerCase().contains("deleted")) { 
                return ResponseEntity.ok(statusProject);
            }

            else if (statusExtension.toLowerCase().contains("not found")) {
            return new ResponseEntity<>(statusExtension, HttpStatus.NOT_FOUND);
        }

            else if (statusExtension.toLowerCase().contains("error")) {
                return new ResponseEntity<>(statusExtension, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            else {
                return ResponseEntity.ok(statusExtension);
            }

            }
        else if (statusExtension.toLowerCase().contains("not found")) {
            return new ResponseEntity<>(statusExtension, HttpStatus.NOT_FOUND);
        }

        else if (statusExtension.toLowerCase().contains("error")) {
            return new ResponseEntity<>(statusExtension, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        else {
            return ResponseEntity.ok(statusExtension);
        }
    }





}



