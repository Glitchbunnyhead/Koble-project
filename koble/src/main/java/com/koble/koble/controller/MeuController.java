//----THIS IS A TEST CLASS, AND DOES NOT BELONGS TO THE FINAL VERSION OF THE SYSTEM------

package com.koble.koble.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.koble.koble.model.Company;
import com.koble.koble.persistence.dataAccessObject.CompanyDAO;

@RestController
@RequestMapping("/api") // Mapeamento Base: /api
public class MeuController {
    
    private final CompanyDAO labibiCompanyDAO;

     public MeuController(CompanyDAO labibiCompanyDAO) {
        this.labibiCompanyDAO = labibiCompanyDAO;
    }   
    
    
}