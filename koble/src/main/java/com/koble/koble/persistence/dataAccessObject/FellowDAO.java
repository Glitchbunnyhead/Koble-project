package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.Fellow;
import com.koble.koble.model.Student;
import com.koble.koble.model.Project;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FellowDAO {

    private final MySqlConnection connection;
    private final StudentDAO studentDAO;
    private final ProjectDAO projectDAO;

    public FellowDAO(MySqlConnection connection, StudentDAO studentDAO, ProjectDAO projectDAO) {
        this.connection = connection;
        this.studentDAO = studentDAO;
        this.projectDAO = projectDAO;
    }

    public Fellow create(Fellow fellow) {
        if (fellow == null || fellow.getStudentId() <= 0 || fellow.getProjectId() <= 0) {
            throw new IllegalArgumentException("Fellow inválido: objeto nulo ou IDs ausentes");
        }

        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_FELLOW +
                " (" + ConstantsDataBase.COLUMN_STUDENTID + ", " +
                ConstantsDataBase.COLUMN_PROJECT_ID + ", " +
                ConstantsDataBase.COLUMN_CPF + ", " +
                ConstantsDataBase.COLUMN_LATTESCURRICULUM + ") VALUES (?, ?, ?, ?)";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, fellow.getStudentId());
            st.setLong(2, fellow.getProjectId());
            st.setString(3, fellow.getCpf());
            st.setString(4, fellow.getLattesCurriculum());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar Fellow: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }

        return fellow;
    }

    public String delete(long studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("ID inválido para deletar Fellow");
        }

        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_FELLOW +
                " WHERE " + ConstantsDataBase.STUDENT_COLUNA_ID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, studentId);
            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
                return "Nenhum Fellow encontrado com Student ID: " + studentId;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar Fellow: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }

        return "Fellow deletado com sucesso";
    }

    public Fellow read(long studentId) {
        if (studentId <= 0) return null;

        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_FELLOW +
                " WHERE " + ConstantsDataBase.STUDENT_COLUNA_ID + " = ?";

        Fellow fellow = null;
        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, studentId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    fellow = mapResultSetToFellow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler Fellow: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }

        return fellow;
    }

    public List<Fellow> listAll() {
        List<Fellow> fellows = new ArrayList<>();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_FELLOW;

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                fellows.add(mapResultSetToFellow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Fellows: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }

        return fellows;
    }

    public Student getStudentByFellow(Fellow fellow) {
        if (fellow == null || fellow.getStudentId() <= 0) return null;
        return studentDAO.read(fellow.getStudentId());
    }

    public Project getProjectByFellow(Fellow fellow) {
        if (fellow == null || fellow.getProjectId() <= 0) return null;
        return projectDAO.read(fellow.getProjectId());
    }

    private Fellow mapResultSetToFellow(ResultSet rs) throws SQLException {
        Fellow fellow = new Fellow();
        fellow.setStudentId(rs.getLong(ConstantsDataBase.COLUMN_STUDENTID));
        fellow.setProjectId(rs.getLong(ConstantsDataBase.COLUMN_PROJECT_ID));
        fellow.setCpf(rs.getString(ConstantsDataBase.COLUMN_CPF));
        fellow.setLattesCurriculum(rs.getString(ConstantsDataBase.COLUMN_LATTESCURRICULUM));
        return fellow;
    }
}
