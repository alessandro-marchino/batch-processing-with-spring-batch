package com.infybuzz.springbatch.writer;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SecondItemWriter implements ItemWriter<Integer> {

	@Override
	public void write(Chunk<? extends Integer> chunk) throws Exception {
		log.info("Inside Item Writer");
		chunk.forEach(value -> log.info("Value: {}", value));
	}

}
