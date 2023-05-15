package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.service.StudentAndGradeService;
import com.luv2code.springmvc.repository.HistoryGradeDAO;
import com.luv2code.springmvc.repository.MathGradeDAO;
import com.luv2code.springmvc.repository.ScienceGradeDAO;
import com.luv2code.springmvc.repository.StudentDAO;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private StudentDAO studentDAO;
    @Autowired
    private MathGradeDAO mathGradeDAO;
    @Autowired
    private ScienceGradeDAO scienceGradeDAO;
    @Autowired
    private HistoryGradeDAO historyGradeDAO;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;
    @Value("${sql.scripts.create.student}")
    private String sqlCreateStudent;
    @Value("${sql.scripts.create.math.grade}")
    private String sqlCreateMathGrade;
    @Value("${sql.scripts.create.science.grade}")
    private String sqlCreateScienceGrade;
    @Value("${sql.scripts.create.history.grade}")
    private String sqlCreateHistoryGrade;
    @Value("${sql.scripts.delete.history.grade}")
    private String sqlDeleteHistoryGrade;
    @Value("${sql.scripts.delete.science.grade}")
    private String sqlDeleteScienceGrade;
    @Value("${sql.scripts.delete.math.grade}")
    private String sqlDeleteMathGrade;
    @Value("${sql.scripts.delete.student}")
    private String sqlDeleteStudent;

    @BeforeAll
    public static void setup() {// in beforeAll method and var must be static
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Chris");
        request.setParameter("lastname", "Darby");
        request.setParameter("emailAddress", "chris@gmail.com");
    }

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlCreateStudent);
        jdbc.execute(sqlCreateMathGrade);
        jdbc.execute(sqlCreateScienceGrade);
        jdbc.execute(sqlCreateHistoryGrade);
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
        jdbc.execute(sqlDeleteScienceGrade);
    }

    @Test
    @DisplayName("GetStudentsHttpRequest")
    void getStudentsHttpRequest() throws Exception {
        CollegeStudent student1 = new GradebookCollegeStudent("Eric", "Rob", "rob@gmail.com");
        CollegeStudent student2 = new GradebookCollegeStudent("John", "Darby", "dar@gmail.com");

        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(student1, student2));

        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();  // getting the HTTP response

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index"); // just compare names
    }

    @Test
    @DisplayName("Creating Student by using Http request")
    void creatingStudentByUsingHttpRequest() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstname", request.getParameterValues("firstname"))
                        .param("lastname", request.getParameterValues("lastname"))
                        .param("emailAddress", request.getParameterValues("emailAddress")))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");
        CollegeStudent verifyStudent = studentDAO.findByEmailAddress("chris@gmail.com");
        assertNotNull(verifyStudent);
    }

    @Test
    @DisplayName("Delete Student by Http request")
    void deleteStudentByHttpRequest() throws Exception {
        assertTrue(studentDAO.findById(1).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}", 1)).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");

        assertFalse(studentDAO.findById(1).isPresent());
    }

    @Test
    @DisplayName("delete Student Http Request Error Page")
    void deleteStudentHttpRequestErrorPage() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}", 0)).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @DisplayName("Student Information HTTP Request")
    void studentInformationHttpRequest() throws Exception {
        assertTrue(studentDAO.findById(1).isPresent());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 1))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");
    }

    @Test
    @DisplayName("Student Information HTTP Request of Not existing student")
    void studentInformationHttpRequestOfNotExistingStudent() throws Exception {
        assertFalse(studentDAO.findById(0).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", 0))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @DisplayName("Create valid grade Http Request")
    public void createValidGradeHttpRequest() throws Exception {

        assertTrue(studentDAO.findById(1).isPresent());

        GradebookCollegeStudent student = studentAndGradeService.studentInformation(1);

        assertEquals(1, student.getStudentGrades().getMathGradeResults().size());

        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade", "85.00")
                .param("gradeType", "math")
                .param("studentId", "1")).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        student = studentAndGradeService.studentInformation(1);

        assertEquals(2, student.getStudentGrades().getMathGradeResults().size());
    }

    @Test
    @DisplayName("Create valid grade http request for an invalid student")
    void createValidGradeHttpRequestForAnInvalidStudent() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade", "85.00")
                .param("gradeType", "history")
                .param("studentId", "0")).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @DisplayName("Create invalid grade http request for a valid student")
    void createInvalidGradeHttpRequestForAValidStudent() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade", "99.00")
                .param("gradeType", "biology")
                .param("studentId", "1")).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @DisplayName("Delete a valid grade Http request")
    void deleteAValidGradeHttpRequest() throws Exception {

        Optional<MathGrade> mathGrade = mathGradeDAO.findById(1);

        assertTrue(mathGrade.isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 1, "math"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        mathGrade = mathGradeDAO.findById(1);
        assertFalse(mathGrade.isPresent());
    }

    @Test
    @DisplayName("Delete a invalid grade Http request")
    void deleteAInvalidGradeHttpRequest() throws Exception {
        Optional<MathGrade> mathGrade = mathGradeDAO.findById(2);

        assertFalse(mathGrade.isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 2, "math"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @DisplayName("Delete an invalid grade HTTP request")
    void deleteAnInvalidGradeHttpRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 1, "biology"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

}
