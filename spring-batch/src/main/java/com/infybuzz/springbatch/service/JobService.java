package com.infybuzz.springbatch.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobExecutionException;
import org.springframework.batch.core.job.parameters.JobParameter;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.infybuzz.springbatch.request.JobParamRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {
	private final JobOperator jobOperator;
	private final JobRegistry jobRegistry;
	private final JobRepository jobRepository;

	@Async
	public void startJob(String jobName, List<JobParamRequest> jobParamRequests) {
		Job job = jobRegistry.getJob(jobName);
		if(job == null) {
			log.error("Job {} not found", jobName);
		}
		Set<JobParameter<?>> parameters = new HashSet<>();
		parameters.add(new JobParameter<>("currentTime", System.currentTimeMillis(), Long.class));
		jobParamRequests.forEach(jpr -> parameters.add(new JobParameter<>(jpr.getParamKey(), jpr.getParamValue(), String.class)));

		JobParameters jobParameters = new JobParameters(parameters);
		try {
			JobExecution jobExecution = jobOperator.run(job, jobParameters);
			log.info("Job for name \"{}\" is {} - execution id {} - instance id {}", jobName, job, jobExecution.getId(), jobExecution.getJobInstanceId());
		} catch (JobExecutionException e) {
			log.error("Exception while running job", e);
		}
	}

	public JobExecution getJobExecutionById(long jobExecutionId) {
		return jobRepository.getJobExecution(jobExecutionId);
	}
}
