package com.koble.koble.persistence.dataAccessObject;

import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.koble.koble.model.Company;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class CompanyDAO implements Crudl<Company> {

    private final MySqlConnection connection;

    public CompanyDAO(MySqlConnection connection){
        this.connection = connection;
    }

    @Override
    public Company create(Company company) {
        connection.openConnection();
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_COMPANY +
                " (" + ConstantsDataBase.COMPANY_COLUNA_CNPJ + ", " +
                ConstantsDataBase.COLUMN_NAME + ", " +
                ConstantsDataBase.COLUMN_EMAIL + ", " +
                ConstantsDataBase.COLUMN_PASSWORD + ", " +
                ConstantsDataBase.COLUMN_PHONE + ") VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setString(1, company.getCnpj());
            st.setString(2, company.getName());
            st.setString(3, company.getEmail());
            st.setString(4, company.getPassword());
            st.setString(5, company.getPhoneNumber());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar empresa: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return company;
    }

    @Override
    public String delete(long id) {
        connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_COMPANY +
                " WHERE " + ConstantsDataBase.COMPANY_COLUNA_ID + " = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar empresa: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return "Company deleted successfully";
    }

    @Override
    public Company update(long id, Company company) {
        connection.openConnection();
        String sql = "UPDATE " + ConstantsDataBase.TABLE_COMPANY +
                " SET " + ConstantsDataBase.COMPANY_COLUNA_CNPJ + "=?, " +
                ConstantsDataBase.COLUMN_NAME + "=?, " +
                ConstantsDataBase.COLUMN_EMAIL + "=?, " +
                ConstantsDataBase.COLUMN_PASSWORD + "=?, " +
                ConstantsDataBase.COLUMN_PHONE + "=? WHERE " +
                ConstantsDataBase.COMPANY_COLUNA_ID + "=?;";
        try {
            if (company == null) {
                throw new RuntimeException("Erro ao atualizar empresa: objeto company é null");
            }
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setString(1, company.getCnpj());
            st.setString(2, company.getName());
            st.setString(3, company.getEmail());
            st.setString(4, company.getPassword());
            st.setString(5, company.getPhoneNumber());
            st.setLong(6, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar empresa: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return company;
    }

    @Override
    public Company read(long id) {
        Company company = null;
        connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_COMPANY +
                     " WHERE " + ConstantsDataBase.COMPANY_COLUNA_ID + " = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                company = new Company();
                company.setId(rs.getLong(ConstantsDataBase.COMPANY_COLUNA_ID));
                company.setCnpj(rs.getString(ConstantsDataBase.COMPANY_COLUNA_CNPJ));
                company.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                company.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                company.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                company.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler empresa: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return company;
    }

    @Override
    public List<Company> listAll() {
        List<Company> companies = new ArrayList<>();
        connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_COMPANY;
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Company company = new Company();
                company.setId(rs.getLong(ConstantsDataBase.COMPANY_COLUNA_ID));
                company.setCnpj(rs.getString(ConstantsDataBase.COMPANY_COLUNA_CNPJ));
                company.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                company.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                company.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                company.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
                companies.add(company);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar empresas: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return companies;
    }

    public boolean exists(long companyId) {
        boolean exists = false;
        connection.openConnection();
        String sql = "SELECT 1 FROM " + ConstantsDataBase.TABLE_COMPANY +
                     " WHERE " + ConstantsDataBase.COMPANY_COLUNA_ID + " = ?";
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, companyId);
            try (ResultSet rs = st.executeQuery()) {
                exists = rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência da empresa: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
        return exists;
    }
}
