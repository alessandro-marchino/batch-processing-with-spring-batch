package com.infybuzz.springbatch.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@XmlRootElement(name = "student")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentXml implements Student {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;

}
