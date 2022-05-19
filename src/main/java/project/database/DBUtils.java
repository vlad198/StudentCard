package project.database;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DBUtils {
    public static List<DBInfo> getStudentInfo(List<DBInfo> DB, short studentId) {
        return DB.stream()
                .filter(dbInfo -> dbInfo.getStudentId() == studentId)
                .collect(Collectors.toList());
    }

    public static void setTaxHasBeenPaid(List<DBInfo> DB, int studentId, int courseId) {
        if (getNumberOfValidExaminations(DB, studentId, courseId) > 0) {
            DBInfo student = getLatestGrade(DB, studentId, courseId);
            student.setThePaymentHasBeenMade(true);
        }
    }

    public static long getNumberOfValidExaminations(List<DBInfo> DB, int studentId, int courseId) {
        return DB.stream()
                .filter(dbInfo -> dbInfo.getStudentId() == studentId && dbInfo.getCourseId() == courseId && dbInfo.getGrade() != 11)
                .count();
    }

    public static boolean isPaymentMade(List<DBInfo> DB, int studentId, int courseId) {
        return DB.stream()
                .filter(dbInfo -> dbInfo.getStudentId() == studentId && dbInfo.getCourseId() == courseId && dbInfo.isThePaymentHasBeenMade())
                .count() > 0;
    }

    public static DBInfo getLatestGrade(List<DBInfo> DB, int studentId, int courseId) {
        return DB.stream()
                .filter(dbInfo -> dbInfo.getStudentId() == studentId && dbInfo.getCourseId() == courseId)
                .max(Comparator.comparing(DBInfo::getDate))
                .orElse(null);
    }
}
