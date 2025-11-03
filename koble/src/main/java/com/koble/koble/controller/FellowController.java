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

import com.koble.koble.model.Fellow;
import com.koble.koble.persistence.dataAccessObject.FellowDAO;

@RestController
@RequestMapping("/api/fellow")
public class FellowController {

    private final FellowDAO fellowDAO;

    @Autowired
    public FellowController(FellowDAO fellowDAO) {
        this.fellowDAO = fellowDAO;
    }

    @PostMapping
    public ResponseEntity<Fellow> createFellow(@RequestBody Fellow fellow) {
        Fellow newFellow = fellowDAO.create(fellow);

        if (newFellow == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(newFellow, HttpStatus.CREATED);
    }

    
    @GetMapping
    public ResponseEntity<List<Fellow>> listAllFellows() {
        List<Fellow> fellows = fellowDAO.listAll();
        return ResponseEntity.ok(fellows);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Fellow> readFellow(@PathVariable Long id) {
        Fellow fellow = fellowDAO.read(id);

        if (fellow != null) {
            return ResponseEntity.ok(fellow);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
  
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFellow(@PathVariable long id) {
        String status = fellowDAO.delete(id);
        
        if (status.toLowerCase().contains("success") || 
            status.toLowerCase().contains("deleted")) {
            
            return ResponseEntity.ok(status); 
            
        } 
        
        else if (status.toLowerCase().contains("no fellow found") || 
                 status.toLowerCase().contains("not found")) {
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