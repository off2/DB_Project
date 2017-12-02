package Tables;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

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
     * @throws SQLException on failure
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

        Statement stmt = conn.createStatement();
        stmt.executeUpdate(
                "INSERT INTO Pending_Friends (fromID, toID, message) " +
                        "VALUES (" +
                        friend1.getUserID() + ", " +
                        friend2.getUserID() + ", " +
                        message +
                        ")"
        );

    }

    public boolean confirm() throws SQLException {

        if (!pending) return false;

        delete();
        conn.createStatement().executeUpdate(
                "INSERT INTO Friends (userID1, userID2, JDate, message) " +
                        "VALUES (" +
                        friends[0].getUserID() + ", " +
                        friends[1].getUserID() + ", " +
                        new Date(System.currentTimeMillis()) + ", " +
                        message +
                        ")"
        );

        return true;
    }

    public void delete() throws SQLException {
        Statement stmt = conn.createStatement();

        if (pending) {
            stmt.executeUpdate(
                    "DELETE FROM Pending_Friends " +
                            "WHERE fromID = " + friends[0] +
                            " AND toID = " + friends[1]
            );
        } else {
            stmt.executeUpdate(
              "DELETE FROM Friends " +
                      "WHERE userID1 = " + friends[0] +
                      " AND userID2 = " + friends[1]
            );
        }

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
