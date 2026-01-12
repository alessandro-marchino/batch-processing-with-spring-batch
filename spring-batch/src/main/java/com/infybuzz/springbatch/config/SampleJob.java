package com.infybuzz.springbatch.config;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.infybuzz.springbatch.service.FirstTasklet;
import com.infybuzz.springbatch.service.SecondTasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SampleJob {

	private final JobRepository jobRepository;
	private final FirstTasklet firstTasklet;
	private final SecondTasklet secondTasklet;

	@Bean
	Job fistJob() {
		return new JobBuilder("First job", jobRepository)
			.start(firstStep())
			.next(secondStep())
			.build();
	}

	private Step firstStep() {
		return new StepBuilder("First step", jobRepository)
			.tasklet(firstTasklet)
			.build();
	}

	private Step secondStep() {
		return new StepBuilder("Second step", jobRepository)
			.tasklet(secondTasklet)
			.build();
	}
}
