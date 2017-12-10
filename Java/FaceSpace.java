import Tables.Friends;
import Tables.Group;
import Tables.GroupMembership;
import Tables.Profile;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class FaceSpace {

    public static void main(String[] args) {

        // Establish connection
        boolean success = false;
        Connection conn = null;
        try {
            URL[] jar = new URL[]{new URL("ojdbc7.jar")};
            String drname = "oracle.jdbc.driver.OracleDriver";
            String connection = "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass";

            Driver driver = (Driver) Class.forName(drname, true, new URLClassLoader(jar)).newInstance();
            DriverManager.registerDriver(driver);
            conn = DriverManager.getConnection(connection);

            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!success) System.exit(0);

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
                        newProfile = Profile.create(
                                conn,
                                get(sc, "your name"),
                                get(sc, "your email"),
                                getDate(sc, "date of birth")
                        );

                        PreparedStatement ps = conn.prepareStatement(
                                "SELECT Password FROM Profile WHERE userID = ?"
                        );
                        ps.setString(1, newProfile.getUserID());
                        ResultSet rs = ps.executeQuery();

                        rs.next();
                        System.out.println("UserID: " + newProfile.getUserID() +
                                "\nPassword : " + rs.getString(1));

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 2:

                    // Login
                    // Will set loggedIn to appropriate profile if successful
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

                    // While user enters input
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

                    // Create group
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
                    Profile other;
                    Group group;
                    try {
                        other = Profile.get(conn, get(sc, "ID of user"), false);
                        group = Group.get(conn, get(sc, "ID of group"));

                        System.out.println(other);
                        System.out.println(group);

                        group.initiateAddUser(other, get(sc, "message"));

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 8:

                    // Send to user
                    try {
                        other = Profile.get(conn, get(sc, "id of user"), true);

                        System.out.println(other);

                        assert loggedIn != null;
                        loggedIn.sendMessageToUser(other, get(sc, "message"));

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 9:

                    // Send to group
                    try {
                        group = Group.get(conn, get(sc, "id of group"));

                        System.out.println(group);

                        assert loggedIn != null;
                        loggedIn.sendMessageToGroup(group, get(sc, "message"));

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 10:

                    try {
                        assert loggedIn != null;
                        loggedIn.displayMessages();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 11:

                    try {
                        assert loggedIn != null;
                        loggedIn.displayNewMessages();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 12:


                    break;

                case 13:


                    break;

                case 14:


                    break;

                case 16:
                    try {
                        loggedIn.logout();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

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
