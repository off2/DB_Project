package Tables;

import java.sql.*;
import java.util.ArrayList;

public class Group {

    private Connection conn;

    private String gID;
    private String name;
    private String description;

    private Profile admin;

    private ArrayList<Profile> members;
    private Integer memberLimit;

    public static Group get(Connection conn, String gID)
            throws SQLException {

        // Get data
        PreparedStatement ps = conn.prepareStatement(
                "SELECT gID, name, description " +
                        "FROM Groups " +
                        "WHERE gID = ?"
        );
        ps.setString(1, gID);
        ResultSet rs = ps.executeQuery();

        // Create object
        Group temp = new Group();
        temp.gID = rs.getString(1);
        temp.name = rs.getString(2);
        temp.description = rs.getString(3);

        return temp;
    }

    public static Group create(Connection conn, Profile admin, String name, String description, int memberLimit)
            throws SQLException {

        // For ID purposes
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Groups");
        ResultSet rs = ps.executeQuery();
        rs.next();

        // Create new group
        Group created = new Group();

        created.gID = String.format("%d", rs.getInt(1) + 1);
        created.conn = conn;
        created.name = name;
        created.description = description;

        created.admin = admin;
        created.memberLimit = memberLimit;

        // Persist to database
        ps = conn.prepareStatement(
                "INSERT INTO Groups (GID, NAME, DESCRIPTION) " +
                        "VALUES (?, ?, ?)"
        );
        ps.setString(1, created.gID);
        ps.setString(2, created.name);
        ps.setString(3, created.description);
        ps.execute();

        return created;
    }

    public void initiateAddUser(Profile to, String message)
            throws SQLException {

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Pending_Groupmembers (gID, userID, message) " +
                        "VALUES (?, ?, ?)"
        );
        ps.setString(1, gID);
        ps.setString(2, to.getUserID());
        ps.setString(3, message);

        ps.execute();
    }

    public void getMembers() {
        // TODO implement
    }

    public void getAdmin() {
        // TODO implement
    }

    public String getgID() {
        return gID;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {

        if (admin == null) {
            return "GroupID: " + gID +
                    "\nName: " + name +
                    "\nDescription: " + description +
                    "\nMax members: " + memberLimit + "\n";
        }

        return "GroupID: " + gID +
                "\nName: " + name +
                "\nDescription: " + description +
                "\nAdmin" + admin +
                "\nMax members: " + memberLimit + "\n";
    }
}
