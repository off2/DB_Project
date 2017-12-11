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

            if (rs == null)
                return null;

            rs.next();

            temp.conn = conn;
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

            if (rs == null)
                return null;

            rs.next();

            temp.userID = rs.getString(1);
            temp.name = rs.getString(2);
        }

        return temp;

    }

    public static Profile create(Connection conn, String email, String name, String pw, Date date_of_birth)
            throws SQLException {

        Profile created = new Profile();

        StringBuilder ID = new StringBuilder();
        for (String s : name.split(" "))
            ID.append(Character.toLowerCase(s.charAt(0)));

        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Profile");
        ResultSet rs = ps.executeQuery();

        if (rs == null)
            return null;

        rs.next();
        ID.append(rs.getInt(1) + 1);

        created.conn = conn;
        created.userID = ID.toString();
        created.name = name;
        created.email = email;
        created.date_of_birth = date_of_birth;

        ps = conn.prepareStatement(
                "INSERT INTO Profile (userID, name, email, password, date_of_birth) VALUES (?, ?, ?, ?, ?)"
        );
        ps.setString(1, created.userID);
        ps.setString(2, created.userID);
        ps.setString(3, created.email);
        ps.setString(4, pw);
        ps.setDate(5, created.date_of_birth);
        ps.execute();

        return created;
    }

    public static Profile login(Connection conn, String userID, String password)
            throws SQLException {

        PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM PROFILE WHERE userID = ? AND password = ?"
        );
        ps.setString(1, userID);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        if (rs == null)
            return null;

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

    public Message sendMessageToUser(Profile to, String message)
            throws SQLException {

        return Message.send(conn, this, message, to);
    }

    public Message sendMessageToGroup(Group group, String message)
            throws SQLException {

        return Message.send(conn, this, message, group);
    }

    public void initiateFriendship(Profile other, String message)
            throws SQLException {

        Friends.addPending(conn, this, other, message);
    }

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

        if (rs == null)
            return null;

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

    public ArrayList<GroupMembership> displayPendingGroups()
            throws SQLException {

        // Get list
        PreparedStatement ps = conn.prepareStatement(
                "SELECT gID, message " +
                        "FROM Pending_Groupmembers " +
                        "WHERE userID = ?"
        );
        ps.setString(1, userID);

        ResultSet rs = ps.executeQuery();

        if (rs == null)
            return null;

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

        if (rs == null)
            return null;

        // Populate our list
        ArrayList<Profile> friends = new ArrayList<Profile>();
        if (rs == null) return null;

        while (rs.next()) {

            String one = rs.getString(1), two = rs.getString(2);

            Profile temp;
            if (one.equals(userID)) {
                temp = get(conn, two, false);
            } else if (two.equals(userID)) {
                temp = get(conn, one, false);
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

    public void displayMessages() throws SQLException {

        PreparedStatement ps = conn.prepareStatement(
                "SELECT m.msgID " +
                        "FROM Message m " +
                        "INNER JOIN Message_Recipient r " +
                        "ON m.msgID = r.msgID " +
                        "WHERE m.msgID = ? " +
                        "ORDER BY m.datesent"
        );
        ps.setString(1, userID);

        ResultSet rs = ps.executeQuery();

        if (rs == null)
            return;

        // Print
        while (rs.next())
            System.out.println(Message.get(conn, rs.getString(1)));
    }

    public void displayNewMessages() throws SQLException {

        // Data
        PreparedStatement ps = conn.prepareStatement(
                "SELECT m.msgID " +
                        "FROM Message m " +
                        "INNER JOIN Message_Recipient r " +
                        "ON m.msgID = r.msgID " +
                        "WHERE m.msgID = ? AND m.datesent < ? " +
                        "ORDER BY m.datesent"
        );
        ps.setString(1, userID);
        ps.setDate(2, new Date(lastlogin.getTime()));

        ResultSet rs = ps.executeQuery();

        if (rs == null)
            return;

        // Print
        while (rs.next())
            System.out.println(Message.get(conn, rs.getString(1)));
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
