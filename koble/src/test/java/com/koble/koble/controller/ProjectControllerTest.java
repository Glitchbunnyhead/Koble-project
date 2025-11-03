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
import static org.mockito.Mockito.*;
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
    void createResearchProject_WithValidCoordinator_ShouldSucceed() throws SQLException {
        ResearchProject project = new ResearchProject();
        project.setCoordinator("John Doe");
        project.setTitle("Research Project 1");
        project.setDescription("Test research project");
        project.setId(1L);

        Teacher mockTeacher = new Teacher();
        mockTeacher.setName("John Doe");
        mockTeacher.setId(1L);

        when(teacherDAO.findByName("John Doe")).thenReturn(mockTeacher);
        when(projectDAO.create(any(ResearchProject.class))).thenReturn(project);
        when(researchDAO.create(any(ResearchProject.class), any(Long.class))).thenReturn(project);

        ResponseEntity<ResearchProject> response = projectController.createResearchProject(project);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getCoordinator());
        
        verify(teacherDAO).findByName("John Doe");
        verify(projectDAO).create(project);
        verify(researchDAO).create(project, project.getId());
    }

    @Test
    void createResearchProject_WithNonexistentCoordinator_ShouldFail() {
        ResearchProject project = new ResearchProject();
        project.setCoordinator("Nonexistent Teacher");
        project.setTitle("Research Project 2");
        project.setDescription("Test research project");

        when(teacherDAO.findByName("Nonexistent Teacher")).thenReturn(null);

        ResponseEntity<ResearchProject> response = projectController.createResearchProject(project);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(teacherDAO).findByName("Nonexistent Teacher");
        verify(projectDAO, never()).create(any(ResearchProject.class));
        verify(researchDAO, never()).create(any(ResearchProject.class), any(Long.class));
    }

    @Test
    void createResearchProject_WithNullCoordinator_ShouldFail() {
        ResearchProject project = new ResearchProject();
        project.setCoordinator(null);
        project.setTitle("Research Project 3");
        project.setDescription("Test research project");

        ResponseEntity<ResearchProject> response = projectController.createResearchProject(project);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(teacherDAO, never()).findByName(any());
        verify(projectDAO, never()).create(any());
        verify(researchDAO, never()).create(any(), any());
    }

    @Test
    void createResearchProject_WithEmptyCoordinator_ShouldFail() {
        ResearchProject project = new ResearchProject();
        project.setCoordinator("");
        project.setTitle("Research Project 4");
        project.setDescription("Test research project");

        ResponseEntity<ResearchProject> response = projectController.createResearchProject(project);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(teacherDAO, never()).findByName(any());
        verify(projectDAO, never()).create(any());
        verify(researchDAO, never()).create(any(), any());
    }
    
    @Test
    void createResearchProject_WhenDatabaseError_ShouldReturnInternalServerError() throws SQLException {
        ResearchProject project = new ResearchProject();
        project.setCoordinator("John Doe");
        project.setTitle("Research Project 5");
        project.setDescription("Test research project");

        Teacher mockTeacher = new Teacher();
        mockTeacher.setName("John Doe");
        mockTeacher.setId(1L);

        when(teacherDAO.findByName("John Doe")).thenReturn(mockTeacher);
        when(projectDAO.create(any(ResearchProject.class))).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<ResearchProject> response = projectController.createResearchProject(project);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(teacherDAO).findByName("John Doe");
        verify(projectDAO).create(project);
        verify(researchDAO, never()).create(any(), any());
    }
    
    @Test
    void createResearchProject_WhenResearchDAOThrowsSQLException_ShouldReturnInternalServerError() throws SQLException {
        ResearchProject project = new ResearchProject();
        project.setCoordinator("John Doe");
        project.setTitle("Research Project 6");
        project.setDescription("Test research project");
        project.setId(1L);

        Teacher mockTeacher = new Teacher();
        mockTeacher.setName("John Doe");
        mockTeacher.setId(1L);

        when(teacherDAO.findByName("John Doe")).thenReturn(mockTeacher);
        when(projectDAO.create(any(ResearchProject.class))).thenReturn(project);
        when(researchDAO.create(any(ResearchProject.class), any(Long.class)))
            .thenThrow(new SQLException("Database error"));

        ResponseEntity<ResearchProject> response = projectController.createResearchProject(project);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(teacherDAO).findByName("John Doe");
        verify(projectDAO).create(project);
        verify(researchDAO).create(project, project.getId());
    }
}