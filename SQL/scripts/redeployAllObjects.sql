-- Write to the console
SET SERVEROUTPUT ON

-- Exit if error
WHENEVER SQLERROR EXIT sqlcode;

@scripts/dropAllObjects
@scripts/createAllObjects
@scripts/validateAllObjects

EXIT