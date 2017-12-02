package Tables;

import java.sql.Connection;
import java.util.Arrays;

public class Friends {

    private Connection conn;

    private boolean pending;
    private Profile[] friends;

    private String message;

    /**
     * Creates a new friend object, doesn't necessarily persist
     *
     * @param pending if the friendship is pending
     * @param friend1 first friend (adding friend?)
     * @param friend2 second friend (confirming friend?)
     */
    public Friends(boolean pending, Profile friend1, Profile friend2) {

        this.pending = pending;
        this.friends = new Profile[]{friend1, friend2};

    }

    public static void addPending(Connection conn, Profile friend1, Profile friend2) {

        Friends pending = new Friends(true, friend1, friend2);

        // save

    }

    public boolean confirm() {

        if (!pending) return false;

        // Move from pending to friends
        // Stored procedure?

        return true;
    }

    public void delete() {

    }

    public boolean isPending() {
        return pending;
    }

    public Profile[] getFriends() {
        return friends;
    }

    public Profile getOther()

    public Profile getFrom() {
        return friends[0];
    }

    public Profile getTo() {
        return friends[1];
    }

    public String getMessage() {

        if (pending) return message;
        else return null;

    }
}
