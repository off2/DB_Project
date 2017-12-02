package Tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Group {

    private Connection conn;

    private String gID;
    private String name;
    private String description;

    private Profile admin;

    private ArrayList<Profile> members;
    private Integer memberLimit;

    public static Group get(Connection conn, String gID) {

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

        // save

        return created;
    }

    public static Group initiateAddUser() {

    }

    @Override
    public String toString() {
        return "GroupID: " + gID +
                "\nName: " + name +
                "\nDescription: " + description +
                "\nAdmin" + admin +
                "\nMax members: " + memberLimit + "\n";
    }
}
