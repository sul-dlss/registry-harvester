package edu.stanford;

import java.sql.*;
import java.util.Calendar;

class CourseDBInsert {
    static void insertCourse(String course_id, String xml) {

        String sql = "insert into courses (course_class_id, xml, date_changed) values (?, ?, ?)";

        Calendar calendar = Calendar.getInstance();
        java.sql.Date today = new java.sql.Date(calendar.getTime().getTime());

        PreparedStatement ps;

        try {
            Clob clob = CourseDBService.dbConnection.createClob();
            clob.setString(1, xml);

            ps = CourseDBService.dbConnection.prepareStatement(sql);
            ps.setString(1, course_id);
            ps.setClob(2, clob);
            ps.setDate(3, today);

            ps.execute();

        } catch(SQLException e) {
            System.err.println("insertCourse SQLException:" + e.getMessage());
        }
    }
}
