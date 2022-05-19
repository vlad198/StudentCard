package project.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder
@Getter
@AllArgsConstructor
public class CourseInfoAPDUResponse {
    private int courseId;
    private int grade;
    private int day;
    private int month;
    private int year;
}
