package com.koble.koble.persistence.dataAccessObject;

import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.koble.koble.model.Teacher;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class TeacherDAO implements Crudl<Teacher> {

    private final MySqlConnection connection;

    public TeacherDAO(MySqlConnection connection) {
        this.connection = connection;
    }

    @Override
    public Teacher create(Teacher teacher) {
        this.connection.openConnection();
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_TEACHER + " (" +
                     ConstantsDataBase.TEACHER_COLUNA_SIAPE + ", " +
                     ConstantsDataBase.COLUMN_EMAIL + ", " +
                     ConstantsDataBase.COLUMN_NAME + ", " +
                     ConstantsDataBase.COLUMN_PASSWORD + ", " +
                     ConstantsDataBase.COLUMN_PHONE + ") VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setString(1, teacher.getSiape());
            st.setString(2, teacher.getEmail());
            st.setString(3, teacher.getName());
            st.setString(4, teacher.getPassword());
            st.setString(5, teacher.getPhoneNumber());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            connection.closeConnection();
        }
        return teacher;
    }

    public Teacher findByName(String name) {
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_TEACHER +
                     " WHERE " + ConstantsDataBase.COLUMN_NAME + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setString(1, name);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Teacher teacher = new Teacher();
                    teacher.setId(rs.getLong(ConstantsDataBase.TEACHER_COLUNA_ID));
                    teacher.setSiape(rs.getString(ConstantsDataBase.TEACHER_COLUNA_SIAPE));
                    teacher.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                    teacher.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                    teacher.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                    teacher.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
                    return teacher;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            connection.closeConnection();
        }
        return null;
    }

    @Override
    public String delete(long id) {
        this.connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_TEACHER +
                     " WHERE " + ConstantsDataBase.TEACHER_COLUNA_ID + " = ?";
        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting teacher: " + e.getMessage();
        } finally {
            connection.closeConnection();
        }
        return "Teacher deleted successfully";
    }

    @Override
    public Teacher update(long id, Teacher teacher) {
        this.connection.openConnection();
        String sql = "UPDATE " + ConstantsDataBase.TABLE_TEACHER + " SET " +
                     ConstantsDataBase.TEACHER_COLUNA_SIAPE + "=?, " +
                     ConstantsDataBase.COLUMN_EMAIL + "=?, " +
                     ConstantsDataBase.COLUMN_NAME + "=?, " +
                     ConstantsDataBase.COLUMN_PASSWORD + "=?, " +
                     ConstantsDataBase.COLUMN_PHONE + "=? WHERE " +
                     ConstantsDataBase.TEACHER_COLUNA_ID + "=?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setString(1, teacher.getSiape());
            st.setString(2, teacher.getEmail());
            st.setString(3, teacher.getName());
            st.setString(4, teacher.getPassword());
            st.setString(5, teacher.getPhoneNumber());
            st.setLong(6, id);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            connection.closeConnection();
        }
        return teacher;
    }

    @Override
    public Teacher read(long id) {
        Teacher teacher = null;
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_TEACHER +
                     " WHERE " + ConstantsDataBase.TEACHER_COLUNA_ID + " = ?";

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    teacher = new Teacher();
                    teacher.setId(rs.getLong(ConstantsDataBase.TEACHER_COLUNA_ID));
                    teacher.setSiape(rs.getString(ConstantsDataBase.TEACHER_COLUNA_SIAPE));
                    teacher.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                    teacher.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                    teacher.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                    teacher.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.closeConnection();
        }
        return teacher;
    }

    @Override
    public List<Teacher> listAll() {
        List<Teacher> teachers = new ArrayList<>();
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_TEACHER;

        try (PreparedStatement st = connection.getConnection().prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Teacher teacher = new Teacher();
                teacher.setId(rs.getLong(ConstantsDataBase.TEACHER_COLUNA_ID));
                teacher.setSiape(rs.getString(ConstantsDataBase.TEACHER_COLUNA_SIAPE));
                teacher.setEmail(rs.getString(ConstantsDataBase.COLUMN_EMAIL));
                teacher.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
                teacher.setPassword(rs.getString(ConstantsDataBase.COLUMN_PASSWORD));
                teacher.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
                teachers.add(teacher);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.closeConnection();
        }
        return teachers;
    }
}
