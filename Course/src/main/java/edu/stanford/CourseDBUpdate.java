package edu.stanford;

import java.sql.*;

class CourseDBUpdate {

    static void updateCourse(String course_id, String xml) {

        String sql = "update courses set xml = ? where course_class_id = ?";

        PreparedStatement ps;


        try {
            Clob clob = CourseDBService.dbConnection.createClob();
            clob.setString(1, xml);

            ps = CourseDBService.dbConnection.prepareStatement(sql);
            ps.setClob(1, clob);
            ps.setString(2, course_id);

            ps.execute();

        } catch(SQLException e) {
            System.err.println("updateCourse SQLException:" + e.getMessage());
        }
    }
}
