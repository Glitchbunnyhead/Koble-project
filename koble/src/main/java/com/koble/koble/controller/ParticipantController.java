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
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<Participant>> listAllParticipants() {
		List<Participant> participants = participantDAO.listAll();
		return ResponseEntity.ok(participants);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Participant> readParticipant(@PathVariable Long id) {
		Participant participant = participantDAO.read(id);
		if (participant != null) {
			return ResponseEntity.ok(participant);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Participant> updateParticipant(@PathVariable long id, @RequestBody Participant participant) {
		Participant updated = participantDAO.update(id, participant);
		if (updated != null) {
			return ResponseEntity.ok(updated);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteParticipant(@PathVariable long id) {
		String status = participantDAO.delete(id);

		if (status == null) {
			return new ResponseEntity<>("Error deleting participant", HttpStatus.INTERNAL_SERVER_ERROR);
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

    @GetMapping("/project/{id}")
    public ResponseEntity<Project> getProjectForParticipant(@PathVariable Long id) {
        Project project = participantDAO.getProjectByParticipant(id);
        if (project != null) return ResponseEntity.ok(project);
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/byProject/{projectId}")
    public ResponseEntity<List<Participant>> listParticipantsByProject(@PathVariable Long projectId) {
        List<Participant> participants = participantDAO.listParticipantsByProject(projectId);
        if (participants.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(participants);
    }
}