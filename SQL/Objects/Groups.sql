--delete group request when member is addded to group
--assume 
CREATE OR REPLACE TRIGGER newGroupMembership
	AFTER INSERT on GROUP_MEMBERSHIP
	BEGIN	
		DELETE FROM PENDING_GROUPMEMBERS
		WHERE (gID = NEW.gID and userID = NEW.userID)
	END newGroupMembership; 
/
	

	