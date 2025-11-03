package com.koble.koble.controller;

import java.util.List;

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

import com.koble.koble.model.ExternalPerson;
import com.koble.koble.persistence.dataAccessObject.ExternalPersonDAO;

@RestController
@RequestMapping("/api/external_person")
public class ExternalPersonController {

    private final ExternalPersonDAO externalPersonDAO;

    @Autowired
    public ExternalPersonController(ExternalPersonDAO externalPersonDAO){
        this.externalPersonDAO = externalPersonDAO;
    }

    @PostMapping
    public ResponseEntity<ExternalPerson> createExternalPerson(@RequestBody ExternalPerson externalPerson){
        ExternalPerson newExternalPerson = externalPersonDAO.create(externalPerson);
        return new ResponseEntity<>(newExternalPerson, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ExternalPerson>> listAllExternalPersons(){
        List<ExternalPerson> externalPersons = externalPersonDAO.listAll();
        return ResponseEntity.ok(externalPersons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExternalPerson> readExternalPerson(@PathVariable Long id){
        ExternalPerson externalPerson = externalPersonDAO.read(id);
        if (externalPerson != null) {
            return ResponseEntity.ok(externalPerson);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExternalPerson> updateExternalPerson(@PathVariable long id ,@RequestBody ExternalPerson externalPerson){
        ExternalPerson updatedExternalPerson = externalPersonDAO.update(id, externalPerson);

        if(updatedExternalPerson != null){
            return ResponseEntity.ok(updatedExternalPerson);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExternalPerson(@PathVariable long id){
        String status = externalPersonDAO.delete(id);
        
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
}