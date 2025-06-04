package dev.folomkin;

import dev.folomkin.entity.Profile;
import dev.folomkin.entity.Student;
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

        Student student1 = new Student("Vasya", 22);
        Student student2 = new Student("Pasha", 20);

        studentService.saveStudent(student1);
        studentService.saveStudent(student2);


        // ================================================= //
        // -> Сохранение сущности
        Profile profile1 = new Profile("My bio", LocalDateTime.now(), student1);

        // -> Создание сессии
        Session session = sessionFactory.openSession();
            // -> Открытие транзакции
            session.beginTransaction();

                // -> сохранение сущности
                session.persist(profile1);

            // -> Закрытие транзакции
            session.getTransaction().commit();
        // -> Закрытие сессии
        session.close();


        // ================================================= //
        // -> Поиск сущности

        // -> Создание сессии
        session = sessionFactory.openSession();

                // -> поиск
                profile1 = session.get(Profile.class, 1L);
                student1 = session.get(Student.class, 1L);

                session.beginTransaction();

                session.remove(student1);
                session.remove(profile1);

                session.getTransaction().commit();

        // -> Закрытие сессии
        session.close();
    }
}