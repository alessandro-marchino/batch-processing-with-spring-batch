package com.infybuzz.springbatch.listener;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FirstStepListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		log.info("Before step: {}", stepExecution.getStepName());
		log.info("Before step: job {}", stepExecution.getJobExecution().getJobInstance().getJobName());
		log.info("Before step: StepExecutionContext {}", stepExecution.getExecutionContext());
		stepExecution.getExecutionContext().put("sec", "sec value");
		
	}
	@Override
	public @Nullable ExitStatus afterStep(StepExecution stepExecution) {
		log.info("After step: {}", stepExecution.getStepName());
		log.info("After step: job {}", stepExecution.getJobExecution().getJobInstance().getJobName());
		log.info("After step: StepExecutionContext {}", stepExecution.getExecutionContext());
		return null;
	}
}
