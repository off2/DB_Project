DECLARE

  num_invalid_objects NUMBER := 0;
    invalid_objects_exception EXCEPTION;
  CURSOR cur_invalid_objects IS
    SELECT *
    FROM
      all_objects
    WHERE
      object_type IN ('PROCEDURE', 'FUNCTION', 'PACKAGE', 'TRIGGER', 'VIEW', 'TYPE')
      AND
      status <> 'VALID'
      AND (
        OWNER IN (
          SELECT user
          FROM dual -- The current user
        )
      )
    ORDER BY
      object_type DESC, owner, object_name;

BEGIN

  dbms_output.put_line('validate_all_objects.sql');

  FOR current_record IN cur_invalid_objects LOOP
    dbms_output.put_line(current_record.object_name || ' is INVALID');
    num_invalid_objects := num_invalid_objects + 1;
  END LOOP;

  IF num_invalid_objects > 0
  THEN
    RAISE invalid_objects_exception;
  END IF;

END;

/