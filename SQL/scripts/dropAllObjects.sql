DECLARE
  CURSOR cur_drop_statements IS
    SELECT 'drop ' || object_type || ' ' || owner || '.' || object_name || ' '
      AS
      drop_statement
    FROM
      all_objects
    WHERE
      object_type IN ('PROCEDURE', 'FUNCTION', 'PACKAGE', 'TRIGGER', 'VIEW', 'TYPE')
      AND (
        OWNER IN (
          SELECT user
          FROM dual
        )
      )
    ORDER BY
      object_type DESC, owner, object_name;
BEGIN

  dbms_output.put_line('drop_all_objects.sql');

  FOR current_record IN cur_drop_statements LOOP
    dbms_output.put_line(current_record.drop_statement);
    EXECUTE IMMEDIATE current_record.drop_statement;
  END LOOP;
END;

/