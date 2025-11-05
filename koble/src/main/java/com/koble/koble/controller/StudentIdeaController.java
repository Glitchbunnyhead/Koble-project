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

import com.koble.koble.model.StudentIdea;
import com.koble.koble.model.Idea;
import com.koble.koble.model.Student;
import com.koble.koble.persistence.dataAccessObject.StudentDAO;
import com.koble.koble.persistence.dataAccessObject.IdeaDAO;
import com.koble.koble.persistence.dataAccessObject.StudentIdeaDAO;

@RestController
@RequestMapping("/api/student_idea")
public class StudentIdeaController {

    private final StudentIdeaDAO studentIdeaDAO;
    private final StudentDAO studentDAO;
    private final IdeaDAO ideaDAO;

    @Autowired
    public StudentIdeaController(StudentIdeaDAO studentIdeaDAO, StudentDAO studentDAO, IdeaDAO ideaDAO) {
        this.studentIdeaDAO = studentIdeaDAO;
        this.studentDAO = studentDAO;
        this.ideaDAO = ideaDAO;
    }

    @PostMapping
    public ResponseEntity<StudentIdea> create(@RequestBody StudentIdea studentIdea) {
        boolean studentExists = studentDAO.read(studentIdea.getStudentId()) != null;
        boolean ideaExists = ideaDAO.read(studentIdea.getIdeaId()) != null;

        if (!studentExists || !ideaExists) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        StudentIdea createdStudentIdea = studentIdeaDAO.create(studentIdea);
        if (createdStudentIdea == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(createdStudentIdea, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudentIdea>> listAll() {
        List<StudentIdea> studentIdeas = studentIdeaDAO.listAll();
        return ResponseEntity.ok(studentIdeas);
    }

    @GetMapping("/ideas/{studentId}")
    public ResponseEntity<List<Idea>> findIdeasByStudentId(@PathVariable Long studentId) {
        List<Idea> ideas = studentIdeaDAO.findIdeasByStudentId(studentId);
        return ideas.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(ideas);
    }

    @GetMapping("/students/{ideaId}")
    public ResponseEntity<List<Student>> findStudentsByIdeaId(@PathVariable Long ideaId) {
        List<Student> students = studentIdeaDAO.findStudentsByIdeaId(ideaId);
        return students.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(students);
    }

    @DeleteMapping("/{studentId}/{ideaId}")
    public ResponseEntity<String> delete(@PathVariable long studentId, @PathVariable long ideaId) {
        if (!studentIdeaDAO.exists(ideaId, studentId)) {
            return new ResponseEntity<>("Student-idea relationship not found", HttpStatus.NOT_FOUND);
        }

        String status = studentIdeaDAO.delete(ideaId, studentId);
        return buildResponse(status);
    }

    @DeleteMapping("/student/{studentId}")
    public ResponseEntity<String> deleteAllByStudentId(@PathVariable long studentId) {
        String status = studentIdeaDAO.deleteAllByStudentId(studentId);
        return buildResponse(status);
    }

    @DeleteMapping("/idea/{ideaId}")
    public ResponseEntity<String> deleteAllByIdeaId(@PathVariable long ideaId) {
        String status = studentIdeaDAO.deleteAllByIdeaId(ideaId);
        return buildResponse(status);
    }

    private ResponseEntity<String> buildResponse(String status) {
        String result = status.toLowerCase();

        if (result.contains("success") || result.contains("deleted")) {
            return ResponseEntity.ok(status);
        } else if (result.contains("not found")) {
            return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
        } else if (result.contains("error")) {
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(status);
    }
}
