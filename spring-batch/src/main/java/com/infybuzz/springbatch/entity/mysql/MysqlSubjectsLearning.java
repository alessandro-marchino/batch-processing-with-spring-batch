package com.infybuzz.springbatch.entity.mysql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "subjects_learning")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MysqlSubjectsLearning {

	@Id
	private Long id;
	@Column(name = "sub_name")
	private String subName;
	@Column(name = "student_id")
	private Long studentId;
	@Column(name = "marks_obtained")
	private Long marksObtained;
}
