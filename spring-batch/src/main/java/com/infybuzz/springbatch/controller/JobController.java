package com.infybuzz.springbatch.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infybuzz.springbatch.request.JobParamRequest;
import com.infybuzz.springbatch.service.JobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job")
@Slf4j
public class JobController {

	private final JobService jobService;

	@PostMapping("/start/{jobName}")
	public String startJob(@PathVariable String jobName, @RequestBody List<JobParamRequest> body) {
		jobService.startJob(jobName, body);
		log.info("Job started...");
		return "Job Started...";
	}

}
