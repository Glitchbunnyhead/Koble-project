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

import com.koble.koble.model.Fellow;
import com.koble.koble.persistence.dataAccessObject.FellowDAO;

@RestController
// Mapeia os requests para "/api/fellow"
@RequestMapping("/api/fellow")
public class FellowController {

    private final FellowDAO fellowDAO;

    // Injeção de dependência do FellowDAO
    @Autowired
    public FellowController(FellowDAO fellowDAO) {
        this.fellowDAO = fellowDAO;
    }

    @PostMapping
    public ResponseEntity<Fellow> createFellow(@RequestBody Fellow fellow) {
        // Tenta criar o objeto Fellow usando o DAO
        Fellow newFellow = fellowDAO.create(fellow);

        if (newFellow == null) {
            // Retorna 500 Internal Server Error se o DAO retornar null (erro na criação ou validação)
            // O DAO já faz a validação e imprime a causa
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Retorna o status 201 Created e o objeto criado
        return new ResponseEntity<>(newFellow, HttpStatus.CREATED);
    }

    
    @GetMapping
    public ResponseEntity<List<Fellow>> listAllFellows() {
        // Busca a lista completa de bolsistas
        List<Fellow> fellows = fellowDAO.listAll();
        // Retorna 200 OK e a lista de bolsistas (pode ser vazia)
        return ResponseEntity.ok(fellows);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Fellow> readFellow(@PathVariable Long id) {
        // Tenta ler o objeto Fellow pelo ID
        Fellow fellow = fellowDAO.read(id);

        if (fellow != null) {
            // Retorna o status 200 OK e o bolsista encontrado
            return ResponseEntity.ok(fellow);
        } else {
            // Retorna o status 404 Not Found se não for encontrado
            return ResponseEntity.notFound().build();
        }
    }

  
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFellow(@PathVariable long id) {
        // Recebe o status da operação de deleção do DAO
        String status = fellowDAO.delete(id);
        
        // O FellowDAO retorna strings com "success", "not found" ou "error"
        if (status.toLowerCase().contains("success") || 
            status.toLowerCase().contains("deleted")) {
            
            // Retorna 200 OK com a mensagem de sucesso
            return ResponseEntity.ok(status); 
            
        } 
        
        // Caso "Not Found":
        else if (status.toLowerCase().contains("no fellow found") || 
                 status.toLowerCase().contains("not found")) {
            return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
        }
        
        // Caso de "Erro" geral:
        else if (status.toLowerCase().contains("error")) {
            return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        else {
            // Retorna 200 OK para outras respostas não mapeadas (improvável)
            return ResponseEntity.ok(status);
        }
    }
}