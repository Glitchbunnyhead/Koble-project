package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.Idea;
import com.koble.koble.model.Student;
import com.koble.koble.model.Teacher;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IdeaDAO implements Crudl<Idea> {

    private final MySqlConnection connection;
    private final StudentDAO studentDAO;
    private final TeacherDAO teacherDAO;

    public IdeaDAO(MySqlConnection connection, StudentDAO studentDAO, TeacherDAO teacherDAO) {
        this.connection = connection;
        this.studentDAO = studentDAO;
        this.teacherDAO = teacherDAO;
    }

    public boolean setTeacherIdFromTeacher(Idea idea, Teacher teacher) {
        if (idea == null || teacher == null) return false;

        String sql = "UPDATE " + ConstantsDataBase.TABLE_IDEA +
                     " SET " + ConstantsDataBase.COLUMN_TEACHERID + " = ? " +
                     " WHERE " + ConstantsDataBase.IDEA_COLUNA_ID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, teacher.getId());
            st.setLong(2, idea.getId());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                idea.setTeacherId(teacher.getId());
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar Teacher ID da Idea: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    @Override
    public Idea create(Idea idea) {
        if (idea == null) throw new IllegalArgumentException("Idea inválida");

        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_IDEA + " (" +
                     ConstantsDataBase.IDEA_COLUNA_PROPOSER + ", " +
                     ConstantsDataBase.COLUMN_TARGETAUDIENCE + ", " +
                     ConstantsDataBase.COLUMN_JUSTIFICATION + ", " +
                     ConstantsDataBase.COLUMN_TITLE + ", " +
                     ConstantsDataBase.COLUMN_AIM + ", " +
                     ConstantsDataBase.COLUMN_SUBTITLE + ", " +
                     ConstantsDataBase.IDEA_COLUNA_AREA + ", " +
                     ConstantsDataBase.IDEA_COLUNA_DESCRIPTION + ", " +
                     ConstantsDataBase.IDEA_COLUNA_TYPE + ", " +
                     ConstantsDataBase.COLUMN_TEACHERID + ", " +
                     ConstantsDataBase.COLUMN_STUDENTID + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setString(1, idea.getProposer());
            st.setString(2, idea.getTargetAudience());
            st.setString(3, idea.getJustification());
            st.setString(4, idea.getTitle());
            st.setString(5, idea.getAim());
            st.setString(6, idea.getSubtitle());
            st.setString(7, idea.getArea());
            st.setString(8, idea.getDescription());
            st.setString(9, idea.getType());
            st.setObject(10, idea.getTeacherId() > 0 ? idea.getTeacherId() : null);
            st.setObject(11, idea.getStudentId() > 0 ? idea.getStudentId() : null);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar Idea: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }

        return idea;
    }

    @Override
    public String delete(long id) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido para deletar Idea");

        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_IDEA +
                     " WHERE " + ConstantsDataBase.IDEA_COLUNA_ID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) return "Nenhuma Idea encontrada com ID: " + id;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar Idea: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }

        return "Idea deletada com sucesso";
    }

    @Override
    public Idea update(long id, Idea idea) {
        if (idea == null) throw new IllegalArgumentException("Idea inválida");

        String sql = "UPDATE " + ConstantsDataBase.TABLE_IDEA +
                     " SET " + ConstantsDataBase.IDEA_COLUNA_PROPOSER + "=?, " +
                     ConstantsDataBase.COLUMN_TARGETAUDIENCE + "=?, " +
                     ConstantsDataBase.COLUMN_JUSTIFICATION + "=?, " +
                     ConstantsDataBase.COLUMN_TITLE + "=?, " +
                     ConstantsDataBase.COLUMN_AIM + "=?, " +
                     ConstantsDataBase.COLUMN_SUBTITLE + "=?, " +
                     ConstantsDataBase.IDEA_COLUNA_AREA + "=?, " +
                     ConstantsDataBase.IDEA_COLUNA_DESCRIPTION + "=?, " +
                     ConstantsDataBase.IDEA_COLUNA_TYPE + "=?, " +
                     ConstantsDataBase.COLUMN_TEACHERID + "=?, " +
                     ConstantsDataBase.COLUMN_STUDENTID + "=? WHERE " +
                     ConstantsDataBase.IDEA_COLUNA_ID + "=?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setString(1, idea.getProposer());
            st.setString(2, idea.getTargetAudience());
            st.setString(3, idea.getJustification());
            st.setString(4, idea.getTitle());
            st.setString(5, idea.getAim());
            st.setString(6, idea.getSubtitle());
            st.setString(7, idea.getArea());
            st.setString(8, idea.getDescription());
            st.setString(9, idea.getType());
            st.setObject(10, idea.getTeacherId() > 0 ? idea.getTeacherId() : null);
            st.setObject(11, idea.getStudentId() > 0 ? idea.getStudentId() : null);
            st.setLong(12, id);

            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar Idea: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }

        return idea;
    }

    @Override
    public Idea read(long id) {
        if (id <= 0) return null;

        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_IDEA +
                     " WHERE " + ConstantsDataBase.IDEA_COLUNA_ID + " = ?";

        Idea idea = null;
        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    idea = mapResultSetToIdea(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler Idea: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }

        return idea;
    }

    @Override
    public List<Idea> listAll() {
        List<Idea> ideas = new ArrayList<>();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_IDEA;

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                ideas.add(mapResultSetToIdea(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Ideas: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }

        return ideas;
    }

    public Student getStudentById(long studentId) {
        if (studentId <= 0) return null;
        return studentDAO.read(studentId);
    }

    public Teacher getTeacherById(long teacherId) {
        if (teacherId <= 0) return null;
        return teacherDAO.read(teacherId);
    }

    public Student getStudentByIdea(Idea idea) {
        if (idea == null) return null;
        return getStudentById(idea.getStudentId());
    }

    public Teacher getTeacherByIdea(Idea idea) {
        if (idea == null) return null;
        return getTeacherById(idea.getTeacherId());
    }

    private Idea mapResultSetToIdea(ResultSet rs) throws SQLException {
        Idea idea = new Idea();
        idea.setId(rs.getLong(ConstantsDataBase.IDEA_COLUNA_ID));
        idea.setProposer(rs.getString(ConstantsDataBase.IDEA_COLUNA_PROPOSER));
        idea.setTargetAudience(rs.getString(ConstantsDataBase.COLUMN_TARGETAUDIENCE));
        idea.setJustification(rs.getString(ConstantsDataBase.COLUMN_JUSTIFICATION));
        idea.setTitle(rs.getString(ConstantsDataBase.COLUMN_TITLE));
        idea.setAim(rs.getString(ConstantsDataBase.COLUMN_AIM));
        idea.setSubtitle(rs.getString(ConstantsDataBase.COLUMN_SUBTITLE));
        idea.setArea(rs.getString(ConstantsDataBase.IDEA_COLUNA_AREA));
        idea.setDescription(rs.getString(ConstantsDataBase.IDEA_COLUNA_DESCRIPTION));
        idea.setType(rs.getString(ConstantsDataBase.IDEA_COLUNA_TYPE));

        long teacherId = rs.getLong(ConstantsDataBase.COLUMN_TEACHERID);
        if (!rs.wasNull()) idea.setTeacherId(teacherId);

        long studentId = rs.getLong(ConstantsDataBase.COLUMN_STUDENTID);
        if (!rs.wasNull()) idea.setStudentId(studentId);

        return idea;
    }
}
