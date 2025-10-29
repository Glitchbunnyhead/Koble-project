
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

import com.koble.koble.model.Teacher;
import com.koble.koble.persistence.dataAccessObject.TeacherDAO;


//It is an annotation that combine two other spring annotation: @Controller and @ResponseBody.
//@Controller recieves the responsible for receiving user requests, processing them and returning a response.
//@ResponseBody indicates that the response is connected to the body of the HTTP protocole(JSON).
//@RestController works like an simplified way of the RestApi.
@RestController
//@RequestMapping maps the HTTP requests and calls the referenced function.
@RequestMapping("/api/teacher")
public class TeacherController {
    
    private final TeacherDAO teacherDAO;

    //Annotation for dependence injection.
    @Autowired
    public TeacherController(TeacherDAO teacherDAO){
        this.teacherDAO = teacherDAO;
    }

    //----- CREATE NEW TEACHER METHOD -------
    //Mapping for the HTTP Post verbe.
    @PostMapping
    //Response Entity is a class that controls the reply HTTP(header,body and status). 
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher){
        Teacher newTeacher = teacherDAO.create(teacher);
        if (newTeacher == null) {
            //Return status 500 Internal Server Error if the object was not created.
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
            //Return the status 201 Created and the object created.
            return new ResponseEntity<>(newTeacher, HttpStatus.CREATED);
    }

    //----- LIST TEACHER METHOD -------
    //Mapping for the HTTP Get verbe.
    @GetMapping
    public ResponseEntity<List<Teacher>> listAllTeacher(){
        List<Teacher> teacher = teacherDAO.listAll();
        //Return 200 OK and the teacher list.
        return ResponseEntity.ok(teacher);
    }

    //----- READ TEACHER FOR THE ID METHOD -------
    @GetMapping("/{id}")
    //@PathVariable maps the {id} for the Long id.
    public ResponseEntity<Teacher> readTeacher(@PathVariable Long id){
        Teacher teacher = teacherDAO.read(id);
        if (teacher != null) {
            // Return the status 200 OK and the founded teacher.
            return ResponseEntity.ok(teacher);
        } else {
            // Return the status 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    //----- UPDATE TEACHER FOR THE ID METHOD -------
    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable long id ,@RequestBody Teacher teacher){
        Teacher updateTeacher = teacherDAO.update(id, teacher);

        //If teacher is not null, it means that the object was updated, and return 200 OK.
        if(updateTeacher != null){
            return ResponseEntity.ok(updateTeacher);
        }

        //If teacher is null, the system assumes it was a Bad Request case and return 400 Bad Request or 500 Internal Server Error.
        else{
            return ResponseEntity.notFound().build();
        }
    }

    //----- DELETE TEACHER FOR THE ID METHOD -------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeacher(@PathVariable long id){
        //Receives the anwser of the method.
        String status = teacherDAO.delete(id);
        
        if (status.toLowerCase().contains("success") || 
            status.toLowerCase().contains("deleted")) {
            
            //Return status 204 No Content (but well succed opperetion).
            return ResponseEntity.ok(status); 
            
        } 
        
        // Not founded case:
        else if (status.toLowerCase().contains("not found")) {
            // Retorna a mensagem de erro (Não Encontrado) com o status 404
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
