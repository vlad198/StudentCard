package project.database;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class DBInfo {
    private int studentId;
    private int courseId;
    private int grade;
    private Date date;
    private boolean thePaymentHasBeenMade;
}
