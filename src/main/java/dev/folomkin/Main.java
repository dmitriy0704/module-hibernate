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
        Student student2 = new Student("Pasha", 20);

        // ============================================================= //
        // -> Сохранение
        session.beginTransaction();
        session.persist(student1);
        session.persist(student2);
        session.getTransaction().commit();

        // ============================================================= //
        // -> Поиск
        // -> Возвращается из кеша
        Student studentById1 = session.get(Student.class, 1L);
        System.out.println("Student 1: " + studentById1.toString());

        // ============================================================= //
        // -> JPQL
        // -> Запрос из базы
        Student studentById2 = session.createQuery(
                        "SELECT s FROM Student s WHERE s.id = :id", Student.class
                )
                .setParameter("id", 2L)
                .getSingleResult();
        System.out.println("Student 2: " + studentById2.toString());


        // ============================================================= //
        // -> Обновление
        session.beginTransaction();
        Student studentForUpdate = session.get(Student.class, 1L);
        studentForUpdate.setAge(30);
        studentForUpdate.setName("Dima");
        session.getTransaction().commit();


        // ============================================================= //
        // -> Удаление
        session.beginTransaction();
        Student studentForDelete = session.get(Student.class, 2L);
        session.remove(studentForDelete);
        session.getTransaction().commit();

        session.createQuery("DELETE FROM Student").executeUpdate();

        // https://www.youtube.com/watch?v=c7TIkimWk4g&t=1821s&ab_channel=%D0%9F%D0%B0%D0%B2%D0%B5%D0%BB%D0%A1%D0%BE%D1%80%D0%BE%D0%BA%D0%B8%D0%BD

        session.close();
    }
}