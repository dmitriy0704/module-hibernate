package dev.folomkin;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("dev.folomkin");

        SessionFactory sessionFactory = context.getBean(SessionFactory.class);

        Session session = sessionFactory.openSession();

        Student student1 = new Student("Vasya", 22);

        session.beginTransaction();
        session.persist(student1);
        session.getTransaction().commit();




        session.close();
    }
}