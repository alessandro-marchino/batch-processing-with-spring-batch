package com.infybuzz.springbatch.listener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import com.infybuzz.springbatch.model.Student;
import com.infybuzz.springbatch.model.StudentCsv;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomSkipListener implements SkipListener<StudentCsv, Student> {

	@Override
	public void onSkipInRead(Throwable t) {
		if(t instanceof FlatFileParseException e) {
			createFile("output-files/chunk_job/chunk_job_first_step/reader/skip-in-read.txt", e.getInput());
		}
	}
	@Override
	public void onSkipInProcess(StudentCsv item, Throwable t) {
		// TODO Auto-generated method stub
		SkipListener.super.onSkipInProcess(item, t);
	}
	@Override
	public void onSkipInWrite(Student item, Throwable t) {
		// TODO Auto-generated method stub
		SkipListener.super.onSkipInWrite(item, t);
	}

	public void createFile(String filePath, String data) {
		Path path = Path.of(filePath);
		try {
			Files.writeString(path, data + "\n", StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		} catch (IOException e) {
			log.warn("IOException in file write: {}", e.getMessage(), e);
		}
	}
}
