import Tables.Friends;
import Tables.Group;
import Tables.GroupMembership;
import Tables.Profile;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class FaceSpace {

    public static void main(String[] args) {

        // Establish connection
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager
                    .getConnection("jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // Prompt
        Scanner sc = new Scanner(System.in);
        Profile loggedIn = null;

        while (true) {
            // Get input
            System.out.println("Enter a command\n" +
                    "1. Create User\n" +
                    "2. Log in\n" +
                    "3. Initiate friendship\n" +
                    "4. Confirm friendship\n" +
                    "5. Display friends\n" +
                    "6. Create group\n" +
                    "7. Initiate adding Group\n" +
                    "8. Send message to user\n" +
                    "9. Send message to group\n" +
                    "10. Display messages\n" +
                    "11. Display New Messages\n" +
                    "12. Search for user\n" +
                    "13. Three degrees\n" +
                    "14. Top messages\n" +
                    "15. Drop user\n" +
                    "16. Log out\n" +
                    "Q to quit" +
                    "Selection:");

            String input = sc.nextLine();
            Integer selection;

            // Check quit
            if (input.equalsIgnoreCase("Q")) {
                break;
            }

            // Attempt parse
            try {
                selection = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid selection");
                continue;
            }

            // Check permissions
            if ((selection >= 3
                    || selection <= 12
                    || selection == 16)
                    && loggedIn == null) {
                System.out.println("Log in first");
                continue;
            }

            // Handle function
            switch (selection) {
                case 1:

                    // Create profile
                    Profile newProfile;
                    try {
                        newProfile = Profile.create(conn,
                                get(sc, "your name"),
                                get(sc, "your email"),
                                getDate(sc, "date of birth"));
						
						
						//HELP
                        PreparedStatement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT Password FROM Profile WHERE userID = " + newProfile.getUserID());
                        rs.next();

                        System.out.println("UserID: " + newProfile.getUserID() +
                                "\nPassword : " + rs.getString(1));

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 2:

                    // Login
                    //will set loggedIn to appropriot profile iff there is exactly one match in profiles
                    try {

                        loggedIn = Profile.login(conn, get(sc, "userID"), get(sc, "password"));
                        if (loggedIn != null) {
                            System.out.println("logged in as " + loggedIn.getName());
                        } else {
                            System.out.println("Invalid login");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 3:

                    // Initiate friendship
                    try {
                        Profile other = Profile.get(conn, get(sc, "user's ID"), false);

                        System.out.println(other);

                        loggedIn.initiateFriendship(other, get(sc, "message"));

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 4:

                    // Get list
                    ArrayList<Friends> pendingFriends = null;
                    ArrayList<GroupMembership> pendingGroups = null;
                    try {
                        assert loggedIn != null;
                        pendingFriends = loggedIn.displayPendingFriends();
                        pendingGroups = loggedIn.displayPendingGroups(pendingFriends.size());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    // TODO possible off by 1
                    assert pendingFriends != null && pendingGroups != null;
                    boolean[] confirmed = new boolean[pendingFriends.size() + pendingGroups.size()];
                    while (!(input = get(sc, "an index")).equals("\n")) {
                        try {
                            int select = Integer.parseInt(input);
                            if (select <= pendingFriends.size()) {
                                pendingFriends.get(select - 1).confirm();
                            } else {
                                pendingGroups.get(select - (pendingFriends.size() + 1)).confirm();
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input, strike return to exit");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        for (int i = 0; i < confirmed.length; i++) {
                            if (i <= pendingFriends.size() && confirmed[i]) {
                                pendingFriends.get(i - 1).delete();
                            } else if (i > pendingFriends.size() && confirmed[i]) {
                                pendingGroups.get(i - (pendingFriends.size() + 1)).delete();
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 5:

                    // Get friends
                    ArrayList<Profile> friends;
                    try {
                        assert loggedIn != null;
                        friends = loggedIn.displayFriends();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    // Get profiles, print more info
                    while (!(input = get(sc, "a userID to view profile")).equals("\n")) {
                        try {
                            System.out.println(Profile.get(conn, input, true));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input, strike return to exit");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    break;

                case 6:

                    Group newGroup;
                    try {
                        newGroup = Group.create(
                                conn,
                                loggedIn,
                                get(sc, "name"),
                                get(sc, "description"),
                                Integer.parseInt(get(sc, "maximum number of members"))
                        );

                        System.out.println(newGroup);

                    } catch (NumberFormatException e) {
                        System.out.println("This is not a number");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 7:

                    // Add to group
                    try {
                        Profile other = Profile.get(conn, get(sc, "user's ID"), false);
                        Group group = Group.get(conn, get(sc, "group's ID"));

                        System.out.println(other);

                        group.initiateAddUser(other, get(sc, "message"));

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 8:

                    Profile toProfile;
                    try {
                        toProfile = Profile.get(conn, get(sc, "id of user"), true);

                        assert loggedIn != null;
                        loggedIn.sendMessageToUser(toProfile, get(sc, "message"), conn);

                    } catch (Exception e) {

                    }
                    //display name
                    //enter message


                case 9:

                case 10:

                case 11:

                case 12:

                case 13:

                case 14:

                case 16:
                    assert loggedIn != null;
                    try {
                        loggedIn.logout();
                    } catch (Exception e) {
                    }
                    ;

            }
        }
    }


    private static String get(Scanner sc, String var) {

        System.out.format("Enter %s:\n", var);

        return sc.nextLine();
    }

    private static Date getDate(Scanner sc, String var) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date_of_birth = null;

        try {
            date_of_birth = (Date) dateFormat.parse(get(sc, "your " + var + " (MM/dd/yyyy):"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date_of_birth;
    }
}
