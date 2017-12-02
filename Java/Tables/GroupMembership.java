package Tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
        conn.createStatement().executeUpdate(
                "INSERT INTO Group_Membership (gID, userID, message) " +
                        "VALUES ('" +
                        group.getgID() + "', '" +
                        user.getUserID() + "', '" +
                        message +
                        "')"
        );

        return pending = true;
    }

    public void delete() throws SQLException {

        Statement stmt = conn.createStatement();
        if (pending) {
            stmt.executeUpdate(
                    "DELETE FROM Pending_Groupmembers " +
                            "WHERE gID = '" + group.getgID() +
                            "' AND userID = '" + user.getUserID() + "'"
            );
        } else {
            stmt.executeUpdate(
                    "DELETE FROM GROUP_MEMBERSHIP " +
                            " WHERE gID = '" + group.getgID() +
                            "' AND userID = '" + user.getUserID() + "'"
            );
        }

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
