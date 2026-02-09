package com.infybuzz.springbatch.processor.migration;

import java.util.concurrent.atomic.AtomicInteger;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.infybuzz.springbatch.entity.mysql.MysqlStudent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StudentMigrationProcessor implements ItemProcessor<com.infybuzz.springbatch.entity.postgresql.Student, com.infybuzz.springbatch.entity.mysql.MysqlStudent> {

	private final AtomicInteger counter = new AtomicInteger();

	@Override
	public @Nullable MysqlStudent process(com.infybuzz.springbatch.entity.postgresql.Student item) throws Exception {
		int counterValue = counter.incrementAndGet();

		if(counterValue % 1000 == 0) {
			log.info("{} - original: {}", counterValue, item);
		}

		return MysqlStudent.builder()
			.id(item.getId())
			.firstName(item.getFirstName())
			.lastName(item.getLastName())
			.email(item.getEmail())
			.deptId(item.getDeptId())
			.isActive(Boolean.valueOf(item.getIsActive()))
			.build();
	}

}
