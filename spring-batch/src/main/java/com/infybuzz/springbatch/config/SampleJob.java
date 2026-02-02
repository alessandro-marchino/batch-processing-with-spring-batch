package com.infybuzz.springbatch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.adapter.ItemReaderAdapter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.json.JacksonJsonObjectReader;
import org.springframework.batch.infrastructure.item.json.JsonItemReader;
import org.springframework.batch.infrastructure.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.infrastructure.item.xml.StaxEventItemReader;
import org.springframework.batch.infrastructure.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.infybuzz.springbatch.listener.FirstJobListener;
import com.infybuzz.springbatch.listener.FirstStepListener;
import com.infybuzz.springbatch.model.Student;
import com.infybuzz.springbatch.model.StudentCsv;
import com.infybuzz.springbatch.model.StudentJdbc;
import com.infybuzz.springbatch.model.StudentJson;
import com.infybuzz.springbatch.model.StudentRest;
import com.infybuzz.springbatch.model.StudentXml;
import com.infybuzz.springbatch.processor.FirstItemProcessor;
import com.infybuzz.springbatch.reader.FirstItemReader;
import com.infybuzz.springbatch.service.FirstTasklet;
import com.infybuzz.springbatch.service.SecondTasklet;
import com.infybuzz.springbatch.service.StudentService;
import com.infybuzz.springbatch.writer.FirstItemWriter;
import com.infybuzz.springbatch.writer.SecondItemWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SampleJob {

	private final JobRepository jobRepository;

	// First job
	private final FirstJobListener firstJobListener;
	private final FirstStepListener firstStepListener;
	private final FirstTasklet firstTasklet;
	private final SecondTasklet secondTasklet;

	// Second job
	private final FirstItemReader firstItemReader;
	private final FirstItemProcessor firstItemProcessor;
	private final FirstItemWriter firstItemWriter;
	private final SecondItemWriter secondItemWriter;

	@Bean
	JobRegistry jobRegistry() throws Exception {
		return new MapJobRegistry();
	}

	@Bean
	Job firstJob() {
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

	@Bean
	Job secondJob() {
		return new JobBuilder("Second job", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(firstChunkStep())
			.next(secondChunkStep())
			.next(secondStep())
			.build();
	}

	private Step firstChunkStep() {
		return new StepBuilder("First chunk step", jobRepository)
			.<Integer, Long>chunk(3)
			.reader(firstItemReader)
			.processor(firstItemProcessor)
			.writer(firstItemWriter)
			.build();
	}

	private Step secondChunkStep() {
		return new StepBuilder("Second chunk step", jobRepository)
			.<Integer, Integer>chunk(3)
			.reader(firstItemReader)
			.writer(secondItemWriter)
			.build();
	}

	@Bean
	Job thirdJob() {
		return new JobBuilder("Third job", jobRepository)
			.start(new StepBuilder("First step", jobRepository)
				.tasklet((contribution, context) -> {
					// Sleep 10 seconds
					Thread.sleep(10000);
					return null;
				})
				.build())
			.build();
	}

	@Bean
	Job chunkJob() {
		return new JobBuilder("Chunk job", jobRepository)
			.start(chunkJobFirstStep())
			.build();
	}

	private Step chunkJobFirstStep() {
		return new StepBuilder("Chunk job first step", jobRepository)
			// Readers
			.<StudentCsv, Student>chunk(3)
			.reader(studentCsvFlatFileItemReader(null))
			// .<StudentJson, Student>chunk(3)
			// .reader(studentJsonItemReader(null))
			// .<StudentXml, Student>chunk(3)
			// .reader(studentXmlItemReader(null))
			// .<StudentJdbc, Student>chunk(3)
			// .reader(studentDbItemReader(null))
			// .<StudentRest, Student>chunk(3)
			// .reader(studentRestItemReader())
			// Writers
			.writer(chunk -> {
				System.out.println("Chunk writing...");
				chunk.forEach(System.out::println);
			})
			.build();
	}

	@Bean
	@StepScope
	FlatFileItemReader<StudentCsv> studentCsvFlatFileItemReader(@Value("#{jobParameters['inputFile']}") Resource resource) {
		return new FlatFileItemReaderBuilder<StudentCsv>()
			.saveState(false)
			.resource(resource)
			.delimited(spec -> spec
				.delimiter("|")
				.names("ID", "First Name", "Last Name", "Email"))
			.targetType(StudentCsv.class)
			.linesToSkip(1)
			.build();
	}

	@Bean
	@StepScope
	JsonItemReader<StudentJson> studentJsonItemReader(@Value("#{jobParameters['inputFile']}") Resource resource) {
		return new JsonItemReaderBuilder<StudentJson>()
			.saveState(false)
			.resource(resource)
			.jsonObjectReader(new JacksonJsonObjectReader<>(StudentJson.class))
			.build();
	}

	@Bean
	@StepScope
	StaxEventItemReader<StudentXml> studentXmlItemReader(@Value("#{jobParameters['inputFile']}") Resource resource) {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(StudentXml.class);
		return new StaxEventItemReaderBuilder<StudentXml>()
			.saveState(false)
			.resource(resource)
			.addFragmentRootElements("student")
			.unmarshaller(marshaller)
			.build();
	}

	@Bean
	@StepScope
	JdbcCursorItemReader<StudentJdbc> studentDbItemReader(@Qualifier("university") DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<StudentJdbc>()
			.saveState(false)
			.beanRowMapper(StudentJdbc.class)
			.dataSource(dataSource)
			.sql("""
					SELECT id, first_name, last_name, email
					FROM student
					ORDER BY id
					""")
			.build();
	}

	@Bean
	@StepScope
	ItemReaderAdapter<StudentRest> studentRestItemReader() {
		ItemReaderAdapter<StudentRest> adapter = new ItemReaderAdapter<>();
		adapter.setTargetObject(new StudentService());
		adapter.setTargetMethod("getStudent");
		adapter.setArguments(new Object[] {1L, "TEST"});
		return adapter;
	}
}
