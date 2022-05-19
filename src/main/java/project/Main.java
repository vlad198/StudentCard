package project;

import project.commands.*;
import project.database.DBInfo;
import project.database.DBUtils;
import project.logic.UpdateDBJC;
import project.pojo.CourseInfoAPDUResponse;
import project.smartCard.APDUUtils;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<DBInfo> db = new LinkedList<>();

        CreateWalletAppletCommand createWalletAppletCommand = new CreateWalletAppletCommand();
        createWalletAppletCommand.createApplet();

        SelectWalletCommand selectWalletCommand = new SelectWalletCommand();
        selectWalletCommand.selectWallet();

        APDUUtils apduUtils = new APDUUtils();

        UpdateDBJC updateDBJC = new UpdateDBJC(apduUtils, db);

        apduUtils.powerUp();

        apduUtils.runCapWallet();

        createWalletAppletCommand.runCommand(apduUtils);
        selectWalletCommand.runCommand(apduUtils);

        // PART 1

        System.out.println("---------> PART 1 <---------");

        int studentId = updateDBJC.getStudentIdFromApplet(Arrays.asList(1, 2, 3, 4, 5));

        updateDBJC.setMarkApplet(studentId, 1, 3, new Date());

        // flow 1
//        updateDBJC.setMarkApplet(studentId,1, 7, new Date());
//
//        // flow 2
//        DBUtils.setTaxHasBeenPaid(db,studentId,1);
//
//        updateDBJC.setMarkApplet(studentId,1, 10, new Date());

        // PART 2

        System.out.println("---------> PART 2 <---------");

        printList(updateDBJC.getGradesFromApplet());
        printList(db);
//
//        db.get(0).setGrade(5);
//        db.get(0).setDate(new Date());
//
        // flow 3
        db.add(
                DBInfo.
                        builder()
                        .studentId(2)
                        .courseId(2)
                        .grade(8)
                        .date(new Date())
                        .build()
        );

        updateDBJC.updateCourseGradesApplet(studentId);

        printList(updateDBJC.getGradesFromApplet());
        printList(db);
////
//        System.out.println(db);
//        System.out.println(updateDBJC.getGradesFromApplet());
//
//        updateDBJC.updateCourseGradesApplet(studentId);
//
//        System.out.println(updateDBJC.getGradesFromApplet());

        apduUtils.powerDown();
    }

    public static void printList(List<?> list)
    {
        System.out.println("------> LIST <-----");
        for(Object ob: list)
            System.out.println(ob);
    }
}
