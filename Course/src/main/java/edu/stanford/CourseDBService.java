package edu.stanford;

import oracle.jdbc.pool.OracleDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Stanford University Libraries, DLSS
 */
class CourseDBService {

    static Connection dbConnection;

    static void openConnection() throws IOException, SQLException {
        if (dbConnection == null) {
            dbConnection = CourseDBService.open();
        }
    }

    static void closeConnection() throws SQLException {
        if (dbConnection != null) {
            dbConnection.close();
            dbConnection = null;
        }
    }

    static Connection open() throws SQLException, IOException {
        return dataSource().getConnection();
    }

    static DataSource dataSource() throws SQLException, IOException {

        String file = CourseDBService.class.getResource("/server.conf").getFile();
        FileInputStream fileInput = new FileInputStream(file);
        Properties props = new Properties();
        props.load(fileInput);

        String server = props.getProperty("SERVER");
        String service = props.getProperty("SERVICE");
        String userName = props.getProperty("USER");
        String userPass = props.getProperty("PASS");

        OracleDataSource ds = new OracleDataSource();

        ds.setURL("jdbc:oracle:thin:@" + server + ":1521:" + service);
        ds.setUser(userName);
        ds.setPassword(userPass);

        return ds;
    }
}