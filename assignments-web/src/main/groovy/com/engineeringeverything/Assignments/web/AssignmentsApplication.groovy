package com.engineeringeverything.Assignments.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@ComponentScan(basePackages = ['com.engineeringeverything.Assignments.core.Repositories','com.engineeringeverything.Assignments.core.Service','com.engineeringeverything.Assignments.web'],basePackageClasses =[])
@EnableMongoRepositories(['com.engineeringeverything.Assignments.core.Repositories'])
@RestController
@SpringBootApplication
class AssignmentsApplication {

	@RequestMapping(value = "/available")
	public String available() {
		return "Spring in Action";
	}

	@RequestMapping(value = "/checked-out")
	public String checkedOut() {
		return "Spring Boot in Action";
	}

	static void main(String[] args) {
		SpringApplication.run(AssignmentsApplication.class, args);
	}
}
