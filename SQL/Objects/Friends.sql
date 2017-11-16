-- Confirm friendship

-- Display friends

--integrity triggers

--delete friend request when new friend is added 
CREATE OR REPLACE TRIGGER newFriend
AFTER INSERT ON FRIENDS
  BEGIN
    DELETE FROM PENDING_FRIENDS
    WHERE (fromID = NEW.userID1 AND toID = NEW.userID2);
  END newFriend;
 
/

