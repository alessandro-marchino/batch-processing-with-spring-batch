package com.infybuzz.springbatch.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import com.infybuzz.springbatch.model.Student;
import com.infybuzz.springbatch.model.StudentRest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class StudentService {

	private List<StudentRest> list;

	public List<StudentRest> restCallToGetStudents() {
		list = RestClient.builder().build()
			.get()
			.uri("http://localhost:8081/api/v1/students")
			.retrieve()
			.body(new ParameterizedTypeReference<List<StudentRest>>() {});
		return list;
	}

	public StudentRest getStudent(long id, String name) {
		log.info("Id: {}, name: {}", id, name);
		if(list == null) {
			restCallToGetStudents();
		}
		if(list != null && !list.isEmpty()) {
			return list.remove(0);
		}
		return null;
	}

	public StudentRest restCallToCreateStudent(Student student) {
		StudentRest studentRest = RestClient.builder().build()
			.post()
			.uri("http://localhost:8081/api/v1/students")
			.body(StudentRest.builder()
				.id(student.getId())
				.firstName(student.getFirstName())
				.lastName(student.getLastName())
				.email(student.getEmail())
				.build())
			.retrieve()
			.body(StudentRest.class);
		return studentRest;
	}
}
