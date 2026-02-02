package com.infybuzz.springbatch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentJson implements Student {

	private Long id;
	@JsonProperty("first_name")
	private String firstName;
	private String lastName;
	private String email;

}
