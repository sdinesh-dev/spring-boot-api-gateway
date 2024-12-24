package com.dinesh.microservices.second_microservice.controller;

import com.dinesh.microservices.second_microservice.model.Company;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/second")
public class SecondController {
    @GetMapping
    public List<Company> getCompanies() {
        System.out.println("getting Companies");
        return Arrays.asList(new Company(1,"TechM","company"), new Company(2,"TCS","company"));
    }

    @PostMapping
    public Boolean createCompany(@RequestBody Company company) {
        System.out.println("creating Company:"+company);
        return true;
    }
}
