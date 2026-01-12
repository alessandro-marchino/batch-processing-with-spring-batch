package com.infybuzz.springbatch.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.infybuzz.springbatch.listener.FirstJobListener;
import com.infybuzz.springbatch.listener.FirstStepListener;
import com.infybuzz.springbatch.service.FirstTasklet;
import com.infybuzz.springbatch.service.SecondTasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SampleJob {

	private final JobRepository jobRepository;
	private final FirstJobListener firstJobListener;
	private final FirstStepListener firstStepListener;
	private final FirstTasklet firstTasklet;
	private final SecondTasklet secondTasklet;

	@Bean
	Job fistJob() {
		return new JobBuilder("First job", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(firstStep())
			.next(secondStep())
			.listener(firstJobListener)
			.build();
	}

	private Step firstStep() {
		return new StepBuilder("First step", jobRepository)
			.tasklet(firstTasklet)
			.listener(firstStepListener)
			.build();
	}

	private Step secondStep() {
		return new StepBuilder("Second step", jobRepository)
			.tasklet(secondTasklet)
			.build();
	}
}
