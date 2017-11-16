import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class Database {

    private final Connection conn;

    Database(File buildScript, File csvDirectory)
            throws SQLException {

        this.conn = connect();

        assert (conn != null);
        assert (buildScript.exists());
        assert (csvDirectory.exists());

        execute(buildScript);
        loader(csvDirectory);
    }

    public Connection getConn() {
        return conn;
    }

    /**
     * Generates a new connection to the database
     * @return connection
     */
    private static Connection connect() {
        try {
            Class.forName(
                    "oracle.jdbc.driver.OracleDriver"
            );
            return DriverManager.getConnection(
                    "jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass"
            );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Uses schema to create new database
     *
     * @throws SQLException on failed commit/get stmt
     */
    public void execute(File toExecute)
            throws SQLException {

        String line;
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader in = new BufferedReader(new FileReader(toExecute));

            while ((line = in.readLine()) != null)
                sb.append(line).append('\n');

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.conn.createStatement().execute(sb.toString());
    }

    /**
     * Automatically loads directory of csv's into their appropriate tables
     * Table name is chosen based on filename
     * Column names are parsed from first line of csv
     *
     * @throws SQLException on failure
     */
    private void loader(File dir)
            throws SQLException {

        File[] directoryListing = dir.listFiles();
        assert directoryListing != null;
        for (File child : directoryListing) load(child);
    }

    /**
     * Loads lines of csv file into appropriate table on connection
     *
     * @param csv values to insert
     */
    private void load(File csv)
            throws SQLException {

        this.conn.setAutoCommit(false);

        String tableName = csv.getName();
        tableName = tableName.substring(0, tableName.lastIndexOf('.'));

        BufferedReader in = null;
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        try {
            in = new BufferedReader(new FileReader(csv));

            /* Format table names and value slots for PreparedStatement */
            String before = "";
            for (String column : in.readLine().split(",")) {
                columns.append(before).append(column);
                values.append(before).append("?");
                before = ", ";
            }

            /* Create PreparedStatement */
            PreparedStatement stmt = conn.prepareStatement(
                    String.format("INSERT (%s) INTO %s VALUES (%s)",
                            columns.toString(),
                            tableName,
                            values.toString()
                    )
            );

            /* Using PreparedStatement, load values then execute statement */
            String line;
            String[] dataValues;

            while ((line = in.readLine()) != null) {
                dataValues = line.split(",");
                for (int i = 1; i <= dataValues.length; i++) {
                    stmt.setString(i, dataValues[i - 1]);
                }
                stmt.execute();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

            /* Commit all */
        conn.commit();
        conn.setAutoCommit(true);
    }

}
