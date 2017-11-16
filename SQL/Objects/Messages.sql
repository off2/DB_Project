-- Send message to user or members of group
CREATE TRIGGER sendMesage
BEFORE INSERT ON Messages

  BEGIN

    IF (new.toGroupID = NULL)
    THEN

      INSERT INTO
        Message_Recipient (msgID, userID)
      VALUES
        (new.msgID, new.toUserID);

    ELSEIF (new.toUserID = NULL)
      THEN

        FOR groupMember IN (SELECT userID
                            FROM Group_Membership
                            WHERE gID = new.toGroupID)
        LOOP
          INSERT INTO
            Message_Recipient (msgID, userID)
          VALUES
            (new.msgID, groupMember.userID);
        END LOOP;

    END IF;

  END sendMesage;
/

-- displayMessages

-- displayNewMessages

