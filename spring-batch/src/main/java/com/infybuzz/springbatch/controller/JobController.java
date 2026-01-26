package com.infybuzz.springbatch.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infybuzz.springbatch.service.JobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job")
@Slf4j
public class JobController {

	private final JobService jobService;

	@GetMapping("/start/{jobName}")
	public String startJob(@PathVariable String jobName) {
		jobService.startJob(jobName);
		log.info("Job started...");
		return "Job Started...";
	}

}
