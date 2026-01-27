package com.infybuzz.springbatch.controller;

import com.infybuzz.springbatch.request.JobParamRequest;
import com.infybuzz.springbatch.service.JobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job")
@Slf4j
public class JobController {

	private final JobService jobService;
	private final JobOperator jobOperator;

	@PostMapping("/start/{jobName}")
	public String startJob(@PathVariable String jobName, @RequestBody List<JobParamRequest> body) {
		jobService.startJob(jobName, body);
		log.info("Job started...");
		return "Job Started...";
	}

	@PatchMapping("/stop/{executionId}")
	public String stopJob(@PathVariable long executionId) {
		JobExecution jobExecution  = jobService.getJobExecutionById(executionId);
		if(jobExecution == null) {
			return "Job not found";
		}
		try {
			jobOperator.stop(jobExecution);
		} catch (JobExecutionNotRunningException e) {
			log.error("Error in Job stopping", e);
		}
		log.info("Job stopped...");
		return "Job Stopped...";
	}

}
