package Classes;


import sun.awt.image.ImageWatched;

import java.sql.Date;
import java.util.LinkedList;

public class Messages {

    private User assoc;
    private LinkedList<Message> messages;

    public class Message {

        private String msgID;

        private String fromID;
        private String toID;        // user or group
        private boolean group;      // t    or f
        private Date dateSent;

        private String body;        // Message itself

    }

}
