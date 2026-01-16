package com.infybuzz.springbatch.writer;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FirstItemWriter implements ItemWriter<Long> {

	@Override
	public void write(Chunk<? extends Long> chunk) throws Exception {
		log.info("Inside Item Writer");
		chunk.forEach(value -> log.info("Value: {}", value));
	}

}
