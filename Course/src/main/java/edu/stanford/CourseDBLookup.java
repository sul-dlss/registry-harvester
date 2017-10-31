package edu.stanford;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class CourseDBLookup {

    static String lookupCourse(String course_id) {
        String sql = "select course_class_id from courses where course_class_id = '" + course_id + "'";
        return queryCourse(sql);
    }

    static String lookupCourseXML(String termCode) {
        String sql = "select xml from courses where course_class_id like '" + termCode + "%'";
        return queryCourse(sql);
    }

    private static String queryCourse(String sql) {
        StringBuilder result = new StringBuilder();

        try {
            Statement s = CourseDBService.dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                result.append(rs.getString(1).trim());
            }
            rs.close();
            s.close();
        } catch(SQLException e) {
            System.err.println("CourseDBLookup SQLException:" + e.getMessage());
        }
        return result.toString();
    }
}
