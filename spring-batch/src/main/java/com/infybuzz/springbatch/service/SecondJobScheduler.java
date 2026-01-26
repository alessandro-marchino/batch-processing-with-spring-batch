package com.infybuzz.springbatch.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobExecutionException;
import org.springframework.batch.core.job.parameters.JobParameter;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecondJobScheduler {

	private final Job secondJob;
	private final JobOperator jobOperator;

	@Scheduled(cron = "0 0 * * * *")
	public void secondJobStarter() {
		Set<JobParameter<?>> parameters = new HashSet<>();
		parameters.add(new JobParameter<>("currentTime", System.currentTimeMillis(), Long.class));
		JobParameters jobParameters = new JobParameters(parameters);
		try {
			JobExecution jobExecution = jobOperator.run(secondJob, jobParameters);
			log.info("Job execution id: {}", jobExecution.getId());
		} catch(JobExecutionException e) {
			log.error("Exception while running job", e);
		}
	}
}
