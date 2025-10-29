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

//It is an annotation that combine two other spring annotation: @Controller and @ResponseBody.
//@Controller recieves the responsible for receiving user requests, processing them and returning a response.
//@ResponseBody indicates that the response is connected to the body of the HTTP protocole(JSON).
//@RestController works like an simplified way of the RestApi.
@RestController
//@RequestMapping maps the HTTP requests and calls the referenced function.
@RequestMapping("/api/external_person")
public class ExternalPersonController {

    private final ExternalPersonDAO externalPersonDAO;

    //Annotation for dependence injection.
    @Autowired
    public ExternalPersonController(ExternalPersonDAO externalPersonDAO){
        this.externalPersonDAO = externalPersonDAO;
    }

    //----- CREATE NEW EXTERNAL PERSON METHOD -------
    //Mapping for the HTTP Post verbe.
    @PostMapping
    //Response Entity is a class that controls the reply HTTP(header,body and status). 
    public ResponseEntity<ExternalPerson> createExternalPerson(@RequestBody ExternalPerson externalPerson){
        ExternalPerson newExternalPerson = externalPersonDAO.create(externalPerson);
        //Return the status 201 Created and the object created.
        return new ResponseEntity<>(newExternalPerson, HttpStatus.CREATED);
    }

    //----- LIST EXTERNAL PERSON METHOD -------
    //Mapping for the HTTP Get verbe.
    @GetMapping
    public ResponseEntity<List<ExternalPerson>> listAllExternalPerson(){
        List<ExternalPerson> externalPersons = externalPersonDAO.listAll();
        //Return 200 OK and the external person list.
        return ResponseEntity.ok(externalPersons);
    }

    //----- READ EXTERNAL PERSON FOR THE ID METHOD -------
    @GetMapping("/{id}")
    //@PathVariable maps the {id} for the Long id.
    public ResponseEntity<ExternalPerson> readExternalPerson(@PathVariable Long id){
        ExternalPerson externalPerson = externalPersonDAO.read(id);
        if (externalPerson != null) {
            // Return the status 200 OK and the founded external person.
            return ResponseEntity.ok(externalPerson);
        } else {
            // Return the status 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    //----- UPDATE EXTERNAL PERSON FOR THE ID METHOD -------
    @PutMapping("/{id}")
    public ResponseEntity<ExternalPerson> updateExternalPerson(@PathVariable long id ,@RequestBody ExternalPerson externalPerson){
        ExternalPerson updatedExternalPerson = externalPersonDAO.update(id, externalPerson);

        //If external person is not null, it means that the object was updated, and return 200 OK.
        if(updatedExternalPerson != null){
            return ResponseEntity.ok(updatedExternalPerson);
        }

        //If external person is null, the system assumes it was a Bad Request case and return 400 Bad Request or 500 Internal Server Error.
        else{
            return ResponseEntity.notFound().build();
        }
    }

    //----- DELETE EXTERNAL PERSON FOR THE ID METHOD -------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExternalPerson(@PathVariable long id){
        //Receives the anwser of the method.
        String status = externalPersonDAO.delete(id);
        
        if (status.toLowerCase().contains("success") || 
            status.toLowerCase().contains("deleted")) {
            
            //Return status 204 No Content (but well succed opperetion).
            return ResponseEntity.ok(status); 
            
        } 
        
        // Not founded case:
        else if (status.toLowerCase().contains("not found")) {
            return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
        }
        
        // General error return status 500:
        else if (status.toLowerCase().contains("error")) {
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        else {
            // if any another anwser of the system:
            return ResponseEntity.ok(status);
        }
    }
}
    