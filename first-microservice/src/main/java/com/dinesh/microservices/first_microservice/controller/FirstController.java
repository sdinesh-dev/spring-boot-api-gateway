package com.dinesh.microservices.first_microservice.controller;

import com.dinesh.microservices.first_microservice.model.Student;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/first")
public class FirstController {

    @GetMapping
    public List<Student> getStudent(){
        System.out.println("Getting Students");
        return Arrays.asList(new Student(1, "Ross","Student"), new Student(2,"Rachel","Student"));
    }

    @PostMapping
    public Boolean createStudent(@RequestBody Student student){
        System.out.println("Creating Student"+student);
        return true;
    }
}
