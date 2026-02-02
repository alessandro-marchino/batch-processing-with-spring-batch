package com.infybuzz.springbatch.model;

import lombok.Data;

@Data
public class StudentRest implements Student {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
}
