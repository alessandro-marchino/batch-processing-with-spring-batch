package com.infybuzz.springbatch.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import com.infybuzz.springbatch.model.StudentRest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
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

	public StudentRest getStudent() {
		if(list == null) {
			restCallToGetStudents();
		}
		if(list != null && !list.isEmpty()) {
			return list.remove(0);
		}
		return null;
	}
}
