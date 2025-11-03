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
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Idea>> listAllIdeas() {
        List<Idea> ideas = ideaDAO.listAll();
        return ResponseEntity.ok(ideas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Idea> readIdea(@PathVariable Long id) {
        Idea idea = ideaDAO.read(id);
        if (idea != null) {
            return ResponseEntity.ok(idea);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Idea> updateIdea(@PathVariable long id, @RequestBody Idea idea) {
        Idea updated = ideaDAO.update(id, idea);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIdea(@PathVariable long id) {
        String status = ideaDAO.delete(id);

        if (status == null) {
            return new ResponseEntity<>("Error deleting idea", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String lower = status.toLowerCase();
        if (lower.contains("success") || lower.contains("deleted")) {
            return ResponseEntity.ok(status);
        } else if (lower.contains("not found")) {
            return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
        } else if (lower.contains("error")) {
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return ResponseEntity.ok(status);
        }
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<Student> getStudentForIdea(@PathVariable Long id) {
        Idea idea = ideaDAO.read(id);
        if (idea == null) return ResponseEntity.notFound().build();

        Student student = ideaDAO.getStudentByIdea(idea);
        if (student != null) return ResponseEntity.ok(student);
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/teacher/{id}")
    public ResponseEntity<Teacher> getTeacherForIdea(@PathVariable Long id) {
        Idea idea = ideaDAO.read(id);
        if (idea == null) return ResponseEntity.notFound().build();

        Teacher teacher = ideaDAO.getTeacherByIdea(idea);
        if (teacher != null) return ResponseEntity.ok(teacher);
        return ResponseEntity.notFound().build();
    }
}

