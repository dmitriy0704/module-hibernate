package dev.folomkin;

import dev.folomkin.entity.Group;
import dev.folomkin.entity.Student;
import dev.folomkin.services.GroupService;
import dev.folomkin.services.ProfileService;
import dev.folomkin.services.StudentService;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("dev.folomkin");
        SessionFactory sessionFactory = context.getBean(SessionFactory.class);

        StudentService studentService = context.getBean(StudentService.class);
        ProfileService profileService = context.getBean(ProfileService.class);
        GroupService groupService = context.getBean(GroupService.class);

        Group group1 = groupService.saveGroup("1", 2024L);
        Group group2 = groupService.saveGroup("2", 2024L);
        Group group3 = groupService.saveGroup("3", 2024L);

        Student student1 = new Student("Vasya", 22, group1);
        Student student2 = new Student("Pasha", 20, group1);

        studentService.saveStudent(student1);
        studentService.saveStudent(student2);

        var session = sessionFactory.openSession();

        group1 = session.get(Group.class, 1L);

        session.close();

    }
}