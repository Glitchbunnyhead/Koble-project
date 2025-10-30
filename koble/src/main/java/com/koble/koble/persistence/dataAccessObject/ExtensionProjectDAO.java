package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.ExtensionProject; // Importa a Model de Extensão
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ExtensionProjectDAO {

    private final MySqlConnection connection;

    // Construtor com injeção de dependência da conexão
    public ExtensionProjectDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    /**
     * Salva os atributos adicionais do projeto de extensão na tabela específica.
     * @param extensionProject O objeto ExtensionProject com os dados.
     * @param idProject O ID gerado na tabela 'projetos' (Chave Estrangeira/Primária).
     * @return O objeto ExtensionProject salvo.
     */
    public ExtensionProject create(ExtensionProject extensionProject, long idProject) throws SQLException {
        this.connection.openConnection();

        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_EXTENTIONPROJECT + " ("
                + ConstantsDataBase.PROJECT_COLUNA_ID + ", "
                + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE + ", " // Presumindo que você tenha essa constante
                + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS + ", "            // Presumindo que você tenha essa constante
                + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS + ") VALUES (?, ?, ?, ?)"; // Presumindo que você tenha essa constante

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {

            // 1. O ID do Projeto Base (FK)
            st.setLong(1, idProject); 
            // 2. Atributos Específicos de ExtensionProject
            st.setString(2, extensionProject.getTargetAudience());
            st.setInt(3, extensionProject.getSlots());
            st.setString(4, extensionProject.getSelectionProcess());

            st.executeUpdate();

            System.out.println("Extension Project created successfully");
            
            // É uma boa prática setar o ID aqui (embora já venha do Controller), para que o objeto retornado esteja completo.
            extensionProject.setId(idProject);
            return extensionProject;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating Extension Project: " + e.getMessage());
            // Lança a exceção para que o Controller possa gerenciar o rollback transacional
            throw e; 
        } finally {
            connection.closeConnection();
        }
    }

    /**
     * Deleta o registro da tabela de extensão usando o ID do projeto base.
     */
    public String delete(long id) {
        this.connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_EXTENTIONPROJECT +
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            st.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting Extension Project: " + e.getMessage();
        } finally {
            connection.closeConnection();
        }

        return "Extension Project deleted successfully";
    }

    /**
     * Atualiza os atributos adicionais do projeto de extensão.
     */
    public ExtensionProject update(long id, ExtensionProject extensionProject) {
        if (id <= 0) {
            System.out.println("Error updating Extension Project: Project ID is missing or invalid.");
            return null;
        }

        this.connection.openConnection();

        String sql = "UPDATE " + ConstantsDataBase.TABLE_EXTENTIONPROJECT + " SET "
                + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS + " = ?, "
                + ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS + " = ? "
                + "WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {

            st.setString(1, extensionProject.getTargetAudience());
            st.setInt(2, extensionProject.getSlots());
            st.setString(3, extensionProject.getSelectionProcess());
            st.setLong(4, id);


            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Extension Project updated successfully with ID: " + id);
                return extensionProject;
            } else {
                System.out.println("Extension Project with Project ID " + id + " not found for update.");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating Extension Project: " + e.getMessage());
            return null;
        } finally {
            connection.closeConnection();
        }
    }

    /**
     * Busca os atributos adicionais do projeto de extensão.
     */
    public ExtensionProject read(long id) {
        ExtensionProject extensionProject  = null;
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_EXTENTIONPROJECT +
                     " WHERE " + ConstantsDataBase.PROJECT_COLUNA_ID + " = ?;";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {
                    extensionProject = new ExtensionProject();
                    // Setando o ID do Projeto Base
                    extensionProject.setId(id);
                    // Setando os atributos específicos
                    extensionProject.setTargetAudience(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE));
                    extensionProject.setSlots(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS));
                    extensionProject.setSelectionProcess(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error reading Extension Project: " + e.getMessage());
        } finally {
            connection.closeConnection();
        }

        return extensionProject;
    }

    /**
     * Lista todos os registros de projetos de extensão.
     */
    public List<ExtensionProject> listAll() {
        List<ExtensionProject> extensionProjects = new ArrayList<>();
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_EXTENTIONPROJECT + ";";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                ExtensionProject extensionProject = new ExtensionProject();
                // O ID do projeto base também deve ser carregado para consistência
                extensionProject.setId(rs.getLong(ConstantsDataBase.PROJECT_COLUNA_ID)); 
                extensionProject.setTargetAudience(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_TARGET_AUDIENCE));
                extensionProject.setSlots(rs.getInt(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SLOTS));
                extensionProject.setSelectionProcess(rs.getString(ConstantsDataBase.PROJECT_COLUNA_EXTENSION_SELECTION_PROCESS));

                extensionProjects.add(extensionProject);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error listing Extension Projects: " + e.getMessage());
        } finally {
            connection.closeConnection();
        }

        return extensionProjects;
    }
}