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
import com.koble.koble.model.Student;
import com.koble.koble.model.Student;
import com.koble.koble.persistence.dataAccessObject.StudentDAO;
import org.springframework.web.bind.annotation.RequestParam;


//It is an annotation that combine two other spring annotation: @Controller and @ResponseBody.
//@Controller recieves the responsible for receiving user requests, processing them and returning a response.
//@ResponseBody indicates that the response is connected to the body of the HTTP protocole(JSON).
//@RestController works like an simplified way of the RestApi.
@RestController
//@RequestMapping maps the HTTP requests and calls the referenced function.
@RequestMapping("/api/student")
public class StudentController {

    private final StudentDAO studentDAO;

    //Annotation for dependence injection
    @Autowired
    public StudentController(StudentDAO studentDAO){
        this.studentDAO = studentDAO;
    }

    //----- CREATE NEW STUDENT METHOD -------
    //Mapping for the HTTP Post verbe.
    @PostMapping
    //Response Entity is a class that controls the reply HTTP(header,body and status). 
    public ResponseEntity<Student> createCompany(@RequestBody Student student){
        Student newStudent = studentDAO.create(student);
        //Return the status 201 Created and the object created.
        return new ResponseEntity<>(newStudent, HttpStatus.CREATED);
    }

    //----- LIST STUDENT METHOD -------
    //Mapping for the HTTP Get verbe.
    @GetMapping
    public ResponseEntity<List<Student>> listAllCompany(){
        List<Student> student = studentDAO.listAll();
        //Return 200 OK and the company list.
        return ResponseEntity.ok(student);
    }

    //----- READ STUDENT FOR THE ID METHOD -------
    @GetMapping("/{id}")
    //@PathVariable maps the {id} for the Long id.
    public ResponseEntity<Student> readStudent(@PathVariable Long id){
        Student student = studentDAO.read(id);
        if (student != null) {
            // Return the status 200 OK and the founded student.
            return ResponseEntity.ok(student);
        } else {
            // Return the status 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    //----- UPDATE STUDENT FOR THE ID METHOD -------
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable long id ,@RequestBody Student student){
        Student updateStudent = studentDAO.update(id, student);

        //If student is not null, it means that the object was updated, and return 200 OK.
        if(updateStudent != null){
            return ResponseEntity.ok(updateStudent);
        }

        //If student is null, the system assumes it was a Bad Request case and return 400 Bad Request or 500 Internal Server Error.
        else{
            return ResponseEntity.notFound().build();
        }
    }
 @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable long id){
        //Receives the anwser of the method.
        String status = studentDAO.delete(id);
        
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
    
    


    
    

    