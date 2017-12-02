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
        } else {
            // just get name, userID
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

        // save

        return created;
    }

    // TODO fix
    public static Profile login(Connection conn, String userID, String password)
            throws SQLException {

        String function = "login";

    }

    // TODO fix
    public void logout() {

    }

    public void sendMessage(Profile to, String message) {

        // TODO Create new Message

    }

    public void initiateFriendship(Profile other, String message) {

        Friends created = new Friends(conn, true, this, other);

    }

    // TODO Queries pending friends, mapping by ID? Name?
    public ArrayList<Friends> displayPendingFriends()
            throws SQLException {

        // Get list
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT fromID, toID, message" +
                "FROM Pending_Friends " +
                "WHERE toID = " + userID);

        // Populate our list
        ArrayList<Friends> pending = new ArrayList<Friends>();
        StringBuilder output = new StringBuilder();

        while (rs.next()) {

            Friends temp = new Friends(
                    conn,
                    true,
                    Profile.get(conn, rs.getString(1), false),
                    Profile.get(conn, rs.getString(2), false)
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
        ResultSet rs = stmt.executeQuery("SELECT userID1, userID2, JDate, message" +
                "FROM Friends" +
                "WHERE userID1 = " + userID + " OR userID2 = " + userID);


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

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public Timestamp getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(Timestamp lastlogin) {
        this.lastlogin = lastlogin;
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
