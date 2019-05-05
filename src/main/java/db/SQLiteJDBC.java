package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLiteJDBC {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteJDBC.class);

    private Connection connection;

    private Connection getMemoryConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException classNotFoundException) {
            LOGGER.error("Error finding JDBC driver: {}", classNotFoundException.getMessage());
        } catch (SQLException sqlException) {
            LOGGER.error("Error retrieving DB connection (memory): {}", sqlException.getMessage());
        }
        return connection;
    }

    public Connection getConnection() {
        if(null == connection) {
            return getMemoryConnection();
        }
        return connection;
    }

    public void memoryRestore() {
        try {
            Statement statement = getConnection().createStatement();
            String sql = "CREATE TABLE accounts (" +
                            "_id integer primary key autoincrement, " +
                            "numberAcc text, " +
                            "balance integer);\n" +
                         "CREATE TABLE transfers (" +
                            "_id integer primary key autoincrement, " +
                            "fromAccId text, " +
                            "toAccId text, " +
                            "amount integer, " +
                            "transferstatus text, " +
                            "transferDate text);\n" +
                    "INSERT INTO accounts(numberAcc, balance) VALUES ('555', 100);\n" +
                    "INSERT INTO accounts(numberAcc, balance) VALUES ('7777', 130);\n" +
                    "INSERT INTO accounts(numberAcc, balance) VALUES ('42', 1000);";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException sqlException) {
            LOGGER.error("Error restoring DB: {}", sqlException.getMessage());
        }
    }

}
