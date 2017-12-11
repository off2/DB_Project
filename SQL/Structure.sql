--CREATE DIRECTORY csvDir AS '/../Data/';

-- Assume dob isn't required
DROP TABLE PROFILE CASCADE CONSTRAINTS;
CREATE TABLE PROFILE (
  userID        VARCHAR2(20) NOT NULL,
  name          VARCHAR2(50) NOT NULL,
  email         VARCHAR2(20) NOT NULL,
  password      VARCHAR2(50) NOT NULL,
  date_of_birth DATE,
  lastlogin     TIMESTAMP,
  CONSTRAINT PROFILE_PK PRIMARY KEY (userID)
);

--assume can only befriend someone once
--assume cannot be friends with yourself
DROP TABLE FRIENDS CASCADE CONSTRAINTS;
CREATE TABLE FRIENDS (
  userID1 VARCHAR2(20)  NOT NULL,
  userID2 VARCHAR2(20)  NOT NULL,
  JDate   DATE          NOT NULL,
  message VARCHAR2(200) NOT NULL,
  CONSTRAINT FRIENDS_UN UNIQUE (userID1, userID2),
  CONSTRAINT FRIENDS_FK1 FOREIGN KEY (userID1) REFERENCES PROFILE (userID),
  CONSTRAINT FRIENDS_FK2 FOREIGN KEY (userID2) REFERENCES PROFILE (userID),
  CONSTRAINT no_self_friend CHECK (userID1 != userID2)
);

--assume cant send multiple friend requests to same person
--assume cant friend self
--message defaults to let's be friends 
--problem with creation
DROP TABLE PENDING_FRIENDS CASCADE CONSTRAINTS;
CREATE TABLE PENDING_FRIENDS (
  fromID  VARCHAR2(20) NOT NULL,
  toID    VARCHAR2(20) NOT NULL,
  message VARCHAR2(200) DEFAULT 'Let''s be friends',
  CONSTRAINT PENDING_UN UNIQUE (fromID, toID),
  CONSTRAINT PENDING_FK1 FOREIGN KEY (fromID) REFERENCES PROFILE (userID),
  CONSTRAINT PENDING_FK2 FOREIGN KEY (toID) REFERENCES PROFILE (userID),
  CONSTRAINT no_profile_repeats CHECK (fromID != toID)
);

--assume group doesnt need description
--assume different groups can have same name 
DROP TABLE GROUPS CASCADE CONSTRAINTS;
CREATE TABLE GROUPS (
  gID         VARCHAR2(20) NOT NULL,
  name        VARCHAR2(50) NOT NULL,
  description VARCHAR2(200),
  CONSTRAINT GROUPS_PK PRIMARY KEY (gID),
  CONSTRAINT GROUPS_UN UNIQUE (name, description)
);

--assume role defaults to member. role is either member or admin
DROP TABLE GROUP_MEMBERSHIP CASCADE CONSTRAINTS;
CREATE TABLE GROUP_MEMBERSHIP (
  gID    VARCHAR2(20) NOT NULL,
  userID VARCHAR2(20) NOT NULL,
  role   VARCHAR2(20) DEFAULT 'Member',
  CONSTRAINT GROUP_MEMBERSHIP_UN UNIQUE (gID, userID),
  CONSTRAINT GROUP_MEMBERSHIP_FK1 FOREIGN KEY (gID) REFERENCES GROUPS (gID),
  CONSTRAINT GROUP_MEMBERSHIP_FK2 FOREIGN KEY (userID) REFERENCES PROFILE (userID),
  CONSTRAINT member_or_admin CHECK (role = 'Member' OR role = 'Admin')
);


DROP TABLE PENDING_GROUPMEMBERS CASCADE CONSTRAINTS;
CREATE TABLE PENDING_GROUPMEMBERS (
  gID     VARCHAR2(20) NOT NULL,
  userID  VARCHAR2(20) NOT NULL,
  message VARCHAR2(200),
  CONSTRAINT PENDING_GROUPMEMBERS_UN UNIQUE (gID, userID),
  CONSTRAINT PENDING_GROUPMEMBERS_FK1 FOREIGN KEY (gID) REFERENCES GROUPS (gID),
  CONSTRAINT PENDING_GROUPMEMBERS_FK2 FOREIGN KEY (userID) REFERENCES PROFILE (userID)
);

