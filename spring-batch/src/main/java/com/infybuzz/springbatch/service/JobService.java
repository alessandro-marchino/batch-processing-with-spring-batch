package com.infybuzz.springbatch.service;

import java.util.Set;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobExecutionException;
import org.springframework.batch.core.job.parameters.JobParameter;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {
	private final JobOperator jobOperator;
	private final JobRegistry jobRegistry;

	@Async
	public void startJob(String jobName) {
		Job job = jobRegistry.getJob(jobName);
		if(job == null) {
			log.error("Job {} not found", jobName);
		}
		JobParameter<Long> currentTime = new JobParameter<>("currentTime", System.currentTimeMillis(), Long.class);
		JobParameters jobParameters = new JobParameters(Set.of(currentTime));
		try {
			JobExecution jobExecution = jobOperator.run(job, jobParameters);
			log.info("Job for name \"{}\" is {} - execution id {} - instance id {}", jobName, job, jobExecution.getId(), jobExecution.getJobInstanceId());
		} catch (JobExecutionException e) {
			log.error("Exception while running job", e);
		}
	}
}
