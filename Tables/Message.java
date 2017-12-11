package Tables;

import java.sql.*;

public class Message {

    private Connection conn;

    private String msgID;
    private Profile from;
    private String message;

    private Profile toProfile;
    private Group toGroup;

    private Date datesent;

    public static Message get(Connection conn, String msgID)
            throws SQLException {

        // Get data
        PreparedStatement ps = conn.prepareStatement(
                "SELECT fromID, message, toUserID, toGroupID, datesent " +
                        "FROM Message WHERE msgID = ?"
        );
        ps.setString(1, msgID);
        ResultSet rs = ps.executeQuery();

        // Create object
        Message msg = new Message();
        msg.conn = conn;
        msg.msgID = msgID;
        msg.from = Profile.get(conn, rs.getString(1), true);
        msg.message = rs.getString(2);
        msg.datesent = rs.getDate(5);

        // Profile or group
        if (rs.getString(4).equalsIgnoreCase("null")) {
            msg.toProfile = Profile.get(conn, rs.getString(3), true);
        } else if (rs.getString(3).equalsIgnoreCase("null")) {
            msg.toGroup = Group.get(conn, rs.getString(4));
        } else {
            return null;
        }

        return msg;
    }

    public static Message send(Connection conn, Profile from, String message, Profile to)
            throws SQLException {

        Message created = new Message();

        created.conn = conn;
        created.send(from, message, to, null);

        return created;
    }

    public static Message send(Connection conn, Profile from, String message, Group to)
            throws SQLException {

        Message created = new Message();

        created.conn = conn;
        created.send(from, message, null, to);

        return created;
    }

    private void send(Profile from, String message, Profile toProfile, Group toGroup)
            throws SQLException {

        // For ID purposes
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Messages");
        ResultSet rs = ps.executeQuery();
        rs.next();

        // Fill out fields
        this.msgID = Integer.toString(rs.getInt(1));
        this.from = from;
        this.message = message;
        this.datesent = new Date(System.currentTimeMillis());
        this.toProfile = toProfile;
        this.toGroup = toGroup;

        // Insert
        ps = conn.prepareStatement(
                "INSERT INTO Message (msgID, fromID, message, toUserID, toGroupID, datesent) " +
                        "VALUES (?, ?, ?, ?, ?, ?)"
        );

        if (toGroup == null) {
            ps.setString(4, toProfile.getUserID());
            ps.setString(5, "NULL");
        } else if (toProfile == null) {
            ps.setString(4, "NULL");
            ps.setString(5, toGroup.getgID());
        }

        ps.execute();
    }

    @Override
    public String toString() {
        return String.format(
                "From: %s (%s)\nDate: %s\nMessage: %s\n\n",
                from.getName(), from.getUserID(),
                datesent, message
        );
    }
}
