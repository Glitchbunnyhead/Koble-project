package com.koble.koble.persistence.dataAccessObject;

import com.koble.koble.model.Student;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentDAO implements Crudl<Student> {

    private final MySqlConnection connection;

    public StudentDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    @Override
    public Student create(Student student) {
        if (student == null) throw new IllegalArgumentException("Student não pode ser null");

        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_STUDENT + " ("
                + ConstantsDataBase.STUDENT_COLUNA_ENROLLMENT + ", "
                + ConstantsDataBase.COLUMN_NAME + ", "
                + ConstantsDataBase.COLUMN_EMAIL + ", "
                + ConstantsDataBase.COLUMN_PASSWORD + ", "
                + ConstantsDataBase.COLUMN_PHONE + ", "
                + ConstantsDataBase.COLUMN_BIRTHDATE + ") VALUES (?, ?, ?, ?, ?, ?)";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setString(1, student.getRegistration());
            st.setString(2, student.getName());
            st.setString(3, student.getEmail());
            st.setString(4, student.getPassword());
            st.setString(5, student.getPhoneNumber());
            st.setDate(6, new Date(student.getBirthDate().getTime()));

            st.executeUpdate();
            return student;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar Student: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    @Override
    public String delete(long id) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido");

        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_STUDENT + " WHERE " + ConstantsDataBase.STUDENT_COLUNA_ID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0 ? "Student deletado com sucesso" : "Student não encontrado";

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar Student: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    @Override
    public Student update(long id, Student student) {
        if (id <= 0 || student == null) throw new IllegalArgumentException("Student inválido ou ID inválido");

        String sql = "UPDATE " + ConstantsDataBase.TABLE_STUDENT + " SET "
                + ConstantsDataBase.STUDENT_COLUNA_ENROLLMENT + "=?, "
                + ConstantsDataBase.COLUMN_NAME + "=?, "
                + ConstantsDataBase.COLUMN_EMAIL + "=?, "
                + ConstantsDataBase.COLUMN_PASSWORD + "=?, "
                + ConstantsDataBase.COLUMN_PHONE + "=?, "
                + ConstantsDataBase.COLUMN_BIRTHDATE + "=? WHERE "
                + ConstantsDataBase.STUDENT_COLUNA_ID + "=?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setString(1, student.getRegistration());
            st.setString(2, student.getName());
            st.setString(3, student.getEmail());
            st.setString(4, student.getPassword());
            st.setString(5, student.getPhoneNumber());
            st.setDate(6, new Date(student.getBirthDate().getTime()));
            st.setLong(7, id);

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0 ? student : null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar Student: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    @Override
    public Student read(long id) {
        if (id <= 0) return null;

        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_STUDENT + " WHERE " + ConstantsDataBase.STUDENT_COLUNA_ID + " = ?";

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? mapResultSetToStudent(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler Student: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    @Override
    public List<Student> listAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_STUDENT;

        connection.openConnection();
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            return students;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Students: " + e.getMessage(), e);
        } finally {
            connection.closeConnection();
        }
    }

    // --- HELPER ---
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getLong(ConstantsDataBase.STUDENT_COLUNA_ID));
        student.setRegistration(rs.getString(ConstantsDataBase.STUDENT_COLUNA_ENROLLMENT));
        student.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
        student.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
        student.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
        student.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
        student.setBirthDate(rs.getDate(ConstantsDataBase.COLUMN_BIRTHDATE));
        return student;
    }
}
