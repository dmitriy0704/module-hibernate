package dev.folomkin.services;

import dev.folomkin.Student;


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



    }

    public void deleteStudent(Student student) {

    }

    public Student getById(Long id) {
        return null;
    }

    public List<Student> findAll(Student student) {
        return null;
    }

    public Student update(Student student) {
        return null;
    }

}
