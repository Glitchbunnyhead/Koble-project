workspace "Koble" "Modelo C4" {
  !identifiers hierarchical

  model {
    teacher = person "Teacher" "Docente que coordena e consulta projetos"
    student = person "Student" "Aluno que participa/propõe ideias/projetos"
    company = person "Company" "Empresa parceira"
    external = person "External Person" "Usuário externo"

    koble = softwareSystem "Koble" "Back-end de projetos e ideias" {
      api = container "Koble API" "Spring Boot REST API" "Java/Spring Boot"
      db  = container "Database" "Persistência de dados" "MySQL" "Database"

      container api {
        controllers = component "Controllers" "Endpoints REST" "Spring MVC"
        daos        = component "DAOs" "Acesso JDBC" "JDBC"
        models      = component "Models" "POJOs de domínio" "Java"
        controllers -> daos "Usa"
        daos -> db "Lê/Escreve (JDBC)"
      }

      teacher -> api "Usa" "HTTP/JSON"
      student -> api "Usa" "HTTP/JSON"
      company -> api "Usa" "HTTP/JSON"
      external -> api "Usa" "HTTP/JSON"
      api -> db "Lê/Escreve" "JDBC"
    }
  }

  views {
    systemContext koble "C1-SystemContext" {
      include *
      autoLayout
    }

    container koble "C2-Containers" {
      include *
      autoLayout
    }

    component api "C3-Components-API" {
      include *
      autoLayout
    }

    styles {
      element "Database" {
        shape cylinder
      }
      element "Person" {
        background #08427b
        color #ffffff
      }
      element "Container" {
        background #1168bd
        color #ffffff
      }
      element "Component" {
        background #438dd5
        color #ffffff
      }
    }
  }
}
