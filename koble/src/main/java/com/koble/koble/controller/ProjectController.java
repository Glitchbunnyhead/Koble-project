package com.koble.koble.controller;

import com.koble.koble.model.Project;
import com.koble.koble.model.ResearchProject;
import com.koble.koble.persistence.dataAccessObject.ProjectDAO;
import com.koble.koble.persistence.dataAccessObject.ResearchProjectDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; // Importe o Transacional
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project") // Mudei para /project para ser mais genérico
public class ProjectController { 
    
    // Injeção dos DAOs (agora no Controller)
    private final ProjectDAO projectDAO;
    private final ResearchProjectDAO researchDAO;

    @Autowired
    public ProjectController(ProjectDAO projectDAO, ResearchProjectDAO researchDAO) {
        this.projectDAO = projectDAO;
        this.researchDAO = researchDAO;
    }

    /**
     * Cria um novo ResearchProject e Project base em uma única transação atômica.
     * * @param researchProject Os dados do ResearchProject recebidos no corpo da requisição.
     * @return ResponseEntity com o projeto criado ou um erro.
     */
    @PostMapping("/research")
    @Transactional // A anotação @Transactional vai aqui, no Controller!
    public ResponseEntity<ResearchProject> createResearchProject(@RequestBody ResearchProject researchProject) {
        
        // 1. Inserir na tabela base ('project')
        Project baseProject = null;
        try {
            baseProject = projectDAO.create(researchProject);
        } catch (RuntimeException e) {
            // A exceção de banco de dados do DAO já foi lançada. 
            // O @Transactional fará o ROLLBACK.
            System.err.println("Erro na criação da base do projeto. Rollback será executado.");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        if (baseProject == null || baseProject.getId() == 0) {
            // Isso só deve acontecer se o DAO retornar null, mas o ROLLBACK já deve ter ocorrido se houver exceção.
             return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        long projectId = baseProject.getId(); 

        // 2. Inserir na tabela específica ('research') usando o ID gerado
        try {
            // O DAO de ResearchProject lança SQLException, que o Spring converte em RuntimeException
            // ou, neste contexto, o @Transactional a detecta para o rollback.
            ResearchProject createdResearch = researchDAO.create(researchProject, projectId);
            
            // Se tudo correr bem até aqui, o Spring fará o COMMIT ao sair do método.
            return new ResponseEntity<>(createdResearch, HttpStatus.CREATED);

        } catch (Exception e) {
            // Captura qualquer exceção (incluindo SQL rollbacks) e retorna 500.
            // O @Transactional já foi acionado para fazer o ROLLBACK.
            System.err.println("Erro na criação do detalhe do projeto. Rollback total será executado.");
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Outros métodos HTTP (GET, PUT, DELETE) para ProjectDAO iriam aqui
}