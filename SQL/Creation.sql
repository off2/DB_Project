DROP TABLE PROFILE CASCADE CONSTRAINTS;
DROP TABLE FRIENDS CASCADE CONSTRAINTS;
DROP TABLE PENDING_FRIENDS CASCADE CONSTRAINTS;
DROP TABLE MESSAGES CASCADE CONSTRAINTS;
DROP TABLE MESSAGE_RECIPIENT CASCADE CONSTRAINTS;
DROP TABLE GROUPS CASCADE CONSTRAINTS;
DROP TABLE GROUP_MEMBERSHIP CASCADE CONSTRAINTS;
DROP TABLE PENDING_GROUPMEMBERS CASCADE CONSTRAINTS;

CREATE TABLE PROFILE(
	userID			varchar2(20),
	name			varchar2(50),
	password		varchar2(50),
	date_of_birth	date,
	lastlogin		timestamp
	
);

CREATE TABLE FRIENDS(
	userID1			varchar2(20),
	userID2			varchar2(20),
	JDate			date,
	message			varchar2(200)
	
);

CREATE TABLE PENDING_FRIENDS(
	fromID			varchar2(20),
	toID			varchar2(20),
	message			varchar2(200)
	
);

CREATE TABLE MESSAGES(
	msgID			varchar2(20),
	fromID			varchar2(20),
	message			varchar2(200),
	toUserID		varchar2(20) default NULL,
	toGroupID		varchar2(20) default NULL,
	dateSent		date

);

CREATE TABLE MESSAGE_RECIPIENT(
	msgID			varchar2(20),
	userID			varchar2(20)

);

CREATE TABLE GROUPS(
	gID				varchar2(20),
	name			varchar2(50),
	description		varchar2(200)

);

CREATE TABLE GROUP_MEMBERSHIP(
	gID				varchar2(20),
	userID			varchar2(20),
	role			varchar2(20)

);

CREATE TABLE PENDING_GROUPMEMBERS(
	gID				varchar2(20),
	userID			varchar2(20),
	message			varchar2(200)

);
