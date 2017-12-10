package Tables;

import java.sql.*;
import java.util.ArrayList;

public class Profile {

    private Connection conn;

    private String userID;
    private String name;
    private String email;
    private Date date_of_birth;
    private Timestamp lastlogin;

    public static Profile get(Connection conn, String userID, boolean full)
            throws SQLException {

        Profile temp = new Profile();

        if (full) {
            // Get all fields
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT userID,name,email, date_of_birth, lastlogin " +
                            "FROM PROFILE " +
                            "WHERE userID = ?"
            );
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();

            rs.next();
            temp.userID = rs.getString(1);
            temp.name = rs.getString(2);
            temp.email = rs.getString(3);
            temp.date_of_birth = rs.getDate(4);
            temp.lastlogin = rs.getTimestamp(5);

        } else {
            // just get name, userID
            PreparedStatement ps = conn.prepareStatement("SELECT userID,name " +
                    "FROM PROFILE " +
                    "WHERE userID = ?");
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();

            rs.next();
            temp.userID = rs.getString(1);
            temp.name = rs.getString(2);
        }

        return temp;

    }

    public static Profile create(Connection conn, String email, String name, Date date_of_birth)
            throws SQLException {

        Profile created = new Profile();

        StringBuilder ID = new StringBuilder();
        for (String s : name.split(" "))
            ID.append(Character.toLowerCase(s.charAt(0)));

        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Profile");
        ResultSet rs = ps.executeQuery();

        rs.next();
        ID.append(rs.getInt(1) + 1);

        created.conn = conn;
        created.userID = ID.toString();
        created.name = name;
        created.email = email;
        created.date_of_birth = date_of_birth;

        ps = conn.prepareStatement(
                "INSERT INTO Friends (userID, name, email, date_of_birth) VALUES (?, ?, ?, ?)"
        );
        ps.setString(1, created.userID);
        ps.setString(2, created.userID);
        ps.setString(3, created.email);
        ps.setDate(4, created.date_of_birth);
        ps.execute();

        return created;
    }

    // TODO fix
    public static Profile login(Connection conn, String userID, String password)
            throws SQLException {

        PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM PROFILE WHERE userID = ? AND password = ?"
        );
        ps.setString(1, userID);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        rs.next();
        if (rs.getInt(1) != 1) return null;

        return Profile.get(conn, userID, true);
    }

    public void logout() throws SQLException {

        lastlogin = new Timestamp(System.currentTimeMillis());

        PreparedStatement ps = conn.prepareStatement(
                "UPDATE PROFILE " +
                        "SET lastlogin = ?" +
                        " WHERE userID = ?"
        );
        ps.setTimestamp(1, lastlogin);
        ps.setString(2, userID);

        ps.executeUpdate();
    }

    public void sendMessageToUser(Profile to, String message, Connection conn)
            throws SQLException {

        // TODO Create new Message

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM message");
        rs.next();
        String mID = String.format("%d", rs.getInt(1) + 1);

        if (stmt.execute("INSERT INTO MESSAGE (msgID, fromID, message, toUserID, dateSent) " +
                "VALUES ('" +
                mID + "','" +
                userID + "','" +
                message + "','" +
                userID + "', NULL,'" +
                new Date(System.currentTimeMillis()) +
                "')"


        )) {
            System.out.println("Message sent");
        }


    }

    public void sendMessageToGroup(String groupID, String message, Connection conn)
            throws SQLException {

        // TODO Create new Message

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM message");
        rs.next();
        String mID = String.format("%d", rs.getInt(1) + 1);

        stmt.execute(
                "INSERT INTO MESSAGE (msgID, fromID, message, toUserID, toGroupID, dateSent) " +
                        "VALUES ('" +
                        mID + "','" +
                        userID + "','" +
                        message + "',NULL,'" +
                        groupID + "','" +
                        new Date(System.currentTimeMillis()) +
                        "')"
        );

    }

    public void initiateFriendship(Profile other, String message)
            throws SQLException {

        Friends.addPending(conn, this, other, message);
    }

    // TODO Queries pending friends, mapping by ID? Name?
    public ArrayList<Friends> displayPendingFriends()
            throws SQLException {

        // Get list
        PreparedStatement ps = conn.prepareStatement(
                "SELECT fromID, toID, message " +
                "FROM Pending_Friends " +
                "WHERE toID = ?"
        );
        ps.setString(1, userID);

        ResultSet rs = ps.executeQuery();

        // Populate our list
        ArrayList<Friends> pending = new ArrayList<Friends>();
        while (rs.next()) {

            Friends temp = new Friends(
                    conn,
                    true,
                    Profile.get(conn, rs.getString(1), false),
                    Profile.get(conn, rs.getString(2), false),
                    rs.getString(3)
            );
            pending.add(temp);

            System.out.format(
                    "%s. %s:\n\t%s\n\n",
                    pending.size(),
                    temp.getFrom().getName(),
                    temp.getMessage()
            );
        }

        return pending;
    }

    public ArrayList<GroupMembership> displayPendingGroups(int startFrom)
            throws SQLException {

        // Get list
        PreparedStatement ps = conn.prepareStatement(
                "SELECT gID, message " +
                        "FROM Pending_Groupmembers " +
                        "WHERE toID = ?"
        );
        ps.setString(1, userID);

        ResultSet rs = ps.executeQuery();

        // Populate our list
        ArrayList<GroupMembership> pending = new ArrayList<GroupMembership>();
        while (rs.next()) {

            GroupMembership temp = new GroupMembership(
                    conn,
                    true,
                    Group.get(conn, rs.getString(1)),
                    this,
                    rs.getString(2)
            );

            pending.add(temp);

            System.out.format(
                    "%s. %s:\n\t%s\n\n",
                    pending.size(),
                    temp.getGroup().getName(),
                    temp.getMessage()
            );
        }

        return pending;
    }

    public ArrayList<Profile> displayFriends()
            throws SQLException {

        // Query for friends
        PreparedStatement ps = conn.prepareStatement(
                "SELECT userID1, userID2, JDate, message " +
                        "FROM Friends " +
                        "WHERE userID1 = ? OR userID2 = ?"
        );
        ps.setString(1, userID);
        ps.setString(2, userID);

        ResultSet rs = ps.executeQuery();

        // Populate our list
        ArrayList<Profile> friends = new ArrayList<Profile>();
        while (rs.next()) {

            Profile temp;
            if (rs.getString(1).equals(userID)) {
                temp = get(conn, rs.getString(2), false);
            } else if (rs.getString(2).equals(userID)) {
                temp = get(conn, rs.getString(1), false);
            } else {
                throw new SQLException("Unknown error when retrieving friends list");
            }

            friends.add(temp);

            System.out.format("%s:\t%s\n",
                    temp.userID,
                    temp.name
            );
        }

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
