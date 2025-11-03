package com.koble.koble.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.koble.koble.model.Participant;
import com.koble.koble.model.Project;
import com.koble.koble.persistence.dataAccessObject.ParticipantDAO;

@RestController
@RequestMapping("/api/participant")
public class ParticipantController {

    private final ParticipantDAO participantDAO;

    @Autowired
    public ParticipantController(ParticipantDAO participantDAO) {
        this.participantDAO = participantDAO;
    }

    @PostMapping
    public ResponseEntity<Participant> createParticipant(@RequestBody Participant participant) {
        Participant created = participantDAO.create(participant);
        if (created == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Participant>> listAllParticipants() {
        return ResponseEntity.ok(participantDAO.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participant> readParticipant(@PathVariable Long id) {
        Participant participant = participantDAO.read(id);
        return (participant != null) ? ResponseEntity.ok(participant) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Participant> updateParticipant(@PathVariable long id, @RequestBody Participant participant) {
        Participant updated = participantDAO.update(id, participant);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteParticipant(@PathVariable long id) {
        String status = participantDAO.delete(id);
        if (status == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting participant");
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

    @GetMapping("/project/{id}")
    public ResponseEntity<Project> getProjectForParticipant(@PathVariable Long id) {
        Project project = participantDAO.getProjectByParticipant(id);
        return (project != null) ? ResponseEntity.ok(project) : ResponseEntity.notFound().build();
    }

    @GetMapping("/byProject/{projectId}")
    public ResponseEntity<List<Participant>> listParticipantsByProject(@PathVariable Long projectId) {
        List<Participant> participants = participantDAO.listParticipantsByProject(projectId);
        return participants.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(participants);
    }
}
