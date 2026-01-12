package com.infybuzz.springbatch.listener;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FirstJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("Before job: {}", jobExecution.getJobInstance().getJobName());
		log.info("Job params: {}", jobExecution.getJobParameters());
		log.info("Job execution context: {}", jobExecution.getExecutionContext());
		jobExecution.getExecutionContext().put("jec", "jec value");
	}
	@Override
	public void afterJob(JobExecution jobExecution) {
		log.info("After job: {}", jobExecution.getJobInstance().getJobName());
		log.info("Job params: {}", jobExecution.getJobParameters());
		log.info("Job execution context: {}", jobExecution.getExecutionContext());
	}
}
