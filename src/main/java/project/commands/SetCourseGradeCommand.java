package project.commands;

import project.utils.Utils;

import java.util.*;

public class SetCourseGradeCommand extends Command {
    private static final byte GET_COURSE_GRADE_CLA = Utils.getByteFromHexCode("0x80");
    private static final byte GET_COURSE_GRADE_INS = Utils.getByteFromHexCode("0x52");
    private static final byte GET_COURSE_GRADE_P1 = Utils.getByteFromHexCode("0x00");
    private static final byte GET_COURSE_GRADE_P2 = Utils.getByteFromHexCode("0x00");
    private static final byte GET_COURSE_GRADE_LE = Utils.getByteFromHexCode("0x7F");

    public SetCourseGradeCommand() {
        setMessage("SET COURSE GRADE");
    }

    public void setGradeInfo(Integer courseId, Integer grade, Date date)
    {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(date);

        Integer day = cal.get(Calendar.DAY_OF_MONTH);
        Integer month = cal.get(Calendar.MONTH);
        Integer year = cal.get(Calendar.YEAR) % 100;

        List<Byte> newCommand = new LinkedList<>();

        newCommand.add(GET_COURSE_GRADE_CLA);
        newCommand.add(GET_COURSE_GRADE_INS);
        newCommand.add(GET_COURSE_GRADE_P1);
        newCommand.add(GET_COURSE_GRADE_P2);

        newCommand.add((byte) 5);
        for(Integer value: Arrays.asList(courseId,grade,day,month,year))
        {
            String hexValue = Utils.toHexString(value);
            newCommand.add(Utils.getByteFromHexCode(hexValue));
        }

        newCommand.add(GET_COURSE_GRADE_LE);

        setCommand(Utils.getByteArrayFromList(newCommand));
    }
}
