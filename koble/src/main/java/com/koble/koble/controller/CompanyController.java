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
import java.util.Optional;


import com.koble.koble.model.Company;
import com.koble.koble.persistence.dataAccessObject.CompanyDAO;

//It is an annotation that combine two other spring annotation: @Controller and @ResponseBody.
//@Controller recieves the responsible for receiving user requests, processing them and returning a response.
//@ResponseBody indicates that the response is connected to the body of the HTTP protocole(JSON).
//@RestController works like an simplified way of the RestApi.
@RestController
//@RequestMapping maps the HTTP requests and calls the referenced function.
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyDAO companyDAO;

    //Annotation for dependence injection.
    @Autowired
    public CompanyController(CompanyDAO companyDAO){
        this.companyDAO = companyDAO;
    }

    //----- CREATE NEW COMPANY METHOD -------
    //Mapping for the HTTP Post verbe.
    @PostMapping
    //Response Entity is a class that controls the reply HTTP(header,body and status). 
    public ResponseEntity<Company> createCompany(@RequestBody Company company){
        Company newCompany = companyDAO.create(company);
        //Return the status 201 Created and the object created.
        return new ResponseEntity<>(newCompany, HttpStatus.CREATED);
    }

    //----- LIST COMPANY METHOD -------
    //Mapping for the HTTP Get verbe.
    @GetMapping
    public ResponseEntity<List<Company>> listAllCompany(){
        List<Company> company = companyDAO.listAll();
        //Return 200 OK and the company list.
        return ResponseEntity.ok(company);
    }

    //----- READ COMPANY FOR THE ID METHOD -------
    @GetMapping("/{id}")
    //@PathVariable maps the {id} for the Long id.
    public ResponseEntity<Company> readCompany(@PathVariable Long id){
        Company company = companyDAO.read(id);
        if (company != null) {
            // Return the status 200 OK and the founded company.
            return ResponseEntity.ok(company);
        } else {
            // Return the status 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    //----- UPDATE COMPANY FOR THE ID METHOD -------
    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable long id ,@RequestBody Company company){
        Company updateCompany = companyDAO.update(id, company);

        //If company is not null, it means that the object was updated, and return 200 OK.
        if(updateCompany != null){
            return ResponseEntity.ok(updateCompany);
        }

        //If company is null, the system assumes it was a Bad Request case and return 400 Bad Request or 500 Internal Server Error.
        else{
            return ResponseEntity.notFound().build();
        }
    }

    //----- DELETE COMPANY FOR THE ID METHOD -------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable long id){
        boolean deleted = companyDAO.delete(id);

        if (deleted) {
            //Return status 204 No Content (but well succed opperetion).
            return ResponseEntity.noContent().build();
        } else {
            // Return status 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }
    }

