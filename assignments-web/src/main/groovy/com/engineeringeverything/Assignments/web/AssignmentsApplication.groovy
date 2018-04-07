package com.engineeringeverything.Assignments.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

import javax.annotation.PostConstruct

@ComponentScan(basePackages = ['com.engineeringeverything.Assignments.core.Repositories','com.engineeringeverything.Assignments.core.Service','com.engineeringeverything.Assignments.web'],basePackageClasses =[])
@EnableMongoRepositories(['com.engineeringeverything.Assignments.core.Repositories'])
@SpringBootApplication
class AssignmentsApplication {


	@PostConstruct
	public void init(){
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));   // It will set IST timezone
		System.out.println("Spring boot application running in UTC timezone :"+new Date());   // It will print IST timezone
	}

	static void main(String[] args) {
		SpringApplication.run(AssignmentsApplication.class, args);
	}
}
