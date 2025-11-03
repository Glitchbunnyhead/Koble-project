package com.koble.koble.persistence.dataAccessObject;

import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.koble.koble.model.Participant;
import com.koble.koble.model.Project;
import com.koble.koble.persistence.ConstantsDataBase;
import com.koble.koble.persistence.Crudl;
import com.koble.koble.persistence.MySqlConnection;

@Repository
public class ParticipantDAO implements Crudl<Participant> {
    private final MySqlConnection connection;
    private final ProjectDAO projectDAO;

    public ParticipantDAO(MySqlConnection connection, ProjectDAO projectDAO) {
        this.connection = connection;
        this.projectDAO = projectDAO;
    }

    @Override
    public Participant create(Participant participant) {
        this.connection.openConnection();
        String sql = "INSERT INTO " + ConstantsDataBase.TABLE_PARTICIPANT + " (" +
                ConstantsDataBase.COLUMN_NAME + ", " +
                ConstantsDataBase.COLUMN_CPF + ", " +
                ConstantsDataBase.COLUMN_PHONE + ", " +
                "role, " +
                ConstantsDataBase.COLUMN_PROJECT_ID +
                ") VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setString(1, participant.getName());
            st.setString(2, participant.getCpf());
            st.setString(3, participant.getPhoneNumber());
            st.setString(4, participant.getRole());
            st.setLong(5, participant.getProjectId());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.connection.closeConnection();
        }
        return participant;
    }

    @Override
    public Participant read(long idEntity) {
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PARTICIPANT + " WHERE " + ConstantsDataBase.PARTICIPANT_COLUNA_ID+ " = ?";
        Participant participant = null;
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, idEntity);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                participant = mapResultSetToParticipant(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.connection.closeConnection();
        }
        return participant;
    }

    @Override
    public List<Participant> listAll() {
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PARTICIPANT;
        List<Participant> participants = new ArrayList<>();
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                participants.add(mapResultSetToParticipant(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.connection.closeConnection();
        }
        return participants;
    }

    @Override
    public Participant update(long idEntity, Participant participant) {
        this.connection.openConnection();
        String sql = "UPDATE " + ConstantsDataBase.TABLE_PARTICIPANT + " SET " +
                ConstantsDataBase.COLUMN_NAME + " = ?, " +
                ConstantsDataBase.COLUMN_CPF + " = ?, " +
                ConstantsDataBase.COLUMN_PHONE + " = ?, " +
                "role = ?, " +
                ConstantsDataBase.COLUMN_PROJECT_ID + " = ? WHERE " +
                ConstantsDataBase.PARTICIPANT_COLUNA_ID + " = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setString(1, participant.getName());
            st.setString(2, participant.getCpf());
            st.setString(3, participant.getPhoneNumber());
            st.setString(4, participant.getRole());
            st.setLong(5, participant.getProjectId());
            st.setLong(6, idEntity);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.connection.closeConnection();
        }
        return participant;
    }

    @Override
    public String delete(long idEntity) {
        this.connection.openConnection();
        String sql = "DELETE FROM " + ConstantsDataBase.TABLE_PARTICIPANT + " WHERE " + ConstantsDataBase.PARTICIPANT_COLUNA_ID + " = ?";
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, idEntity);
            int affectedRows = st.executeUpdate();
            if (affectedRows > 0) {
                return "Deleted successfully";
            } else {
                return "No record found to delete";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error deleting record: " + e.getMessage();
        } finally {
            this.connection.closeConnection();
        }
    }

    // Helper method to map ResultSet to Participant object
    private Participant mapResultSetToParticipant(ResultSet rs) throws SQLException {
        Participant participant = new Participant();
        participant.setId(rs.getLong(ConstantsDataBase.PARTICIPANT_COLUNA_ID));
        participant.setName(rs.getString(ConstantsDataBase.COLUMN_NAME));
        participant.setCpf(rs.getString(ConstantsDataBase.COLUMN_CPF));
        participant.setPhoneNumber(rs.getString(ConstantsDataBase.COLUMN_PHONE));
        participant.setRole(rs.getString("role"));
        participant.setProjectId(rs.getLong(ConstantsDataBase.COLUMN_PROJECT_ID));
        return participant;
    }

    // Convenience method: get Project object by participant id
    public Project getProjectByParticipant(long participantId) {
        Participant participant = read(participantId);
        if (participant != null && participant.getProjectId() > 0) {
            return projectDAO.read(participant.getProjectId());
        }
        return null;
    }

    // Get all participants for a specific project
    public List<Participant> listParticipantsByProject(long projectId) {
        this.connection.openConnection();
        String sql = "SELECT * FROM " + ConstantsDataBase.TABLE_PARTICIPANT + 
                    " WHERE " + ConstantsDataBase.COLUMN_PROJECT_ID + " = ?";
        List<Participant> participants = new ArrayList<>();
        try {
            PreparedStatement st = connection.getConnection().prepareStatement(sql);
            st.setLong(1, projectId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                participants.add(mapResultSetToParticipant(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.connection.closeConnection();
        }
        return participants;
    }
}
