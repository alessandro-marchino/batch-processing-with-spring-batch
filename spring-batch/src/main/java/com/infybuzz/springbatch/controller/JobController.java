package com.infybuzz.springbatch.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameter;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job")
@Slf4j
public class JobController {

	private final JobOperator jobOperator;
	private final JobRegistry jobRegistry;

	@GetMapping("/start/{jobName}")
	public String startJob(@PathVariable String jobName) throws Exception {
		Job job = jobRegistry.getJob(jobName);
		if(job == null) {
			throw new RuntimeException("Job " + jobName + " not found");
		}
		JobParameter<Long> currentTime = new JobParameter<>("currentTime", System.currentTimeMillis(), Long.class);
		JobParameters jobParameters = new JobParameters(Set.of(currentTime));
		jobOperator.run(job, jobParameters);
		log.info("Job for name \"{}\" is {}", jobName, job);
		return "Job Started...";
	}

}
