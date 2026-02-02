package com.infybuzz.springbatch.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.infybuzz.springbatch.model.StudentRest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

	private final RestClient restClient;

	public List<StudentRest> restCallToGetStudents() {
		return restClient.get()
			.uri("http://localhost:8081/api/v1/students")
			.retrieve()
			.body(new ParameterizedTypeReference<List<StudentRest>>() {});
	}
}
