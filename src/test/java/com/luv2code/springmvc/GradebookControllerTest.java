package com.luv2code.springmvc;
import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.repository.StudentDAO;
import org.junit.jupiter.api.*;
import com.luv2code.springmvc.models.service.StudentAndGradeService;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
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
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;

    @BeforeAll
    public static void setup() {// in beforeAll method and var must be static
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Chris");
        request.setParameter("lastname", "Darby");
        request.setParameter("emailAddress", "chris@gmail.com");
    }

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute("INSERT INTO student(id, firstname, lastname, email_address) VALUES(1, 'Dan', 'Somehow', 'dan@somehow@gmail.com')");
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute("DELETE FROM student");
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
    void creatingStudentByUsingHttpRequest() throws Exception{
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
    @DisplayName("deleteStudentHttpRequestErrorPage")
    void deleteStudentHttpRequestErrorPage() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}", 0)).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

}
