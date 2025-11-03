package com.koble.koble.controller;

import com.koble.koble.model.*;
import com.koble.koble.persistence.dataAccessObject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectDAO projectDAO;
    private final ResearchProjectDAO researchDAO;
    private final EducationalProjectDAO educationalDAO;
    private final ExtensionProjectDAO extensionDAO;
    private final TeacherDAO teacherDAO;

    @Autowired
    public ProjectController(ProjectDAO projectDAO, ResearchProjectDAO researchDAO,
                             EducationalProjectDAO educationalDAO, ExtensionProjectDAO extensionDAO,
                             TeacherDAO teacherDAO) {
        this.projectDAO = projectDAO;
        this.researchDAO = researchDAO;
        this.educationalDAO = educationalDAO;
        this.extensionDAO = extensionDAO;
        this.teacherDAO = teacherDAO;
    }

    private boolean validateCoordinator(Project project) {
        if (project.getCoordinator() == null || project.getCoordinator().trim().isEmpty()) {
            return false;
        }
        Teacher teacher = teacherDAO.findByName(project.getCoordinator());
        return teacher != null;
    }

    @PostMapping("/research")
    @Transactional
    public ResponseEntity<ResearchProject> createResearchProject(@RequestBody ResearchProject researchProject) {
        if (!validateCoordinator(researchProject)) {
            return ResponseEntity.badRequest().body(null);
        }

        Project rootProject;
        try {
            rootProject = projectDAO.create(researchProject);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (rootProject == null || rootProject.getId() == 0) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            ResearchProject createdResearch = researchDAO.create(researchProject, rootProject.getId());
            return new ResponseEntity<>(createdResearch, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/educational")
    @Transactional
    public ResponseEntity<EducationalProject> createEducationalProject(@RequestBody EducationalProject educationalProject) {
        if (!validateCoordinator(educationalProject)) {
            return ResponseEntity.badRequest().body(null);
        }

        Project rootProject;
        try {
            rootProject = projectDAO.create(educationalProject);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (rootProject == null || rootProject.getId() == 0) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            EducationalProject createdEducational = educationalDAO.create(educationalProject, rootProject.getId());
            return new ResponseEntity<>(createdEducational, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/extension")
    @Transactional
    public ResponseEntity<ExtensionProject> createExtensionProject(@RequestBody ExtensionProject extensionProject) {
        if (!validateCoordinator(extensionProject)) {
            return ResponseEntity.badRequest().body(null);
        }

        Project rootProject;
        try {
            rootProject = projectDAO.create(extensionProject);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (rootProject == null || rootProject.getId() == 0) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            ExtensionProject createdExtension = extensionDAO.create(extensionProject, rootProject.getId());
            return new ResponseEntity<>(createdExtension, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/research/{id}")
    public ResponseEntity<ResearchProject> readResearchProject(@PathVariable Long id) {
        ResearchProject researchProject = researchDAO.read(id);
        return researchProject != null ? ResponseEntity.ok(researchProject) : ResponseEntity.notFound().build();
    }

    @GetMapping("/educational/{id}")
    public ResponseEntity<EducationalProject> readEducationalProject(@PathVariable Long id) {
        EducationalProject educationalProject = educationalDAO.read(id);
        return educationalProject != null ? ResponseEntity.ok(educationalProject) : ResponseEntity.notFound().build();
    }

    @GetMapping("/extension/{id}")
    public ResponseEntity<ExtensionProject> readExtensionProject(@PathVariable Long id) {
        ExtensionProject extensionProject = extensionDAO.read(id);
        return extensionProject != null ? ResponseEntity.ok(extensionProject) : ResponseEntity.notFound().build();
    }

    @GetMapping("/research")
    public ResponseEntity<List<ResearchProject>> readAllResearchProjects() {
        return ResponseEntity.ok(researchDAO.listAll());
    }

    @GetMapping("/educational")
    public ResponseEntity<List<EducationalProject>> readAllEducationalProjects() {
        return ResponseEntity.ok(educationalDAO.listAll());
    }

    @GetMapping("/extension")
    public ResponseEntity<List<ExtensionProject>> readAllExtensionProjects() {
        return ResponseEntity.ok(extensionDAO.listAll());
    }

    @GetMapping
    public ResponseEntity<List<Project>> readAllProjects() {
        return ResponseEntity.ok(projectDAO.listAll());
    }

    @PutMapping("/research/{id}")
    @Transactional
    public ResponseEntity<ResearchProject> updateResearchProject(@PathVariable long id, @RequestBody ResearchProject newProject) {
        projectDAO.update(id, newProject);
        ResearchProject updated = researchDAO.update(id, newProject);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/educational/{id}")
    @Transactional
    public ResponseEntity<EducationalProject> updateEducationalProject(@PathVariable long id, @RequestBody EducationalProject newProject) {
        projectDAO.update(id, newProject);
        EducationalProject updated = educationalDAO.update(id, newProject);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/extension/{id}")
    @Transactional
    public ResponseEntity<ExtensionProject> updateExtensionProject(@PathVariable long id, @RequestBody ExtensionProject newProject) {
        projectDAO.update(id, newProject);
        ExtensionProject updated = extensionDAO.update(id, newProject);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/research/{id}")
    @Transactional
    public ResponseEntity<String> deleteResearch(@PathVariable long id) {
        return handleDelete(researchDAO.delete(id), projectDAO.delete(id));
    }

    @DeleteMapping("/educational/{id}")
    @Transactional
    public ResponseEntity<String> deleteEducational(@PathVariable long id) {
        return handleDelete(educationalDAO.delete(id), projectDAO.delete(id));
    }

    @DeleteMapping("/extension/{id}")
    @Transactional
    public ResponseEntity<String> deleteExtension(@PathVariable long id) {
        return handleDelete(extensionDAO.delete(id), projectDAO.delete(id));
    }

    private ResponseEntity<String> handleDelete(String statusSpecific, String statusProject) {
        if (statusSpecific == null) return new ResponseEntity<>("Error deleting project", HttpStatus.INTERNAL_SERVER_ERROR);
        String lower = statusSpecific.toLowerCase();

        if (lower.contains("success") || lower.contains("deleted")) {
            return ResponseEntity.ok(statusProject);
        } else if (lower.contains("not found")) {
            return new ResponseEntity<>(statusSpecific, HttpStatus.NOT_FOUND);
        } else if (lower.contains("error")) {
            return new ResponseEntity<>(statusSpecific, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(statusSpecific);
    }
}
