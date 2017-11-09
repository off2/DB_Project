package Classes;

import java.sql.Date;

public class Admin {

    public static User createUser(String name, String email, Date dob) {

    }

    public static User searchForUser(String searchCriteria) {

    }

    public static User threeDegrees(User a, User b) {
        return threeDegrees(a.userID, b.userID);
    }

    public static User threeDegrees(String aID, String bID) {

    }

    public static boolean dropUser(User user) {

    }

    public static boolean dropUser(String userID) {

    }

    public static Group createGroup(User admin, String name, String description, Integer membershipLimit) {

    }
}
