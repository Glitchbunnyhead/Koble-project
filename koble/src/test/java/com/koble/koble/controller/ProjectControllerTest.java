package com.koble.koble.controller;

import com.koble.koble.model.ResearchProject;
import com.koble.koble.model.Teacher;
import com.koble.koble.persistence.dataAccessObject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectControllerTest {

    @Mock
    private ProjectDAO projectDAO;

    @Mock
    private ResearchProjectDAO researchDAO;

    @Mock
    private EducationalProjectDAO educationalDAO;

    @Mock
    private ExtensionProjectDAO extensionDAO;

    @Mock
    private TeacherDAO teacherDAO;

    private ProjectController projectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        projectController = new ProjectController(projectDAO, researchDAO, educationalDAO, extensionDAO, teacherDAO);
    }

    @Test
    void createResearchProject_WithValidCoordinator_ShouldSucceed() {
        // Arrange
        ResearchProject project = new ResearchProject();
        project.setCoordinator("John Doe");
        project.setTitle("Research Project 1");
        project.setDescription("Test research project");

        Teacher mockTeacher = new Teacher();
        mockTeacher.setName("John Doe");

        when(teacherDAO.findByName("John Doe")).thenReturn(mockTeacher);
        when(projectDAO.create(any(ResearchProject.class))).thenReturn(project);
        try {
            when(researchDAO.create(any(ResearchProject.class), any(Long.class))).thenReturn(project);
        } catch (SQLException e) {
            fail("SQLException should not occur in mock");
        }

        // Act
        ResponseEntity<ResearchProject> response = projectController.createResearchProject(project);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getCoordinator());
    }

    @Test
    void createResearchProject_WithNonexistentCoordinator_ShouldFail() {
        // Arrange
        ResearchProject project = new ResearchProject();
        project.setCoordinator("Nonexistent Teacher");
        project.setTitle("Research Project 2");
        project.setDescription("Test research project");

        when(teacherDAO.findByName("Nonexistent Teacher")).thenReturn(null);

        // Act
        ResponseEntity<ResearchProject> response = projectController.createResearchProject(project);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createResearchProject_WithNullCoordinator_ShouldFail() {
        // Arrange
        ResearchProject project = new ResearchProject();
        project.setCoordinator(null);
        project.setTitle("Research Project 3");
        project.setDescription("Test research project");

        // Act
        ResponseEntity<ResearchProject> response = projectController.createResearchProject(project);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void createResearchProject_WithEmptyCoordinator_ShouldFail() {
        // Arrange
        ResearchProject project = new ResearchProject();
        project.setCoordinator("");
        project.setTitle("Research Project 4");
        project.setDescription("Test research project");

        // Act
        ResponseEntity<ResearchProject> response = projectController.createResearchProject(project);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
}