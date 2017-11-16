import java.io.File;
import java.sql.SQLException;

public class FaceSpace {

    public static void main(String[] args) {
        Database FaceBase;

        try {
            FaceBase = new Database(
                    new File("SQL/CreationScript.sql"),
                    new File("../Data")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
