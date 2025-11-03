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

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherDAO teacherDAO;

    @Autowired
    public TeacherController(TeacherDAO teacherDAO) {
        this.teacherDAO = teacherDAO;
    }

    @PostMapping
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        Teacher newTeacher = teacherDAO.create(teacher);
        if (newTeacher == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(newTeacher, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Teacher>> listAllTeachers() {
        List<Teacher> teachers = teacherDAO.listAll();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> readTeacher(@PathVariable Long id) {
        Teacher teacher = teacherDAO.read(id);
        if (teacher != null) {
            return ResponseEntity.ok(teacher);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable long id, @RequestBody Teacher teacher) {
        Teacher updatedTeacher = teacherDAO.update(id, teacher);
        if (updatedTeacher != null) {
            return ResponseEntity.ok(updatedTeacher);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeacher(@PathVariable long id) {
        String status = teacherDAO.delete(id);
        String lowerStatus = status.toLowerCase();

        if (lowerStatus.contains("success") || lowerStatus.contains("deleted")) {
            return ResponseEntity.ok(status);
        } else if (lowerStatus.contains("not found")) {
            return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
        } else if (lowerStatus.contains("error")) {
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(status);
    }
}
