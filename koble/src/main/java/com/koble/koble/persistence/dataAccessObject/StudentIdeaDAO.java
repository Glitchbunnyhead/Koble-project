package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.StudentIdea;
import com.koble.koble.model.Idea;
import com.koble.koble.model.Student;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentIdeaDAO {

    private final MySqlConnection connection;
    private final IdeaDAO ideaDAO;
    private final StudentDAO studentDAO;

    public StudentIdeaDAO(MySqlConnection connection, IdeaDAO ideaDAO, StudentDAO studentDAO) {
        this.connection = connection;
        this.ideaDAO = ideaDAO;
        this.studentDAO = studentDAO;
    }

    public StudentIdea create(StudentIdea si) {
        if (si == null) throw new IllegalArgumentException("StudentIdea não pode ser null");

        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_STUDENTIDEA + " ("
                + ConstantsDataBase.COLUMN_IDEAID + ", "
                + ConstantsDataBase.COLUMN_STUDENTID + ") VALUES (?, ?)";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, si.getIdeaId());
            st.setLong(2, si.getStudentId());
            st.executeUpdate();
            return si;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar StudentIdea: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    public String delete(long ideaId, long studentId) {
        if (ideaId <= 0 || studentId <= 0) throw new IllegalArgumentException("IDs inválidos");

        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_IDEAID + " = ? AND " +
                ConstantsDataBase.COLUMN_STUDENTID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, ideaId);
            st.setLong(2, studentId);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0 ? "Relacionamento Student-Idea deletado com sucesso" : "Relacionamento não encontrado";
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar StudentIdea: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    public String deleteAllByIdeaId(long ideaId) {
        if (ideaId <= 0) throw new IllegalArgumentException("ID de Idea inválido");

        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_IDEAID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, ideaId);
            st.executeUpdate();
            return "Todos os registros Student-Idea para a ideia deletados com sucesso";
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar StudentIdea por ideia: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    public String deleteAllByStudentId(long studentId) {
        if (studentId <= 0) throw new IllegalArgumentException("ID de Student inválido");

        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_STUDENTID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, studentId);
            st.executeUpdate();
            return "Todos os registros Student-Idea para o estudante deletados com sucesso";
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar StudentIdea por estudante: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    public boolean exists(long ideaId, long studentId) {
        if (ideaId <= 0 || studentId <= 0) return false;

        String sql = "SELECT COUNT(*) FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_IDEAID + " = ? AND " +
                ConstantsDataBase.COLUMN_STUDENTID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, ideaId);
            st.setLong(2, studentId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência StudentIdea: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    public List<StudentIdea> listAll() {
        List<StudentIdea> list = new ArrayList<>();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_STUDENTIDEA;

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                StudentIdea si = new StudentIdea();
                si.setIdeaId(rs.getLong(ConstantsDataBase.COLUMN_IDEAID));
                si.setStudentId(rs.getLong(ConstantsDataBase.COLUMN_STUDENTID));
                list.add(si);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar StudentIdeas: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    public List<Idea> findIdeasByStudentId(long studentId) {
        if (studentId <= 0) return new ArrayList<>();
        List<Idea> ideas = new ArrayList<>();
        String sql = "SELECT " + ConstantsDataBase.COLUMN_IDEAID + " FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_STUDENTID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, studentId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Idea idea = ideaDAO.read(rs.getLong(ConstantsDataBase.COLUMN_IDEAID));
                    if (idea != null) ideas.add(idea);
                }
            }
            return ideas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ideias por estudante: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    public List<Student> findStudentsByIdeaId(long ideaId) {
        if (ideaId <= 0) return new ArrayList<>();
        List<Student> students = new ArrayList<>();
        String sql = "SELECT " + ConstantsDataBase.COLUMN_STUDENTID + " FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                " WHERE " + ConstantsDataBase.COLUMN_IDEAID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, ideaId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Student student = studentDAO.read(rs.getLong(ConstantsDataBase.COLUMN_STUDENTID));
                    if (student != null) students.add(student);
                }
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar estudantes por ideia: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }
}
