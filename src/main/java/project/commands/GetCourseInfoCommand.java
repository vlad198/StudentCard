package project.commands;

import project.utils.Utils;

import java.util.LinkedList;
import java.util.List;

public class GetCourseInfoCommand extends Command {
    private static final byte GET_COURSE_INFO_CLA = Utils.getByteFromHexCode("0x80");
    private static final byte GET_COURSE_INFO_INS = Utils.getByteFromHexCode("0x53");
    private static final byte GET_COURSE_INFO_P1 = Utils.getByteFromHexCode("0x00");
    private static final byte GET_COURSE_INFO_P2 = Utils.getByteFromHexCode("0x00");
    private static final byte GET_COURSE_INFO_LE = Utils.getByteFromHexCode("0x7F");

    public GetCourseInfoCommand() {
        setMessage("GET COURSE INFO");
    }

    public void setCourseId(Integer courseId) {
        if (!(courseId >= 0 && courseId <= 128)) {
            System.out.println("Invalid course ID");
            return;
        }

        List<Byte> newCommand = new LinkedList<>();

        newCommand.add(GET_COURSE_INFO_CLA);
        newCommand.add(GET_COURSE_INFO_INS);
        newCommand.add(GET_COURSE_INFO_P1);
        newCommand.add(GET_COURSE_INFO_P2);

        newCommand.add((byte) 1);

        String hexValue = Utils.toHexString(courseId);
        newCommand.add(Utils.getByteFromHexCode(hexValue));

        newCommand.add(GET_COURSE_INFO_LE);

        setCommand(Utils.getByteArrayFromList(newCommand));
    }
}
