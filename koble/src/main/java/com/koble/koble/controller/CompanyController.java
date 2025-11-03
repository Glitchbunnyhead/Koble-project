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

import com.koble.koble.model.Company;
import com.koble.koble.persistence.dataAccessObject.CompanyDAO;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyDAO companyDAO;

    @Autowired
    public CompanyController(CompanyDAO companyDAO){
        this.companyDAO = companyDAO;
    }

    @PostMapping
    public ResponseEntity<Company> createCompany(@RequestBody Company company){
        Company newCompany = companyDAO.create(company);
        if (newCompany == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(newCompany, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<Company>> listAllCompanies(){
        List<Company> companies = companyDAO.listAll();
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Company> readCompany(@PathVariable Long id){
        Company company = companyDAO.read(id);
        if (company != null) {
            return ResponseEntity.ok(company);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable long id ,@RequestBody Company company){
        Company updatedCompany = companyDAO.update(id, company);

        if(updatedCompany != null){
            return ResponseEntity.ok(updatedCompany);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable long id){
        String status = companyDAO.delete(id);
        
        if (status.toLowerCase().contains("success") || 
            status.toLowerCase().contains("deleted")) {
            
            
            return ResponseEntity.ok(status); 
            
        } else if (status.toLowerCase().contains("not found")) {
            return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
        } else if (status.toLowerCase().contains("error")) {
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return ResponseEntity.ok(status);
        }
    }
}