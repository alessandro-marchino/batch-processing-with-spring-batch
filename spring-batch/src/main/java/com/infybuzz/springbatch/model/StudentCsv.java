package com.infybuzz.springbatch.model;

import lombok.Data;

@Data
public class StudentCsv implements Student {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;

}
