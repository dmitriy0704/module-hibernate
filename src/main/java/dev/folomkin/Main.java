package dev.folomkin;

import dev.folomkin.entity.Profile;
import dev.folomkin.entity.Student;
import dev.folomkin.services.ProfileService;
import dev.folomkin.services.StudentService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("dev.folomkin");
        SessionFactory sessionFactory = context.getBean(SessionFactory.class);

        StudentService studentService = context.getBean(StudentService.class);
        ProfileService profileService = context.getBean(ProfileService.class);

        Student student1 = new Student("Vasya", 22);
        Student student2 = new Student("Pasha", 20);

        studentService.saveStudent(student1);
        studentService.saveStudent(student2);


        // ================================================= //
        // -> Сохранение сущности
        Profile profile1 = new Profile("My bio", LocalDateTime.now(), student1);
        profileService.saveProfile(profile1);

    }
}