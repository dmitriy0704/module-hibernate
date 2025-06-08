package dev.folomkin;

import dev.folomkin.entity.Course;
import dev.folomkin.entity.Group;
import dev.folomkin.entity.Student;
import dev.folomkin.services.CourseService;
import dev.folomkin.services.GroupService;
import dev.folomkin.services.ProfileService;
import dev.folomkin.services.StudentService;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;


public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("dev.folomkin");
        SessionFactory sessionFactory = context.getBean(SessionFactory.class);

        StudentService studentService = context.getBean(StudentService.class);
        ProfileService profileService = context.getBean(ProfileService.class);
        GroupService groupService = context.getBean(GroupService.class);
        CourseService courseService = context.getBean(CourseService.class);

//        Group group1 = groupService.saveGroup("1", 2024L);
//        Group group2 = groupService.saveGroup("2", 2024L);
//        Group group3 = groupService.saveGroup("3", 2024L);
//
//
        Course course1 = new Course("math-1", "math");
        Course course2 = new Course("math-2", "math");
        Course course3 = new Course("math-3", "math");

//        courseService.saveCourse(course1);
//        courseService.saveCourse(course2);
//        courseService.saveCourse(course3);

        courseService.enrollStudentToCourse(2L, 2L);
        courseService.enrollStudentToCourse(3L, 2L);

        Student student = studentService.getById(2L);
        System.out.println(student);
    }
}

