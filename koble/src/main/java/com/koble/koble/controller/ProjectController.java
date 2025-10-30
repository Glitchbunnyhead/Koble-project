package com.koble.koble.controller;

import com.koble.koble.model.ResearchProject;
import com.koble.koble.model.Project;
import com.koble.koble.model.EducationalProject;
import com.koble.koble.model.ExtensionProject;
import com.koble.koble.model.ResearchProject;
import com.koble.koble.persistence.dataAccessObject.ProjectDAO; // Presumindo que você tem essa classe DAO
import com.koble.koble.persistence.dataAccessObject.ResearchProjectDAO;
import com.koble.koble.persistence.dataAccessObject.EducationalProjectDAO;
import com.koble.koble.persistence.dataAccessObject.ExtensionProjectDAO;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    // Injeção de todos os DAOs necessários para a orquestração
    
    @Autowired
    private ProjectDAO projectDAO; // Para a tabela 'projetos' (base)

    @Autowired
    private ResearchProjectDAO researchDAO; // Para a tabela 'projetos_research' (tipada)

    // Nota: Você injetaria também o EducationalProjectDAO e o ExtensionProjectDAO aqui.
    @Autowired
    private EducationalProjectDAO educationalDAO;

    @Autowired
    private ExtensionProjectDAO extensionDAO;


    /**
     * Endpoint para criar um novo Projeto de Pesquisa.
     * Recebe o objeto ResearchProject completo no corpo da requisição.
     */
    @PostMapping("/research")
    @Transactional 
    public ResponseEntity<ResearchProject> criarProjetoPesquisa(@RequestBody ResearchProject newResearchProject) {

        try {
            // 1. INSERÇÃO NA TABELA BASE: Salvar os atributos comuns e obter o ID gerado.
            // O projetoDAO.salvar() deve retornar o objeto com o ID preenchido.
            // Nota: O método salvar deve aceitar o tipo Project ou o tipo ResearchProject (subclasse).
            Project rootProject = projectDAO.create(newResearchProject);
            
            long idProject = rootProject.getId();
            
            if (idProject <= 0) {
                // Deve haver uma exceção no DAO, mas é um bom guardrail
                return new ResponseEntity("Persistente error: id not genereted.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 2. INSERÇÃO NA TABELA TIPADA: Usar o ID gerado para salvar os atributos específicos.
            // Como o objeto 'newResearchProject' já é um ResearchProject, ele contém todos os dados.
            // O DAO específico só precisa do ID para a Foreign Key.
            ResearchProject researchProject = researchDAO.create(newResearchProject, idProject);

            // 3. Resposta de sucesso (com o objeto completo e o ID)
            return new ResponseEntity<>(researchProject, HttpStatus.CREATED);

        } catch (SQLException e) {
            // O erro é capturado e, graças ao @Transactional, o banco de dados
            // fará rollback automaticamente.
            System.err.println("Erro ao criar Projeto de Pesquisa: " + e.getMessage());
            return new ResponseEntity("Erro ao criar projeto: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
             System.err.println("Erro inesperado: " + e.getMessage());
            return new ResponseEntity("Erro inesperado no servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Você faria métodos POST similares para "/projetos/ensino" e "/projetos/extensao".
    // ...
}