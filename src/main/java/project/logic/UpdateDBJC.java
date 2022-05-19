package project.logic;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import project.commands.GetCourseInfoCommand;
import project.commands.SetCourseGradeCommand;
import project.commands.VerifyPinCommand;
import project.database.DBInfo;
import project.database.DBUtils;
import project.pojo.CourseInfoAPDUResponse;
import project.smartCard.APDUUtils;

import java.util.*;

@AllArgsConstructor
public class UpdateDBJC {
    private APDUUtils apduUtils;
    private List<DBInfo> DB;

    public Integer getStudentIdFromApplet(List<Integer> pin) {
        VerifyPinCommand verifyPinCommand = new VerifyPinCommand();
        verifyPinCommand.setPin(pin);

        byte[] result = verifyPinCommand.runCommand(apduUtils);

        if (result != null) {
            System.out.println("[GET STUDENT ID] All good!");
            return (int) result[0];
        } else {
            System.out.println("[GET STUDENT ID] Error!");
            return -1;
        }
    }

    public List<CourseInfoAPDUResponse> getGradesFromApplet() {
        List<CourseInfoAPDUResponse> responses = new LinkedList<>();

        GetCourseInfoCommand getCourseInfoCommand = new GetCourseInfoCommand();

        for (Integer courseId : Arrays.asList(1, 2, 3, 4, 5)) {
            getCourseInfoCommand.setCourseId(courseId);

            byte[] result = getCourseInfoCommand.runCommand(apduUtils);

            if (result != null) {

                responses.add(
                        new CourseInfoAPDUResponse(courseId, (int) result[0], (int) result[1], (int) result[2], (int) result[3])
                );
            }
        }

        if (responses.size() != 5) {
            System.out.println("[GET ALL COURSES] Something went wrong!");
        } else {
            System.out.println("[GET ALL COURSES] All good!");
        }

        return responses;
    }

    public void setMarkApplet(Integer studentId, Integer courseId, Integer grade, Date date) {
        SetCourseGradeCommand setCourseGradeCommand = new SetCourseGradeCommand();

        long numberOfValidExaminations = DBUtils.getNumberOfValidExaminations(DB, studentId, courseId);
        DBInfo student = DBUtils.getLatestGrade(DB, studentId, courseId);

        if (student == null) {
            DB.add(new DBInfo(studentId, courseId, grade, new Date(), false));

            setCourseGradeCommand.setGradeInfo(courseId, grade, date);
            byte[] result = setCourseGradeCommand.runCommand(apduUtils);

            if (result != null) {
                System.out.println("[SET MARK] All good!");
            } else {
                System.out.println("[SET MARK] Error!");
            }
        } else if (numberOfValidExaminations < 3) {
            boolean payment_is_made = DBUtils.isPaymentMade(DB, studentId, courseId);

            if (numberOfValidExaminations < 2) {
                // all good
            } else {
                if (!payment_is_made) {
                    grade = 11;
                }
            }

            DB.add(new DBInfo(studentId, courseId, grade, new Date(), payment_is_made));

            setCourseGradeCommand.setGradeInfo(courseId, grade, date);
            byte[] result = setCourseGradeCommand.runCommand(apduUtils);

            if (result != null) {
                System.out.println("[SET MARK] All good!");
            } else {
                System.out.println("[SET MARK] Error!");
            }
        }
    }

    public void updateCourseGradesApplet(int studentId) {
        List<CourseInfoAPDUResponse> coursesInfo = this.getGradesFromApplet();

        for (CourseInfoAPDUResponse courseInfo : coursesInfo) {
            //data from db
            DBInfo dbInfo = DBUtils.getLatestGrade(DB, studentId, courseInfo.getCourseId());

            if (dbInfo != null) {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
                cal.setTime(dbInfo.getDate());

                Integer day = cal.get(Calendar.DAY_OF_MONTH);
                Integer month = cal.get(Calendar.MONTH);
                Integer year = cal.get(Calendar.YEAR) % 100;

                if (courseInfo.getDay() != day ||
                        courseInfo.getMonth() != month ||
                        courseInfo.getYear() != year ||
                        courseInfo.getGrade() != dbInfo.getGrade()) {

                    SetCourseGradeCommand setCourseGradeCommand = new SetCourseGradeCommand();
                    setCourseGradeCommand.setGradeInfo(courseInfo.getCourseId(), dbInfo.getGrade(), dbInfo.getDate());

                    byte[] result = setCourseGradeCommand.runCommand(apduUtils);
                    if (result != null) {
                        System.out.println("[UPDATE MARK] All good!");
                    } else {
                        System.out.println("[UPDATE MARK] Error!");
                    }
                }
            }
        }

    }
}
