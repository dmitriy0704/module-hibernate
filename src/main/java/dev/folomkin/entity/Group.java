package dev.folomkin.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "student_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private String number;

    @Column(name = "grad_year")
    private Long graduationYear;

    @OneToMany(mappedBy = "group")
    private List<Student> studentList;

    public Group() {
    }

    public Group(
            String number,
            Long graduationYear
    ) {
        this.number = number;
        this.graduationYear = graduationYear;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Long graduationYear) {
        this.graduationYear = graduationYear;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
