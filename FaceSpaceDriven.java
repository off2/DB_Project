import Tables.Friends;
import Tables.Group;
import Tables.GroupMembership;
import Tables.Profile;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class FaceSpaceDriven {

    public static void main(String[] args) {

        // Establish connection
        boolean success = false;
        Connection conn = null;

        String username = "off2";
        String password = "3960426";

        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass", username, password);

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

        int counter = 0;
        String[] inputs = new String[]{
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "16", "15", "q"
        };

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
                "Q to quit\n" +
                "Selection:");

        while (true) {
            // Get input


            String input = inputs[counter++];

            System.out.println("\n\nSimulated input: " + input);

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
            if ((selection >= 3 && selection <= 12
                    || selection == 16)
                    && loggedIn == null) {
                System.out.println("Log in first");
                continue;
            }

            // Handle function
            switch (selection) {
                case 1:

                    // Create profile
                    try {

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Profile newProfile = Profile.create(
                                conn,
                                "Roy Gustafson",
                                "rag94@pitt.edu",
                                "password",
                                new Date(sdf.parse("31/05/1996").getTime())
                        );

                        assert newProfile != null;
                        System.out.println("UserID: " + newProfile.getUserID() +
                                "\nPassword : " + "password");

                        System.out.println(newProfile);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    break;

                case 2:

                    // Login
                    // Will set loggedIn to appropriate profile if successful
                    try {
                        loggedIn = Profile.login(conn, "jy21", "password");
                        if (loggedIn != null) {
                            System.out.println("logged in as " + loggedIn.getName());
                        } else {
                            System.out.println("Invalid login");
                        }

                        System.out.println(loggedIn);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 3:

                    // Initiate friendship
                    try {
                        Profile other = Profile.get(conn, "ba22", false);

                        System.out.println(other);

                        assert loggedIn != null;
                        loggedIn.initiateFriendship(other, "Ay boo boo");

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
                        pendingGroups = loggedIn.displayPendingGroups();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    // TODO possible off by 1
                    assert pendingFriends != null && pendingGroups != null;
                    boolean[] confirmed = new boolean[pendingFriends.size() + pendingGroups.size()];

                    // While user enters input

                    int[] indices = new int[]{1, 2, 3};

                    if (pendingFriends.size() == 0 || pendingGroups.size() == 0) {
                        System.out.println("No pending friends");
                        break;
                    }

                    for (int i = 0; i < indices.length; i++) {
                        // Confirm
                        try {
                            int select = indices[i];
                            if (select <= pendingFriends.size()) {
                                pendingFriends.get(select - 1).confirm();
                                confirmed[select - 1] = true;
                            } else if(select - pendingFriends.size() <= pendingGroups.size()) {
                                pendingGroups.get(select - (pendingFriends.size() + 1)).confirm();
                                confirmed[select - 1] = true;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input, strike return to exit");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    // Delete all remaining
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
                    assert loggedIn != null;
                    // Get profiles, print more info

                    String[] ids = new String[]{"nnk28", "kr29", "ccg31"};

                    for (int i = 0; i < ids.length; i++) {
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
                    try {
                        Group newGroup = Group.create(
                                conn,
                                loggedIn,
                                "New Group",
                                "Catchy description",
                                15
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
                        Profile other = Profile.get(conn, "sst32", false);
                        Group group = Group.get(conn, "2");

                        System.out.println(other);
                        System.out.println(group);

                        group.initiateAddUser(other, "Ay boo boo");

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 8:

                    // Send to user
                    try {
                        Profile other = Profile.get(conn, "hhl61", true);

                        System.out.println(other);

                        assert loggedIn != null;
                        loggedIn.sendMessageToUser(other, "Ay boo boo");

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 9:

                    // Send to group
                    try {
                        Group group = Group.get(conn, "2");

                        System.out.println(group);

                        assert loggedIn != null;
                        loggedIn.sendMessageToGroup(group, "Ay boo boos");

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 10:

                    // Display messages
                    try {
                        assert loggedIn != null;
                        loggedIn.displayMessages();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 11:

                    // Display new messages
				/*
				   try {
                        assert loggedIn != null;
                        loggedIn.displayNewMessages();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
					*/

                    break;


                case 12:

                    // Search for all keys
                    try {
                        String[] queryStrings = new String[]{"Nathalia", "dd14@pitt.edu", "Bessy"};
                        PreparedStatement userSearch;

                        for (int i = 0; i < queryStrings.length; i++) {
                            userSearch = conn.prepareStatement(
                                    "SELECT * FROM PROFILE WHERE userID = ? OR name = ? OR email = ?"
                            );
                            userSearch.setString(1, queryStrings[i]);
                            userSearch.setString(2, queryStrings[i]);
                            userSearch.setString(3, queryStrings[i]);

                            ResultSet rs = userSearch.executeQuery();
                            System.out.println("Results for " + queryStrings[i] + ":");
                            while (rs.next()) {
                                System.out.println(rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3));
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 13:

                    //threeDegrees

                    try {
                        Profile A = Profile.get(conn, "bbs17", true);
                        Profile B = Profile.get(conn, "ddb49", true);

                        // Check if friendship exists (0 degrees)
                        // Check if shared friend (1 degree)

                        assert A != null && B != null;
                        ArrayList<Profile> aFriends = A.displayFriends();
                        ArrayList<Profile> bFriends = B.displayFriends();
                        ArrayList<Profile> aFriendFriends;
                        boolean found = false;

                        for (Profile aFriend : aFriends) {
                            if (aFriend.getUserID().equals(B.getUserID())) {
                                System.out.println(A.getUserID() + " and " + B.getUserID() + " are friends");
                                found = true;
                            }
                        }

                        if (!found) {
                            for (Profile aFriend : aFriends) {
                                aFriendFriends = aFriend.displayFriends();
                                for (Profile aFriendFriend : aFriendFriends) {
                                    if (aFriendFriend.getUserID().equals(B.getUserID())) {
                                        System.out.println(A.getUserID() + " is friends with " + aFriend.getUserID() + " who is friends with " + B.getUserID());
                                        found = true;
                                    }

                                }
                            }

                        }

                        if (!found) {
                            for (Profile aFriend : aFriends) {
                                aFriendFriends = aFriend.displayFriends();
                                for (Profile aFriendFriend : aFriendFriends) {
                                    for (Profile bFriend : bFriends) {
                                        if (aFriendFriend.getUserID().equals(bFriend.getUserID())) {
                                            System.out.println(A.getUserID() + " is friends with " + aFriend.getUserID() + " who is frinds with " + aFriendFriend.getUserID() + " who is friends with " + B.getUserID());
                                            found = true;
                                        }
                                    }

                                }
                            }

                        }

                        if (!found) System.out.println("not linked within three friends");
                        //

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 14:

                    // Top messages
                    try {
                        // Get input
                        int k = 4;
                        int x = 1;

                        // Date math
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(System.currentTimeMillis());
                        cal.add(Calendar.MONTH, -x);

                        // Query
                        PreparedStatement ps = conn.prepareStatement(
                                "SELECT profile.userID, profile.name, " +
                                        "COUNT(message.msgID) AS sentCount, " +
                                        "COUNT(message_recipient.msgID) AS recCount " +
                                        "FROM Profile " +
                                        "JOIN Message ON message.fromID = profile.userID " +
                                        "JOIN Message_Recipient ON message_recipient.userID = profile.userID " +
                                        "WHERE datesent > ? " +
                                        "ORDER BY sentCount + recCount DESC"
                        );
                        ps.setDate(1, new Date(cal.getTimeInMillis()));
                        ResultSet rs = ps.executeQuery();

                        while (rs.next() || k != 0) {
                            System.out.format(
                                    "%s (%s)\n%d messages sent, %d messages received\n\n",
                                    rs.getString(2),
                                    rs.getString(1),
                                    rs.getInt(3),
                                    rs.getInt(4)
                            );
                            k--;
                        }


                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 15:

                    // Drop user
                    try {
                        assert loggedIn != null;
                        Profile other = Profile.get(conn, loggedIn.getUserID(), true);
                        PreparedStatement ps = conn.prepareStatement("DELETE FROM Profile WHERE userID = ?");
                        ps.setString(1, other.getUserID());
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

                case 16:

                    // Logout
                    try {
                        loggedIn.logout();
                        System.out.println(loggedIn);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

}
