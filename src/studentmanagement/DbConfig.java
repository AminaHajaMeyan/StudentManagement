package studentmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConfig {
    private static DbConfig instance = null;

    private final String URL = "jdbc:mysql://localhost:3306/studentManagement"; 
    private final String USER = "root";
    private final String PASSWORD = "Amina@1234";

    private DbConfig() {
        // Private constructor to prevent instantiation
    }

    public static DbConfig getInstance() {
        if (instance == null) {
            instance = new DbConfig();
        }
        return instance;
    }

    public Connection dbConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

