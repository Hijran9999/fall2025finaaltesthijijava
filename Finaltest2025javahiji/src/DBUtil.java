import java.sql.*;

public class DBUtil {
    // TODO: change these to your DB details
    private static final String URL = "jdbc:mysql://localhost:3306/EmploymentApp?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Gunaydin1992";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
