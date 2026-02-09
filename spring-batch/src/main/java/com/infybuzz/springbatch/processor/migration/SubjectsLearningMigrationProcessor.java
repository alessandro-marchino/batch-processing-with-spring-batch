package com.infybuzz.springbatch.processor.migration;

import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.infybuzz.springbatch.entity.mysql.MysqlSubjectsLearning;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

import org.jspecify.annotations.Nullable;

@Slf4j
public class SubjectsLearningMigrationProcessor implements ItemProcessor<com.infybuzz.springbatch.entity.postgresql.SubjectsLearning, com.infybuzz.springbatch.entity.mysql.MysqlSubjectsLearning> {
	private final AtomicInteger counter = new AtomicInteger();

	@Override
	public @Nullable MysqlSubjectsLearning process(com.infybuzz.springbatch.entity.postgresql.SubjectsLearning item) throws Exception {
		int counterValue = counter.incrementAndGet();

		if(counterValue % 1000 == 0) {
			log.info("{} - original: {}", counterValue, item);
		}
		return MysqlSubjectsLearning.builder()
			.id(item.getId())
			.subName(item.getSubName())
			.studentId(item.getStudentId())
			.marksObtained(item.getMarksObtained())
			.build();
	}

}
