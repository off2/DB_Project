package Tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

public class GroupMembership {

    private Connection conn;

    private boolean pending;

    private Group group;
    private Profile user;

    private String message;

    public GroupMembership(Connection conn, boolean pending, Group group, Profile user, String message) {
        this.conn = conn;
        this.pending = pending;
        this.group = group;
        this.user = user;
        this.message = message;
    }

    public boolean confirm() throws SQLException {

        if (!pending) return false;

        delete();
		
       
		PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Group_Membership (gID, userID, message) " +
                        "VALUES (?, ?, ?)"
        );
        stmt.setString(1, group.getgID());
        stmt.setString(2, user.getUserID());
        stmt.setString(3, message);

        stmt.executeUpdate();

		pending = false;
        return true;
    }

    public void delete() throws SQLException {

        PreparedStatement stmt = conn.createStatement();
        if (pending) {
            stmt = conn.prepareStatement(
                    "DELETE FROM Pending_Groupmembers " +
                            "WHERE gID = ? AND userID = ?"
            );
			
			stmt.setString(1, group.getgID());
			stmt.setString(2, user.getUserID());
			
        } else {
            stmt. conn.prepareStatement(
                    "DELETE FROM GROUP_MEMBERSHIP " +
                            " WHERE gID = ? AND userID = ?"
            );
			
			stmt.setString(1, group.getgID());
			stmt.setString(2, user.getUserID());
        }
		stmt.executeUpdate();
		
		

    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Profile getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
