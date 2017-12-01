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
)
/*ORGANIZATION EXTERNAL (
TYPE ORACLE_LOADER
DEFAULT DIRECTORY csvDir
ACCESS PARAMETERS (
  RECORDS DELIMITED BY NEWLINE
  FIELDS TERMINATED BY ','
    (
      "userID"
      CHAR (20),
      "name"
      CHAR (50),
      email
      CHAR (20),
      "password"
      CHAR (50),
      date_of_birth
      CHAR (20),
      lastlogin
      CHAR (20)
    )
)
LOCATION ('profile.csv')
)*/;



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
)
/*ORGANIZATION EXTERNAL (
TYPE ORACLE_LOADER
DEFAULT DIRECTORY csvDir
ACCESS PARAMETERS (
  RECORDS DELIMITED BY NEWLINE
  FIELDS TERMINATED BY ','
    (
      userID1
      CHAR (20),
      userID2
      CHAR (20),
      JDate
      CHAR (20),
      message
      CHAR (200)
    )
)
LOCATION ('friends.csv')
)*/;

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
  CONSTRAINT MESSAGE_FK2 FOREIGN KEY (fromID) REFERENCES PROFILE (userID),
  --date wrongly specified
  --CONSTRAINT valid_send_date CHECK
  --(dateSent < CURRENT_DATE),
  CONSTRAINT valid_sent_to CHECK
  (toUserID IS NULL OR toGroupID IS NULL)
  --figure out how to set foreign key to userID or groupID as appropriate
  -- @Roy this is fixed
)
/*ORGANIZATION EXTERNAL (
TYPE ORACLE_LOADER
DEFAULT DIRECTORY csvDir
ACCESS PARAMETERS (
  RECORDS DELIMITED BY NEWLINE
  FIELDS TERMINATED BY ','
    (
      msgID
      CHAR (20),
      fromID
      CHAR (20),
      message
      CHAR (200),
      toUserID
      CHAR (20),
      toGroupID
      CHAR (20),
      dateSent
      CHAR (20)
    )
)
LOCATION ('message.csv')
)*/;


DROP TABLE MESSAGE_RECIPIENT CASCADE CONSTRAINTS;
CREATE TABLE MESSAGE_RECIPIENT (
  msgID  VARCHAR2(20) NOT NULL,
  userID VARCHAR2(20) NOT NULL,
  CONSTRAINT MESSAGE_RECIPIENT_FK1 FOREIGN KEY (msgID) REFERENCES MESSAGE (msgID),
  CONSTRAINT MESSAGE_RECIPIENT_FK2 FOREIGN KEY (userID) REFERENCES PROFILE (userID)
)
/*ORGANIZATION EXTERNAL (
TYPE ORACLE_LOADER
DEFAULT DIRECTORY csvDir
ACCESS PARAMETERS (
  RECORDS DELIMITED BY NEWLINE
  FIELDS TERMINATED BY ','
    (
      msgID
      CHAR (20),
      userID
      CHAR (20)
    )
)
LOCATION ('message_recipient.csv')
)*/;

--assume group doesnt need description
--assume different groups can have same name 
DROP TABLE GROUPS CASCADE CONSTRAINTS;
CREATE TABLE GROUPS (
  gID         VARCHAR2(20) NOT NULL,
  name        VARCHAR2(50) NOT NULL,
  description VARCHAR2(200),
  CONSTRAINT GROUPS_PK PRIMARY KEY (gID),
  CONSTRAINT GROUPS_UN UNIQUE (name, description)
)
/*ORGANIZATION EXTERNAL (
TYPE ORACLE_LOADER
DEFAULT DIRECTORY csvDir
ACCESS PARAMETERS (
  RECORDS DELIMITED BY NEWLINE
  FIELDS TERMINATED BY ','
    (
      gID
      CHAR (20),
      "name"
      CHAR (50),
      description
      CHAR (200)
    )
)
LOCATION ('groups.csv')
)*/;

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
)
/*ORGANIZATION EXTERNAL (
TYPE ORACLE_LOADER
DEFAULT DIRECTORY csvDir
ACCESS PARAMETERS (
  RECORDS DELIMITED BY NEWLINE
  FIELDS TERMINATED BY ','
    (
      gID
      CHAR (20),
      userID
      CHAR (20),
      "role"
      CHAR (20)
    )
)
LOCATION ('group_membership.csv')
)*/;


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


--view pending friends and groups

	
