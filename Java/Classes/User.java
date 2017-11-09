package Classes;

import java.sql.Date;
import java.util.LinkedList;

public class User {

    String userID;

    private String name;
    private String email;
    private Date dob;

    public User(String name, String email, Date dob) {
        this.name = name;
        this.email = email;
        this.dob = dob;
    }

    public static User login(String userID, String password) {

    }

    public boolean logout() {

    }

    public boolean initiateFriendship(User friend, String message) {
        return initiateFriendship(friend.userID, message);
    }

    public boolean initiateFriendship(String userID, String message) {
        // Add to pending friendships?

        return true;
    }

    public boolean confirmFriendship() {
        // Get formatted, numbered list
        // Friend requests + message
        // Group requests + message
        // Take number
        // Confirm friends


    }

    public boolean displayFriends() {
        // Get this users
        // Friends + UserID's
        // Friends of Friends
        // Take userID
        // Load friend
        // go back or main menu
        // quit or loop
    }

    public boolean sendMessageToUser(User user) {
        return sendMessageToUser(user.userID);
    }

    public boolean sendMessageToUser(String userID) {

    }

    public boolean sendMessageToGroup(Group group) {
        return sendMessageToGroup(group.groupID);
    }

    public boolean sendMessageToGroup(String groupID) {

    }

    public LinkedList<Message> displayMessages() {

    }

    public LinkedList<Message> displayNewMessages() {

    }

}
