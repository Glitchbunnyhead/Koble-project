package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.ExternalPerson;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExternalPersonDAO implements Crudl<ExternalPerson> {

    private final MySqlConnection connection;

    public ExternalPersonDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    @Override
    public ExternalPerson create(ExternalPerson externalPerson) {
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_EXTERNALPERSON +
                " (" + ConstantsDataBase.COLUMN_NAME + ", " +
                ConstantsDataBase.COLUMN_EMAIL + ", " +
                ConstantsDataBase.COLUMN_PASSWORD + ", " +
                ConstantsDataBase.COLUMN_PHONE + ") VALUES (?, ?, ?, ?)";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setString(1, externalPerson.getName());
            st.setString(2, externalPerson.getEmail());
            st.setString(3, externalPerson.getPassword());
            st.setString(4, externalPerson.getPhoneNumber());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar external user: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return externalPerson;
    }

    @Override
    public String delete(long id) {
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_EXTERNALPERSON +
                     " WHERE " + ConstantsDataBase.EXTERNAL_PERSON_COLUNA_ID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar external user: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return "External user deletado com sucesso";
    }

    @Override
    public ExternalPerson update(long id, ExternalPerson externalPerson) {
        if (externalPerson == null) {
            throw new RuntimeException("Erro ao atualizar external user: objeto nulo");
        }

        String sql = "UPDATE " + ConstantsDataBase.TABLE_EXTERNALPERSON +
                " SET " + ConstantsDataBase.COLUMN_NAME + " = ?, " +
                ConstantsDataBase.COLUMN_EMAIL + " = ?, " +
                ConstantsDataBase.COLUMN_PASSWORD + " = ?, " +
                ConstantsDataBase.COLUMN_PHONE + " = ? " +
                "WHERE " + ConstantsDataBase.EXTERNAL_PERSON_COLUNA_ID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setString(1, externalPerson.getName());
            st.setString(2, externalPerson.getEmail());
            st.setString(3, externalPerson.getPassword());
            st.setString(4, externalPerson.getPhoneNumber());
            st.setLong(5, id);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("External user com ID " + id + " não encontrado para atualização.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar external user: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return externalPerson;
    }

    @Override
    public ExternalPerson read(long id) {
        ExternalPerson externalPerson = null;
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_EXTERNALPERSON +
                     " WHERE " + ConstantsDataBase.EXTERNAL_PERSON_COLUNA_ID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    externalPerson = mapResultSetToExternalPerson(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler external user: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }

        return externalPerson;
    }

    @Override
    public List<ExternalPerson> listAll() {
        List<ExternalPerson> externalPeople = new ArrayList<>();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_EXTERNALPERSON;

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                externalPeople.add(mapResultSetToExternalPerson(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar external users: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }

        return externalPeople;
    }

    private ExternalPerson mapResultSetToExternalPerson(ResultSet rs) throws SQLException {
        ExternalPerson externalPerson = new ExternalPerson();
        externalPerson.setId(rs.getLong(ConstantsDataBase.EXTERNAL_PERSON_COLUNA_ID));
        externalPerson.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
        externalPerson.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
        externalPerson.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
        externalPerson.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
        return externalPerson;
    }
}
