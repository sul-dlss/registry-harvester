package edu.stanford;

import oracle.jdbc.pool.OracleDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Stanford University Libraries, DLSS
 */
class CourseDBService {

    static Connection dbConnection;

    static void openConnection() throws IOException, SQLException, URISyntaxException {
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

    private static Connection open() throws SQLException, IOException, URISyntaxException {
        return dataSource().getConnection();
    }

    private static DataSource dataSource() throws SQLException, IOException, URISyntaxException {

        Properties props = getProps();

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

    private static Properties getProps() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("Course/src/main/resources/server.conf"));
        return props;
    }
}