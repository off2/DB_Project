/*
NOTE FROM ROY
We need to write all the functions/procedures/views
 - Procedures: take arguments, no return
 - Functions: take arguments, return one value
 - Views: take arguments, return SELECT statement

Once we've written them, we then need to seperate them into
 - procedures.sql
 - functions.sql
 - views.sql
*/

--assume dob isnt required 
DROP TABLE PROFILE CASCADE CONSTRAINTS;
CREATE TABLE PROFILE (
  userID        VARCHAR2(20) NOT NULL,
  name          VARCHAR2(50) NOT NULL,
  email			VARCHAR2(20) NOT NULL,
  password      VARCHAR2(50) NOT NULL,
  date_of_birth DATE,
  lastlogin     TIMESTAMP,

  CONSTRAINT PROFILE_PK PRIMARY KEY (userID),

  CONSTRAINT valid_last_login CHECK
  (lastlogin IS NULL OR lastlogin < CURRENT_TIMESTAMP),
  CONSTRAINT valid_birthday CHECK
  (date_of_birth IS NULL OR date_of_birth < add_months(current_date, -12 * 13))
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
DROP TABLE PENDING_FRIENDS CASCADE CONSTRAINTS;
CREATE TABLE PENDING_FRIENDS (
  fromID  VARCHAR2(20) NOT NULL,
  toID    VARCHAR2(20) NOT NULL,
  message VARCHAR2(200) DEFAULT 'Let''s be friends',

  CONSTRAINT PENDING_UN UNIQUE (fromID, toID),
  CONSTRAINT PENDING_FK1 FOREIGN KEY (fromID) REFERENCES PROFILE (userID),
  CONSTRAINT PENDING_FK1 FOREIGN KEY (toID) REFERENCES PROFILE (userID),

  CONSTRAINT no_profile_repeats CHECK (fromID != toID)
);

--assume can message yourself
--assume cannot send empty message 
DROP TABLE MESSAGES CASCADE CONSTRAINTS;
CREATE TABLE MESSAGES (
  msgID     VARCHAR2(20)  NOT NULL,
  fromID    VARCHAR2(20)  NOT NULL,
  message   VARCHAR2(200) NOT NULL,
  toUserID  VARCHAR2(20) DEFAULT NULL,
  toGroupID VARCHAR2(20) DEFAULT NULL,
  dateSent  DATE          NOT NULL,

  CONSTRAINT MESSAGE_PK PRIMARY KEY (msgID),
  CONSTRAINT MESSAGE_FK2 FOREIGN KEY (fromID) REFERENCES PROFILE (userID),

  CONSTRAINT valid_send_date CHECK
  (dateSent IS NULL OR dateSent < CURRENT_DATE),
  CONSTRAINT valid_sent_to CHECK
  (toUserID IS NULL OR toGroupID IS NULL)
  --figure out how to set foreign key to userID or groupID as appropriate
  -- @Roy this is fixed

);


DROP TABLE MESSAGE_RECIPIENT CASCADE CONSTRAINTS;
CREATE TABLE MESSAGE_RECIPIENT (
  msgID  VARCHAR2(20) NOT NULL,
  userID VARCHAR2(20) NOT NULL,

  CONSTRAINT MESSAGE_RECIPIENT_FK1 FOREIGN KEY (msgID) REFERENCES MESSAGES (msgID),
  CONSTRAINT MESSAGE_RECIPIENT_FK2 FOREIGN KEY (userID) REFERENCES PROFILE (userID)

);

--assume group doesnt need description
--assume different groups can have same name 
DROP TABLE GROUPS CASCADE CONSTRAINTS;
CREATE TABLE GROUPS (
  gID         VARCHAR2(20) NOT NULL,
  name        VARCHAR2(50) NOT NULL,
  description VARCHAR2(200),

  CONSTRAINT GROUPS_PK PRIMARY KEY (gID) -- , @roy don't forget the comma

  --Is there a problem with having two differnt groups both named "Squad"
  --with description "Squad Groupchat"?
  --CONSTRAINT GROUPS_UN UNIQUE (name, description)
  -- @roy idt you can do that on real facebook
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

  CONSTRAINT member_or_admin
  CHECK (role = 'Member' OR role = 'Admin')

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

--triggers


--delete friend request when new friend is added 
CREATE OR REPLACE TRIGGER NewFriend
AFTER INSERT ON FRIENDS
REFERENCING NEW AS newFriend
  BEGIN
    DELETE FROM PENDING_FRIENDS
    WHERE (fromID = :newFriend.userID1 AND toID = :newFriend.userID2);
  END;


--delete group request when member is addded to group
--assume 
CREATE OR REPLACE TRIGGER NewGroupMembership
<<<<<<< HEAD
	AFTER INSERT on GROUP_MEMBERSHIP
	REFERENCING NEW AS newGroup
	BEGIN	
		DELETE FROM PENDING_GROUPMEMBERS
		WHERE (gID == :newGroup.gID and userID == :newGroup.userID)
	END;
	

	
	
--view pending friends and groups 
=======
AFTER INSERT ON GROUP_MEMBERSHIP
REFERENCING NEW AS newGroup
  BEGIN
    DELETE FROM PENDING_GROUPMEMBERS
    WHERE (gID = :newGroup.gID AND userID = :newGroup.userID);
  END;
	
>>>>>>> origin/master
