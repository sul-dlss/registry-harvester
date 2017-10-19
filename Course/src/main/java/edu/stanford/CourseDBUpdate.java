package edu.stanford;

import java.sql.*;
import java.util.Calendar;

class CourseDBUpdate {

    static void updateCourse(String course_id, String xml) {

        String sql = "update courses set xml = ?, date_changed = ? where course_class_id = ?";

        Calendar calendar = Calendar.getInstance();
        java.sql.Date today = new java.sql.Date(calendar.getTime().getTime());

        PreparedStatement ps;


        try {
            Clob clob = CourseDBService.dbConnection.createClob();
            clob.setString(1, xml);

            ps = CourseDBService.dbConnection.prepareStatement(sql);
            ps.setClob(1, clob);
            ps.setDate(2, today);
            ps.setString(3, course_id);

            ps.execute();

        } catch(SQLException e) {
            System.err.println("updateCourse SQLException:" + e.getMessage());
        }
    }
}
