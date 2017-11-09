package Classes;

import java.util.LinkedList;

public class Group {

    String groupID;

    private User admin;

    private String name;
    private String description;
    private Integer membershipLimit;

    private LinkedList<User> members;

    public Group(User admin, String name, String description, Integer membershipLimit) {
        this.admin = admin;
        this.name = name;
        this.description = description;
        this.membershipLimit = membershipLimit;
    }

    public static Group createGroup(User admin, String name, String description, Integer membershipLimit) {
        return new Group(admin, name, description, membershipLimit);
    }

    public void initiateAddingGroup(User user) {
        initiateAddingGroup(user.userID);
    }

    public void initiateAddingGroup(String userID) {
        // check membership limit
        // get user/id
        // get messsage
    }
}
