package com.infybuzz.springbatch.processor.migration;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import com.infybuzz.springbatch.entity.mysql.MysqlDepartment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DepartmentMigrationProcessor implements ItemProcessor<com.infybuzz.springbatch.entity.postgresql.Department, com.infybuzz.springbatch.entity.mysql.MysqlDepartment> {
	@Override
	public @Nullable MysqlDepartment process(com.infybuzz.springbatch.entity.postgresql.Department item) throws Exception {
		log.info("Original: {}", item);
		return MysqlDepartment.builder()
			.id(item.getId())
			.deptName(item.getDeptName())
			.build();
	}

}
