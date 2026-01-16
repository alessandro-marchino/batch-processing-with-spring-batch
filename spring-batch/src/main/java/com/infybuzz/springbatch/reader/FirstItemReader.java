package com.infybuzz.springbatch.reader;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FirstItemReader implements ItemReader<Integer> {

	private List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	private int index;

	@Override
	public @Nullable Integer read() throws Exception {
		log.info("Inside Item Reader");
		if(index < list.size()) {
			return list.get(index++);
		}
		index = 0;
		return null;
	}
}
