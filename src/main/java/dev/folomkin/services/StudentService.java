package dev.folomkin.services;

import dev.folomkin.entity.Student;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class StudentService {

    private final SessionFactory sessionFactory;

    public StudentService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public Student saveStudent(Student student) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.persist(student);
        session.getTransaction().commit();
        session.close();
        return student;
    }

    public void deleteStudent(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Student studentForDelete = session.get(Student.class, id);
        session.remove(studentForDelete);
        session.close();
    }

    public Student getById(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Student student = session.get(Student.class, id);
        session.close();
        return student;
    }

    public List<Student> findAll(Student student) {
        Session session = sessionFactory.openSession();
        List<Student> allStudent = session
                .createQuery("select s from Student s", Student.class).list();
        session.close();
        return allStudent;
    }

    public Student update(Student student) {

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        student = session.merge(student);
        session.close();
        return student;
    }

}
