package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.CollegeStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDAO extends CrudRepository<CollegeStudent, Integer> {
    //CrudRepository will create CRUD methods for free

    CollegeStudent findByEmailAddress(String email);
}
