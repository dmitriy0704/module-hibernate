package dev.folomkin.services;

import dev.folomkin.config.TransactionHelper;
import dev.folomkin.entity.Student;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StudentService {

    private final SessionFactory sessionFactory;
    private final TransactionHelper transactionHelper;


    public StudentService(SessionFactory sessionFactory,
                          TransactionHelper transactionHelper) {
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
    }

    public Student saveStudent(Student student) {
        return transactionHelper.executeInTransaction(session -> {
            session.persist(student);
            return student;
        });
    }

    public void deleteStudent(Long id) {
        transactionHelper.executeInTransaction(session -> {
            Student studentForDelete = session.get(Student.class, id);
            session.remove(studentForDelete);
        });
    }

    public Student getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Student.class, id);
        }
    }

    public List<Student> findAll(Student student) {
        try (Session session = sessionFactory.openSession()) {
            return session
                    .createQuery("select s from Student s", Student.class).list();
        }
    }

    public Student update(Student student) {
        return transactionHelper.executeInTransaction(session -> {
            return session.merge(student);
        });
    }
}
