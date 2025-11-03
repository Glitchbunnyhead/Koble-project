package com.koble.koble.persistence.dataAccessObject;

import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.koble.koble.model.StudentIdea;
import com.koble.koble.model.Idea;
import com.koble.koble.model.Student;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.MySqlConnection;

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

    // Create relationship (junction table)
    public StudentIdea create(StudentIdea si) {
        this.connection.openConnection();
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_STUDENTIDEA + " (" +
                     ConstantsDataBase.COLUMN_IDEAID + ", " +
                     ConstantsDataBase.COLUMN_STUDENTID + ") VALUES (?, ?)";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, si.getIdeaId());
            st.setLong(2, si.getStudentId());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            connection.closeConnection();
        }
        return si;
    }

    // Delete by composite key
    public String delete(long ideaId, long studentId) {
        this.connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                     " WHERE " + ConstantsDataBase.COLUMN_IDEAID + " = ? AND " +
                     ConstantsDataBase.COLUMN_STUDENTID + " = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, ideaId);
            st.setLong(2, studentId);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting student-idea relationship: " + e.getMessage();
        } finally {
            connection.closeConnection();
        }
        return "Student-idea relationship deleted successfully";
    }

    // Delete all by idea id
    public String deleteAllByIdeaId(long ideaId) {
        this.connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                     " WHERE " + ConstantsDataBase.COLUMN_IDEAID + " = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, ideaId);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting student-idea by idea id: " + e.getMessage();
        } finally {
            connection.closeConnection();
        }
        return "All student-idea records for idea deleted successfully";
    }

    // Delete all by student id
    public String deleteAllByStudentId(long studentId) {
        this.connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                     " WHERE " + ConstantsDataBase.COLUMN_STUDENTID + " = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, studentId);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting student-idea by student id: " + e.getMessage();
        } finally {
            connection.closeConnection();
        }
        return "All student-idea records for student deleted successfully";
    }

    // Check existence
    public boolean exists(long ideaId, long studentId) {
        boolean exists = false;
        this.connection.openConnection();
        String sql = "SELECT COUNT(*) FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                     " WHERE " + ConstantsDataBase.COLUMN_IDEAID + " = ? AND " +
                     ConstantsDataBase.COLUMN_STUDENTID + " = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, ideaId);
            st.setLong(2, studentId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.closeConnection();
        }
        return exists;
    }

    // List all relationships
    public List<StudentIdea> listAll() {
        List<StudentIdea> list = new ArrayList<>();
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_STUDENTIDEA;
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                StudentIdea si = new StudentIdea();
                si.setIdeaId(rs.getLong(ConstantsDataBase.COLUMN_IDEAID));
                si.setStudentId(rs.getLong(ConstantsDataBase.COLUMN_STUDENTID));
                list.add(si);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.closeConnection();
        }
        return list;
    }

    // Find all ideas for a student
    public List<Idea> findIdeasByStudentId(long studentId) {
        List<Idea> ideas = new ArrayList<>();
        this.connection.openConnection();
        String sql = "SELECT " + ConstantsDataBase.COLUMN_IDEAID + " FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                     " WHERE " + ConstantsDataBase.COLUMN_STUDENTID + " = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, studentId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    long ideaId = rs.getLong(ConstantsDataBase.COLUMN_IDEAID);
                    Idea idea = ideaDAO.read(ideaId);
                    if (idea != null) ideas.add(idea);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.closeConnection();
        }
        return ideas;
    }

    // Find all students for an idea
    public List<Student> findStudentsByIdeaId(long ideaId) {
        List<Student> students = new ArrayList<>();
        this.connection.openConnection();
        String sql = "SELECT " + ConstantsDataBase.COLUMN_STUDENTID + " FROM " + ConstantsDataBase.TABLE_STUDENTIDEA +
                     " WHERE " + ConstantsDataBase.COLUMN_IDEAID + " = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, ideaId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    long studentId = rs.getLong(ConstantsDataBase.COLUMN_STUDENTID);
                    Student student = studentDAO.read(studentId);
                    if (student != null) students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.closeConnection();
        }
        return students;
    }
}
    
