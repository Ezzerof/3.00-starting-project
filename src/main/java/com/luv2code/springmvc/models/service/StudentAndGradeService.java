package com.luv2code.springmvc.models.service;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDAO;
import com.luv2code.springmvc.repository.MathGradeDAO;
import com.luv2code.springmvc.repository.ScienceGradeDAO;
import com.luv2code.springmvc.repository.StudentDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDAO studentDAO;
    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;
    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;
    @Autowired
    private MathGradeDAO mathGradeDAO;
    @Autowired
    private ScienceGradeDAO scienceGradeDAO;
    @Autowired
    private HistoryGradeDAO historyGradeDAO;
    @Autowired
    private StudentGrades studentGrades;

    public void createStudent(String firstname, String lastname, String emailAddress) {
        CollegeStudent student = new CollegeStudent(firstname, lastname, emailAddress);
        student.setId(0);
        studentDAO.save(student);
    }

    public boolean checkIfStudentIsNull(int id) {
        Optional<CollegeStudent> student = studentDAO.findById(id);
        if (student.isPresent()) {
            return true;
        }
        return false;
    }

    public void deleteStudent(int id) {
        if (checkIfStudentIsNull(id)) {
            studentDAO.deleteById(id);
            mathGradeDAO.deleteByStudentId(id);
            scienceGradeDAO.deleteByStudentId(id);
            historyGradeDAO.deleteByStudentId(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook() {
        Iterable<CollegeStudent> collegeStudents = studentDAO.findAll();
        return collegeStudents;
    }

    public void updateStudentFirstname(int id, String firstname) {
        Optional<CollegeStudent> student = studentDAO.findById(id);
        if (student.isPresent()) {
            CollegeStudent temp = student.get();
            temp.setFirstname(firstname);
            studentDAO.save(temp);
        }
    }

    public boolean createGrade(double grade, int studentId, String object) {
        if (!checkIfStudentIsNull(studentId))
            return false;

        if (grade >= 0 && grade <= 100) {
            if ("math".equals(object)) {
                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(studentId);
                mathGradeDAO.save(mathGrade);
                return true;
            } else if ("science".equals(object)) {
                scienceGrade.setId(0);
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(studentId);
                scienceGradeDAO.save(scienceGrade);
                return true;
            } else if ("history".equals(object)) {
                historyGrade.setId(0);
                historyGrade.setGrade(grade);
                historyGrade.setStudentId(studentId);
                historyGradeDAO.save(historyGrade);
                return true;
            }
        }

        return false;
    }

    public int deleteGrade(int studentId, String courseName) {
        int id = 0;
        if (!checkIfStudentIsNull(studentId))
            return id;

        if ("math".equals(courseName)) {
            Optional<MathGrade> grade = mathGradeDAO.findById(studentId);
            if (!grade.isPresent())
                return id;

            id = grade.get().getStudentId();
            mathGradeDAO.deleteById(id);
        } else if ("science".equals(courseName)) {
            Optional<ScienceGrade> grade = scienceGradeDAO.findById(studentId);
            if (!grade.isPresent())
                return id;

            id = grade.get().getStudentId();
            scienceGradeDAO.deleteById(id);
        } else if ("history".equals(courseName)) {
            Optional<HistoryGrade> grade = historyGradeDAO.findById(studentId);
            if (!grade.isPresent())
                return id;

            id = grade.get().getStudentId();
            historyGradeDAO.deleteById(id);
        }


        return id;
    }

    public GradebookCollegeStudent studentInformation(int id) {
        if (!checkIfStudentIsNull(id))
            return null;

        Optional<CollegeStudent> student = studentDAO.findById(id);
        Iterable<MathGrade> mathGrades = mathGradeDAO.findGradeByStudentId(id);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDAO.findGradeByStudentId(id);
        Iterable<HistoryGrade> historyGrades = historyGradeDAO.findGradeByStudentId(id);

        List<Grade> mathGradeList = new ArrayList<>();
        mathGrades.forEach(mathGradeList::add);

        List<Grade> scienceGradeList = new ArrayList<>();
        scienceGrades.forEach(scienceGradeList::add);

        List<Grade> historyGradeList = new ArrayList<>();
        historyGrades.forEach(historyGradeList::add);

        studentGrades.setMathGradeResults(mathGradeList);
        studentGrades.setHistoryGradeResults(historyGradeList);
        studentGrades.setScienceGradeResults(scienceGradeList);

        GradebookCollegeStudent gradebookCollegeStudent = new GradebookCollegeStudent(student.get().getId(), student.get().getFirstname(), student.get().getLastname(), student.get().getEmailAddress(), studentGrades);

        return gradebookCollegeStudent;
    }

}