--assume can message yourself
--assume cannot send empty message
--table or view does not exist
DROP TABLE MESSAGE CASCADE CONSTRAINTS;
CREATE TABLE MESSAGE (
  msgID     VARCHAR2(20)  NOT NULL,
  fromID    VARCHAR2(20)  NOT NULL,
  message   VARCHAR2(200) NOT NULL,
  toUserID  VARCHAR2(20) DEFAULT NULL,
  toGroupID VARCHAR2(20) DEFAULT NULL,
  dateSent  DATE          NOT NULL,
  CONSTRAINT MESSAGE_PK PRIMARY KEY (msgID),
  CONSTRAINT MESSAGE_FK FOREIGN KEY (fromID) REFERENCES PROFILE (userID),
  CONSTRAINT MESSAGE_FK2 FOREIGN KEY (toUserID) REFERENCES PROFILE (userID),
  CONSTRAINT MESSAGE_FK3 FOREIGN KEY (toGroupID) REFERENCES GROUPS (gID),
  CONSTRAINT valid_sent_to CHECK (toUserID IS NULL OR toGroupID IS NULL)
);


DROP TABLE MESSAGE_RECIPIENT CASCADE CONSTRAINTS;
CREATE TABLE MESSAGE_RECIPIENT (
  msgID  VARCHAR2(20) NOT NULL,
  userID VARCHAR2(20) NOT NULL,
  CONSTRAINT MESSAGE_RECIPIENT_FK1 FOREIGN KEY (msgID) REFERENCES MESSAGE (msgID),
  CONSTRAINT MESSAGE_RECIPIENT_FK2 FOREIGN KEY (userID) REFERENCES PROFILE (userID)
);

-- Drop user
CREATE OR REPLACE TRIGGER dropUser
  BEFORE DELETE
  ON PROFILE

  BEGIN

    -- Delete from friends
    DELETE FROM FRIENDS
    WHERE userID1 = :new.userID
          OR userID2 = :new.userID;

    -- Delete from pending_friends
    DELETE FROM PENDING_FRIENDS
    WHERE toID = :new.userID;

    -- Delete from group_membership
    DELETE FROM GROUP_MEMBERSHIP
    WHERE userID = :new.userID;

    -- Delete from pending_groupmembers
    DELETE FROM PENDING_GROUPMEMBERS
    WHERE userID = :new.userID;

    -- If message should be deleted
    FOR msg IN (SELECT *
                FROM Message
                WHERE fromID = :new.userID
                      OR toUserID = :new.userID)
    LOOP
      IF (SELECT COUNT(*)
          FROM Profile
          WHERE userID = msg.fromID) = 0
         OR ((SELECT COUNT(*)
              FROM Profile
              WHERE userID = msg.toUserID) = 0
             AND msg.toUserID IS NOT NULL)
      THEN
        DELETE FROM Message
        WHERE CURRENT OF msg;
      END IF;
    END LOOP;

    -- Message recipient
    DELETE FROM MESSAGE_RECIPIENT
    WHERE userID = :new.userID;

  END;

-- 8 and 9
CREATE OR REPLACE TRIGGER sendMessage
  BEFORE INSERT
  ON Message

  BEGIN

    IF (:new.toGroupID IS NULL)
    THEN

      INSERT INTO MESSAGE_RECIPIENT (msgID, userID)
      VALUES (:new.msgID, :new.toUserID);

    ELSIF (:new.toGroupID IS NULL)
      THEN

        FOR groupMember IN (SELECT userID
                            FROM GROUP_MEMBERSHIP
                            WHERE gID = :new.toGroupID)
        LOOP

          INSERT INTO MESSAGE_RECIPIENT (msgID, userID)
          VALUES (:new.msgID, groupMember.userID);

        END LOOP;

    END IF;

  END sendMessage;
/