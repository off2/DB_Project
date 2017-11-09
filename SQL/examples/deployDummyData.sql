SET SERVEROUTPUT ON
WHENEVER SQLERROR exit sqlcode;

DECLARE

  customer_id INTEGER;

BEGIN
  sample_create_customer(
      'Roy'
      , 'Gustafson'
      , 'Computer Science'
      , customer_id
  );
END;

/

EXIT