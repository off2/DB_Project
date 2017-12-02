package Tables;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Profile {

    private Connection conn;

    private String userID;
    private String name;
    private String email;
    private Date date_of_birth;
    private Timestamp lastlogin;

    public static Profile get(Connection conn, String userID, boolean full)
            throws SQLException {

        if (full) {
            // get all fields

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT userID,name,email, date_of_birth, lastlogin" +
                            " FROM PROFILE" +
                            " WHERE userID = " + userID
            );

            Profile temp = new Profile();
            temp.userID = rs.getString(1);
            temp.name = rs.getString(2);
            temp.email = rs.getString(3);
            temp.date_of_birth = rs.getDate(4);
            temp.lastlogin = rs.getTimestamp(5);

            return temp;

        } else {
            // just get name, userID

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT userID,name" +
                            " FROM PROFILE" +
                            " WHERE userID = " + userID
            );

            rs.next();
            Profile temp = new Profile();
            temp.userID = rs.getString(1);
            temp.name = rs.getString(2);

            return temp;

        }
    }

    public static Profile create(Connection conn, String email, String name, Date date_of_birth)
            throws SQLException {

        Profile created = new Profile();

        StringBuilder ID = new StringBuilder();
        for (String s : name.split(" "))
            ID.append(Character.toLowerCase(s.charAt(0)));

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Profile");
        rs.next();
        ID.append(rs.getInt(1) + 1);

        created.conn = conn;
        created.userID = ID.toString();
        created.name = name;
        created.email = email;
        created.date_of_birth = date_of_birth;

        stmt.execute("INSERT INTO Friends (userID, name, email, date_of_birth) " +
                "VALUES (" +
                created.userID + ", " +
                created.name + ", " +
                created.email + ", " +
                created.date_of_birth +
                ")"
        );

        return created;
    }

    // TODO fix
    public static Profile login(Connection conn, String userID, String password)
            throws SQLException {

        String function = "login";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT userID,name,email, date_of_birth, lastlogin " +
                "FROM PROFILE " +
                "WHERE userID = " + userID + " AND password = " + password);

        //if no matches

        Profile temp = null;

        if (!rs.next()) {
            //no profile matches login
            return null;

            //no second profile that also matches loggin
        } else if (!rs.next()) {
            temp = get(conn, userID, true);
        } else {
            //a second matches login
            return null;
        }

        return temp;


    }

    // TODO fix
    public void logout() {


    }

    public void sendMessage(Profile to, String message) {

        // TODO Create new Message

    }

    public void initiateFriendship(Profile other, String message)
            throws SQLException {

        Friends.addPending(conn, this, other, message);
    }

    // TODO Queries pending friends, mapping by ID? Name?
    public ArrayList<Friends> displayPendingFriends()
            throws SQLException {

        // Get list
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT fromID, toID, message" +
                        "FROM Pending_Friends " +
                        "WHERE toID = " + userID
        );

        // Populate our list
        ArrayList<Friends> pending = new ArrayList<Friends>();
        StringBuilder output = new StringBuilder();

        while (rs.next()) {

            Friends temp = new Friends(
                    conn,
                    true,
                    Profile.get(conn, rs.getString(1), false),
                    Profile.get(conn, rs.getString(2), false),
                    rs.getString(3)
            );

            pending.add(temp);

            // TODO possible off by one
            output
                    .append(pending.size())
                    .append(". ")
                    .append(temp.getFrom().getName())
                    .append("\t")
                    .append(temp.getMessage())
                    .append("\n");
        }

        System.out.println(output);

        return pending;
    }

    public HashMap<String, Profile> displayFriends()
            throws SQLException {

        // Query for friends
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT userID1, userID2, JDate, message" +
                        "FROM Friends" +
                        "WHERE userID1 = " + userID + " OR userID2 = " + userID
        );


        // Populate our list
        HashMap<String, Profile> friends = new HashMap<String, Profile>();
        StringBuilder output = new StringBuilder();

        while (rs.next()) {

            Profile temp;
            if (rs.getString(1) == userID) {
                temp = get(conn, rs.getString(2), false);
            } else if (rs.getString(2) == userID) {
                temp = get(conn, rs.getString(1), false);
            } else {
                throw new SQLException("Unknown error when retrieving friends list");
            }

            friends.put(temp.userID, temp);

            output
                    .append(temp.userID)
                    .append(": \t")
                    .append(temp.name);
        }

        System.out.println(output);

        return friends;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {

        if (email == null && date_of_birth == null && lastlogin == null)
            return "Name: " + name + "\nUserID: " + userID;

        return "UserID: " + userID +
                "\nName: " + name +
                "\nEmail: " + email +
                "\nDOB: " + date_of_birth +
                "\nLast Login:" + lastlogin + "\n";
    }

}
