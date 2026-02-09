package com.infybuzz.springbatch.processor.migration;

import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.infybuzz.springbatch.entity.mysql.SubjectsLearning;

import org.jspecify.annotations.Nullable;

public class SubjectsLearningMigrationProcessor implements ItemProcessor<com.infybuzz.springbatch.entity.postgresql.SubjectsLearning, com.infybuzz.springbatch.entity.mysql.SubjectsLearning> {

	@Override
	public @Nullable SubjectsLearning process(com.infybuzz.springbatch.entity.postgresql.SubjectsLearning item) throws Exception {
		return SubjectsLearning.builder()
			.id(item.getId())
			.subName(item.getSubName())
			.studentId(item.getStudentId())
			.marksObtained(item.getMarksObtained())
			.build();
	}

}
