package com.koble.koble.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.koble.koble.model.Idea;
import com.koble.koble.model.Student;
import com.koble.koble.model.Teacher;
import com.koble.koble.persistence.dataAccessObject.IdeaDAO;

@RestController
@RequestMapping("/api/idea")
public class IdeaController {

    private final IdeaDAO ideaDAO;

    @Autowired
    public IdeaController(IdeaDAO ideaDAO) {
        this.ideaDAO = ideaDAO;
    }

    @PostMapping
    public ResponseEntity<Idea> createIdea(@RequestBody Idea idea) {
        Idea created = ideaDAO.create(idea);
        if (created == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Idea>> listAllIdeas() {
        return ResponseEntity.ok(ideaDAO.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Idea> readIdea(@PathVariable Long id) {
        Idea idea = ideaDAO.read(id);
        return (idea != null) ? ResponseEntity.ok(idea) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Idea> updateIdea(@PathVariable long id, @RequestBody Idea idea) {
        Idea updated = ideaDAO.update(id, idea);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIdea(@PathVariable long id) {
        String status = ideaDAO.delete(id);
        if (status == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting idea");
        }

        String lower = status.toLowerCase();
        if (lower.contains("success") || lower.contains("deleted")) {
            return ResponseEntity.ok(status);
        } else if (lower.contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(status);
        } else if (lower.contains("error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
        return ResponseEntity.ok(status);
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<Student> getStudentForIdea(@PathVariable Long id) {
        Idea idea = ideaDAO.read(id);
        if (idea == null) {
            return ResponseEntity.notFound().build();
        }

        Student student = ideaDAO.getStudentByIdea(idea);
        return (student != null) ? ResponseEntity.ok(student) : ResponseEntity.notFound().build();
    }

    @GetMapping("/teacher/{id}")
    public ResponseEntity<Teacher> getTeacherForIdea(@PathVariable Long id) {
        Idea idea = ideaDAO.read(id);
        if (idea == null) {
            return ResponseEntity.notFound().build();
        }

        Teacher teacher = ideaDAO.getTeacherByIdea(idea);
        return (teacher != null) ? ResponseEntity.ok(teacher) : ResponseEntity.notFound().build();
    }
}