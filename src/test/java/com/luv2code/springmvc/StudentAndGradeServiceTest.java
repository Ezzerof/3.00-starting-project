package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.models.service.StudentAndGradeService;
import com.luv2code.springmvc.repository.HistoryGradeDAO;
import com.luv2code.springmvc.repository.MathGradeDAO;
import com.luv2code.springmvc.repository.ScienceGradeDAO;
import com.luv2code.springmvc.repository.StudentDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;
    @Autowired
    private StudentDAO studentDAO;
    @Autowired
    private MathGradeDAO mathGradeDAO;
    @Autowired
    private ScienceGradeDAO scienceGradeDAO;
    @Autowired
    private HistoryGradeDAO historyGradeDAO;
    @Autowired
    private JdbcTemplate jdbc;
    private CollegeStudent student;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address) VALUES(1, 'Dan', 'Somehow', 'dan@somehow@gmail.com')");
        jdbc.execute("INSERT INTO math_grade(id, student_id, grade) values (1,1,100.00)");
        jdbc.execute("INSERT INTO science_grade(id, student_id, grade) values (1,1,100.00)");
        jdbc.execute("INSERT INTO history_grade(id, student_id, grade) values (1,1,100.00)");
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute("DELETE FROM student");
        jdbc.execute("DELETE FROM math_grade");
        jdbc.execute("DELETE FROM science_grade");
        jdbc.execute("DELETE FROM history_grade");
    }


    @Test
    @DisplayName("Create new Student")
    void createNewStudent() {
        studentService.createStudent("Dan", "Loffman", "dan.loff@gmail.com");
        CollegeStudent student = studentDAO.findByEmailAddress("dan.loff@gmail.com");

        assertEquals("dan.loff@gmail.com", student.getEmailAddress());
    }

    @Test
    @DisplayName("Check if student is null")
    void checkIfStudentIsNull() {
        assertTrue(studentService.checkIfStudentIsNull(1));
    }

    @Test
    @DisplayName("Delete student from database")
    void deleteStudentFromDatabase() {
        Optional<CollegeStudent> deleteCollegeStudent = studentDAO.findById(1);
        Optional<MathGrade> deleteMathGrade = mathGradeDAO.findById(1);
        Optional<ScienceGrade> deleteScienceGrade = scienceGradeDAO.findById(1);
        Optional<HistoryGrade> deleteHistoryGrade = historyGradeDAO.findById(1);


        assertTrue(deleteCollegeStudent.isPresent(), "Return true");
        assertTrue(deleteMathGrade.isPresent());
        assertTrue(deleteScienceGrade.isPresent());
        assertTrue(deleteHistoryGrade.isPresent());

        studentService.deleteStudent(1);

        deleteCollegeStudent = studentDAO.findById(1);
        deleteMathGrade = mathGradeDAO.findById(1);
        deleteScienceGrade = scienceGradeDAO.findById(1);
        deleteHistoryGrade = historyGradeDAO.findById(1);

        assertFalse(deleteCollegeStudent.isPresent(), "Return false");
        assertFalse(deleteMathGrade.isPresent(), "Return false");
        assertFalse(deleteScienceGrade.isPresent(), "Return false");
        assertFalse(deleteHistoryGrade.isPresent(), "Return false");
    }

    @Sql("/insertData.sql")
    @Test
    @DisplayName("Get Gradebook list of students")
    void getGradebookListOfStudents() {
        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradebook();
        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for (CollegeStudent collegeStudent : iterableCollegeStudents) {
            collegeStudents.add(collegeStudent);
        }

        assertEquals(5, collegeStudents.size());
    }

    @Test
    @DisplayName("Modify student")
    void modifyStudent() {
        //given
        //1, 'Dan', 'Somehow', 'dan@somehow@gmail.com'

        //when
        studentService.updateStudentFirstname(1, "Ethan");

        //then
        assertTrue(studentDAO.findById(1).isPresent());
        assertEquals("Ethan", studentDAO.findById(1).get().getFirstname());
    }

    @Test
    @DisplayName("Create grade service")
    void createGradeService() {

        assertTrue(studentService.createGrade(80.0, 1, "math"));
        assertTrue(studentService.createGrade(80.0, 1, "science"));
        assertTrue(studentService.createGrade(80.0, 1, "history"));

        Iterable<MathGrade> mathGrades = mathGradeDAO.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDAO.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDAO.findGradeByStudentId(1);

        assertTrue(((Collection<MathGrade>) mathGrades).size() == 2);
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2);
        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2);
        assertTrue(mathGrades.iterator().hasNext());
        assertTrue(scienceGrades.iterator().hasNext());
        assertTrue(historyGrades.iterator().hasNext());
    }

    @Test
    @DisplayName("Create Grade service Return False")
    void createGradeServiceReturnFalse() {
        assertFalse(studentService.createGrade(180.0, 1, "math"));
        assertFalse(studentService.createGrade(-80.0, 1, "math"));
        assertFalse(studentService.createGrade(200.0, 2, "math"));
        assertFalse(studentService.createGrade(10.0, 1, "programming"));
    }

    @Test
    @DisplayName("Deleting grade service")
    void deletingGradeService() {
        assertEquals(1, studentService.deleteGrade(1, "math"));
        assertEquals(1, studentService.deleteGrade(1, "science"));
        assertEquals(1, studentService.deleteGrade(1, "history"));
    }
    
    @Test
    @DisplayName("Delete grade service when student is null")
    void deleteGradeServiceWhenStudentIsNull() {
        assertEquals(0,studentService.deleteGrade(0, "math"));
        assertEquals(0,studentService.deleteGrade(1, "literature"));
    }

    @Test
    @DisplayName("Student information")
    void studentInformation() {
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(1);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Dan", gradebookCollegeStudent.getFirstname());
        assertEquals("Somehow", gradebookCollegeStudent.getLastname());
        assertEquals("dan@somehow@gmail.com", gradebookCollegeStudent.getEmailAddress());
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() == 1);
    }

    @Test
    @DisplayName("Student information service return null")
    void studentInformationServiceReturnNull() {
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(0);
        assertNull(gradebookCollegeStudent);
    }

}
