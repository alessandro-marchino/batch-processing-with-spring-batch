package com.infybuzz.springbatch.processor;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FirstItemProcessor implements ItemProcessor<Integer, Long> {

	@Override
	public @Nullable Long process(Integer item) throws Exception {
		log.info("Inside Item Processor");
		return Long.valueOf(item.longValue() + 20L);
	}

}
