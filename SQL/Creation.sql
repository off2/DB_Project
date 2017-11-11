DROP TABLE PROFILE CASCADE CONSTRAINTS;
DROP TABLE FRIENDS CASCADE CONSTRAINTS;
DROP TABLE PENDING_FRIENDS CASCADE CONSTRAINTS;
DROP TABLE MESSAGES CASCADE CONSTRAINTS;
DROP TABLE MESSAGE_RECIPIENT CASCADE CONSTRAINTS;
DROP TABLE GROUPS CASCADE CONSTRAINTS;
DROP TABLE GROUP_MEMBERSHIP CASCADE CONSTRAINTS;
DROP TABLE PENDING_GROUPMEMBERS CASCADE CONSTRAINTS;

/* 
to do 
-message to person vs to group distinction
-pending friend requests to friends
-pending group requests to group

-delete pending friend when friend request is accepted and new friend is added 
-update message resipient when new message is sent
-delete pending group membership request when new person is added to group



*/

--assume dob isnt required 
CREATE TABLE PROFILE(
	userID			varchar2(20) not NULL,
	name			varchar2(50) not NULL,
	password		varchar2(50) not NULL,
	date_of_birth	date,
	lastlogin		timestamp,
	CONSTRAINT valid_birthday
		CHECK (date_of_birth == null or (EXTRACT(YEAR FROM date_of_birth)) < (EXTRACT(YEAR FROM CURRENT_DATE) - 14)),
	CONSTRAINT valid_last_login
		CHECK (lastlogin == NULL or lastlogin < CURRENT_TIMESTAMP),
	CONSTRAINT PROFILE_PK PRIMARY KEY(userID)
		
		
	
);

--assume can only befriend someone once
--assume cannot be friends with yourself
CREATE TABLE FRIENDS(
	userID1			varchar2(20) not NULL,
	userID2			varchar2(20) not NULL,
	JDate			date not NULL,
	message			varchar2(200) not NULL,
	CONSTRAINT no_self_friend
		check(userID1 != userID2),
	CONSTRAINT no_double_friendship UNIQUE (userID1,userID2),
	CONSTRAINT FRIENDS_FK1 FOREIGN KEY(userID1) REFERENCES PROFILE(userID),
	CONSTRAINT FRIENDS_FK2 FOREIGN KEY(userID2) REFERENCES PROFILE(userID)
	
);

--assume cant send multiple friend requests to same person
--assume cant friend self
--message defaults to let's be friends 
CREATE TABLE PENDING_FRIENDS(
	fromID			varchar2(20) not NULL,
	toID			varchar2(20) not NULL,
	message			varchar2(200) not NULL default 'Let\'s be friends',
	CONSTRAINT dont_friend_self
		check(fromID != toID),
	CONSTRAINT no_re_requests  UNIQUE (fromID,toID),	
	CONSTRAINT PENDING_FK1 FOREIGN KEY(fromID) REFERENCES PROFILE(userID),
	CONSTRAINT PENDING_FK1 FOREIGN KEY(toID) REFERENCES PROFILE(userID)
	
);


--assume can message yourself
--assume cannot send empty message 
CREATE TABLE MESSAGES(
	msgID			varchar2(20) not NULL,
	fromID			varchar2(20) not NULL,
	message			varchar2(200) not NULL,
	toUserID		varchar2(20) default NULL,
	toGroupID		varchar2(20) default NULL,
	dateSent		date not NULL,
	CONSTRAINT valid_send_date
		CHECK (dateSent == null or dateSent < CURRENT_DATE),
	CONSTRAINT MESSAGE_PK PRIMARY KEY(msgID),
	CONSTRAINT MESSAGE_FK2 FOREIGN KEY(fromID) REFERENCES PROFILE(userID)
	--figure out how to set foreign key to userID or groupID as appropriate 


);



CREATE TABLE MESSAGE_RECIPIENT(
	msgID			varchar2(20) not NULL,
	userID			varchar2(20) not NULL,
	CONSTRAINT MESSAGE_RECIPIENT_FK1 FOREIGN KEY(msgID) REFERENCES MESSAGES(msgID),
	CONSTRAINT MESSAGE_RECIPIENT_FK2 FOREIGN KEY(userID) REFERENCES PROFILE(userID)

);

--assume group doesnt need description
--assume different groups can have same name 
CREATE TABLE GROUPS(
	gID				varchar2(20) not NULL,
	name			varchar2(50) not NULL,
	description		varchar2(200),
	CONSTRAINT GROUPS_PK PRIMARY KEY(gID),
	
	

);

--assume role defaults to member. role is either member or admin 
CREATE TABLE GROUP_MEMBERSHIP(
	gID				varchar2(20) not NULL,
	userID			varchar2(20) not NULL,
	role			varchar2(20) default 'Member',
	CONSTRAINT member_or_admin 
		CHECK (role == 'Member' or role == 'Admin'),
	CONSTRAINT no_double_group_membership UNIQUE (gID,userID),
	CONSTRAINT GROUP_MEMBERSHIP_FK1 FOREIGN KEY(gID) REFERENCES GROUPS(gID),
	CONSTRAINT GROUP_MEMBERSHIP_FK2 FOREIGN KEY(userID) REFERENCES PROFILE(userID)

);


CREATE TABLE PENDING_GROUPMEMBERS(
	gID				varchar2(20) not NULL,
	userID			varchar2(20) not NULL,
	message			varchar2(200),
	CONSTRAINT no_repeat_group_requests UNIQUE (gID,userID),
	CONSTRAINT PENDING_GROUPMEMBERS_FK1 FOREIGN KEY(gID) REFERENCES GROUPS(gID),
	CONSTRAINT PENDING_GROUPMEMBERS_FK2 FOREIGN KEY(userID) REFERENCES PROFILE(userID)
 

);

CREATE OR REPLACE TRIGGER NewFriend
	AFTER INSERT on FRIENDS
	REFERENCING NEW AS newFriend
	BEGIN	
		DELETE FROM PENDING_FRIENDS
		WHERE (fromID == :newFriend.userID1 and toID == :newFriend.userID2)
	END;
		
		
CREATE OR REPLACE TRIGGER NewGroupMembership
	AFTER INSERT on GROUP_MEMBERSHIP
	REFERENCING NEW AS newGroup
	BEGIN	
		DELETE FROM PENDING_GROUPMEMBERS
		WHERE (gID == :newGroup.gID and userID == :newGroup.userID)
	END;
		
