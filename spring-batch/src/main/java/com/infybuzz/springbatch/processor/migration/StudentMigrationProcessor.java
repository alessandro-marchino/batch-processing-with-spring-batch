package com.infybuzz.springbatch.processor.migration;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.infybuzz.springbatch.entity.mysql.Student;

public class StudentMigrationProcessor implements ItemProcessor<com.infybuzz.springbatch.entity.postgresql.Student, com.infybuzz.springbatch.entity.mysql.Student> {

	@Override
	public @Nullable Student process(com.infybuzz.springbatch.entity.postgresql.Student item) throws Exception {
		return Student.builder()
			.id(item.getId())
			.firstName(item.getFirstName())
			.lastName(item.getLastName())
			.email(item.getEmail())
			.deptId(item.getDeptId())
			.isActive(Boolean.valueOf(item.getIsActive()))
			.build();
	}

}
