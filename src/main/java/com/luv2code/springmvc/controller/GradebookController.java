package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.models.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

	@Autowired
	private Gradebook gradebook;

	@Autowired
	private StudentAndGradeService service;
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getStudents(Model m) {
		Iterable<CollegeStudent> collegeStudents = service.getGradebook();
		m.addAttribute("students", collegeStudents);
		return "index";
	}

	@GetMapping(value = "/delete/student/{id}")//PathVariable will add id
	public String deleteStudent(@PathVariable int id, Model m) {

		if (!service.checkIfStudentIsNull(id)) {
			return "error";
		}

		service.deleteStudent(id);

		//getting updated list of students
		Iterable<CollegeStudent> collegeStudents = service.getGradebook();
		m.addAttribute("students", collegeStudents);
		return "index";
	}

	@PostMapping(value = "/")
	public String createStudent(@ModelAttribute("student") CollegeStudent student, Model m) {
		service.createStudent(student.getFirstname(), student.getLastname(), student.getEmailAddress());

		Iterable<CollegeStudent> collegeStudents = service.getGradebook();
		m.addAttribute("students", collegeStudents);

		return "index";
	}

	@GetMapping("/studentInformation/{id}")
	public String studentInformation(@PathVariable int id, Model m) {
		return "studentInformation";
	}


}
