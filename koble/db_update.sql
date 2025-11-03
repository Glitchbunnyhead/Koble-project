-- Remover tabela de junção desnecessária
DROP TABLE IF EXISTS teacher_project;

-- Atualizar a tabela project para garantir que coordinator seja único e referencia um professor
ALTER TABLE project 
MODIFY COLUMN coordinator VARCHAR(60) NOT NULL,
ADD CONSTRAINT unique_coordinator UNIQUE (coordinator),
ADD CONSTRAINT fk_coordinator_teacher 
FOREIGN KEY (coordinator) REFERENCES teacher_staff(name);