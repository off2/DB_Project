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

    public Group() {

    }

    public Group(Connection conn, String gID, String name, String description) {
        this.conn = conn;
        this.gID = gID;
        this.name = name;
        this.description = description;

        // TODO get members?
        // TODO get admin
        // TODO get memberlimit
    }

    public static Group get(Connection conn, String gID)
            throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT gID, name, description" +
                        "FROM Groups" +
                        "WHERE gID = '" + gID + "'"
        );

        Group temp = new Group();
        temp.gID = rs.getString(1);
        temp.name = rs.getString(2);
        temp.description = rs.getString(3);

        // TODO get admin;

        return temp;
    }

    public static Group create(Connection conn, Profile admin, String name, String description, int memberLimit)
            throws SQLException {

        Group created = new Group();

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Groups");
        rs.next();
        created.gID = String.format("%d", rs.getInt(1) + 1);

        created.conn = conn;
        created.name = name;
        created.description = description;

        created.admin = admin;

        created.memberLimit = memberLimit;

        stmt.execute("INSERT INTO Groups (GID, NAME, DESCRIPTION) " +
                "VALUES ('" +
                created.gID + "'', '" +
                created.name + "', '" +
                created.description +
                "'')"
        );

        return created;
    }

    public void initiateAddUser(Profile to, String message)
            throws SQLException {

        Statement stmt = conn.createStatement();
        stmt.execute("INSERT INTO Pending_Groupmembers (gID, userID, message)" +
                "VALUES ('" +
                gID + "', '" +
                to.getUserID() + "', '" +
                message +
                "')"
        );
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
