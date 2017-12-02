-- Takes first part of email, name, and DOB. Creates a new account
CREATE OR REPLACE FUNCTION addUser
  ("ID" IN VARCHAR2, name IN VARCHAR2, email IN VARCHAR2, birthday IN DATE)
  RETURN VARCHAR2
IS
DECLARE
  pw VARCHAR2(20) := dbms_random.string('A', 20);

  BEGIN
    INSERT INTO
      Profile (userID, "name", email, date_of_birth, "password")
    VALUES
      ("ID", name, email, birthday, pw);

    RETURN pw;

  END addUser;
/

-- For logins
CREATE OR REPLACE FUNCTION login
  ("ID" IN VARCHAR2, pw IN VARCHAR2(20))
  RETURN BOOLEAN
IS
DECLARE
  matches NUMBER;

  BEGIN
    SELECT COUNT(*)
    INTO matches
    FROM Profile
    WHERE userID = "ID" AND "password" = pw;

    RETURN (matches > 0);

  END login;
/

-- For logouts
CREATE OR REPLACE FUNCTION logout
  (pittID IN VARCHAR2)
  RETURN BOOLEAN
IS
DECLARE
  succ NUMBER;

  BEGIN

    UPDATE Profile
    SET lastlogin = current_timestamp
    WHERE userID = pittID;

    RETURN TRUE;

  END logout;
/

-- For when you want your own password
CREATE OR REPLACE PROCEDURE setPassword
  (pittID IN VARCHAR2, pw IN VARCHAR2(20)) AS

  BEGIN

    UPDATE Profile
    SET password = pw
    WHERE userID = pittID;

  END setPassword;
/