package dev.folomkin;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;


public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("dev.folomkin");

        SessionFactory sessionFactory = context.getBean(SessionFactory.class);
        Session session = sessionFactory.openSession();


        Student student1 = new Student("Vasya", 22);
        Student student2 = new Student("Pasha", 20);

        // ========== -> Сохранение ========================================= //
        session.beginTransaction();
        session.persist(student1);
        session.persist(student2);
        session.getTransaction().commit();

        // ====== // -> Поиск -> Возвращается из кеша ======================= //
        Student studentById1 = session.get(Student.class, 1L);
        System.out.println("Student 1: " + studentById1.toString());

        // -> JPQL: Запрос из базы
        Student studentById2 = session
                .createQuery("SELECT s FROM Student s WHERE s.id = :id", Student.class)
                .setParameter("id", 2L).getSingleResult();
        System.out.println("Student 2: " + studentById2.toString());

        // ================ // -> Обновление ================================= //
        session.beginTransaction();
        Student studentForUpdate = session.get(Student.class, 1L);
        studentForUpdate.setAge(30);
        studentForUpdate.setName("Dima");
        session.getTransaction().commit();

        // ============ // -> Удаление ============ //
//        session.beginTransaction();

//        Student studentForDelete = session.get(Student.class, 2L);
//        session.remove(studentForDelete);

//        session.createQuery("DELETE FROM Student s where s.id = 1").executeUpdate();
//        session
//                 .createNativeQuery("delete from students s where s.id = 2")
//                .executeUpdate();
//        session.getTransaction().commit();

        List<Student> allStudent = session
                .createQuery("select s from Student s", Student.class).list();

        Student studentByName = session
                .createQuery("SELECT s FROM Student s WHERE s.name = :name", Student.class)
                .setParameter("name", "Pasha")
                .getSingleResult();
        System.out.println("Student name: " + studentByName.toString());

        session.beginTransaction();
        Student student3 = new Student("Pasha", 20);
        session.persist(student3);
        session.getTransaction().commit();

        session.close();
    }
}