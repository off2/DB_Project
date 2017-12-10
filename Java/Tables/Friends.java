package Tables;

import java.sql.*;

public class Friends {

    private Connection conn;

    private boolean pending;
    private Profile[] friends;

    private String message;

    /**
     * Creates a new friend object, doesn't necessarily persist
     *
     * @param conn    the database
     * @param pending if the friendship is pending
     * @param friend1 from
     * @param friend2 to
     */
    Friends(Connection conn, boolean pending, Profile friend1, Profile friend2, String message) {

        this.pending = pending;
        this.friends = new Profile[]{friend1, friend2};
        this.message = message;

    }

    /**
     * Inserts new row to Pending_Friends
     *
     * @param conn    the database
     * @param friend1 from
     * @param friend2 to
     * @param message accompanied
     * @throws SQLException on failure
     */
    public static void addPending(Connection conn, Profile friend1, Profile friend2, String message)
            throws SQLException {

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Pending_Friends (fromID, toID, message) " +
                        "VALUES (?, ?, ?)"
        );
        ps.setString(1, friend1.getUserID());
        ps.setString(2, friend2.getUserID());
        ps.setString(3, message);

        ps.executeUpdate();
    }

	//HELP
    public boolean confirm() throws SQLException {

        if (!pending) return false;

        delete();
        conn.createStatement().executeUpdate(
                "INSERT INTO Friends (userID1, userID2, JDate, message) " +
                        "VALUES ('" +
                        friends[0].getUserID() + "', '" +
                        friends[1].getUserID() + "', '" +
                        new Date(System.currentTimeMillis()) + "', '" +
                        message +
                        "')"
        );

        return pending = true;
    }

    public void delete() throws SQLException {

        Statement stmt;
        if (pending) {
			stmt = conn.prepareStatement(
				"DELETE FROM Pending_Friends " +
                            "WHERE fromID = ? AND toID = ?"
			);
			stmt.setString(1, friends[0]);
			stmt.setString(2, friends[1]);           
		   
        } else {
            stmt = conn.prepareStatement(
				"DELETE FROM Pending_Friends " +
                            "WHERE fromID = '?' AND toID = '?'"
			);
			stmt.setString(1, friends[0]);
			stmt.setString(2, friends[1]); 
        }
		
		stmt.executeUpdate();

    }

    public boolean isPending() {
        return pending;
    }

    public Profile[] getFriends() {
        return friends;
    }

    public Profile getFrom() {
        return friends[0];
    }

    public Profile getTo() {
        return friends[1];
    }

    public String getMessage() {
        return message;
    }
}
