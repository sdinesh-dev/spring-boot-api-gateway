package com.dinesh.microservices.api_gateway.controller;

import com.dinesh.microservices.api_gateway.model.Company;
import com.dinesh.microservices.api_gateway.model.Student;
import com.dinesh.microservices.api_gateway.model.Type;
import com.dinesh.microservices.api_gateway.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/process")
public class TypeController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AuthUtil authUtil;

    @PostMapping
    public String getType(@RequestBody Type type, ServerHttpRequest inRequest) {
        System.out.println("getting type");
        System.out.println("types:" + type.getTypes());
        type.getTypes().forEach(f -> {
            if (f.equals("Student")) {
                HttpEntity<Student> request = new HttpEntity<>(
                        new Student(1, "Test", "Student"),
                        setAuthHeader(inRequest.getHeaders().get("userName").toString(), inRequest.getHeaders().get("role").toString())
                );
                restTemplate.exchange("http://localhost:8080/first", HttpMethod.POST, request, String.class);
            }
            if (f.equals("Company")) {
                HttpEntity<Company> request = new HttpEntity<>(
                        new Company(1, "Test", "Company"),
                        setAuthHeader(inRequest.getHeaders().get("userName").toString(), inRequest.getHeaders().get("role").toString())
                );
                restTemplate.exchange("http://localhost:8080/second", HttpMethod.POST, request, String.class);
            }
        });
        return "done";
    }

    private HttpHeaders setAuthHeader(String username, String role) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authUtil.getToken(username, role));
        return headers;
    }
}
