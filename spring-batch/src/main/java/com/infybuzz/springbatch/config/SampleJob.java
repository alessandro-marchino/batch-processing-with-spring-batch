package com.infybuzz.springbatch.config;

import java.time.Instant;

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
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.adapter.ItemReaderAdapter;
import org.springframework.batch.infrastructure.item.adapter.ItemWriterAdapter;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.JpaCursorItemReader;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.infrastructure.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.infrastructure.item.json.JacksonJsonObjectReader;
import org.springframework.batch.infrastructure.item.json.JsonFileItemWriter;
import org.springframework.batch.infrastructure.item.json.JsonItemReader;
import org.springframework.batch.infrastructure.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.infrastructure.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.infrastructure.item.xml.StaxEventItemReader;
import org.springframework.batch.infrastructure.item.xml.StaxEventItemWriter;
import org.springframework.batch.infrastructure.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.infrastructure.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.infybuzz.springbatch.listener.FirstJobListener;
import com.infybuzz.springbatch.listener.FirstStepListener;
import com.infybuzz.springbatch.listener.CustomSkipListener;
import com.infybuzz.springbatch.model.Student;
import com.infybuzz.springbatch.model.StudentCsv;
import com.infybuzz.springbatch.model.StudentJdbc;
import com.infybuzz.springbatch.model.StudentJson;
import com.infybuzz.springbatch.model.StudentRest;
import com.infybuzz.springbatch.model.StudentXml;
import com.infybuzz.springbatch.processor.FirstItemProcessor;
import com.infybuzz.springbatch.processor.migration.DepartmentMigrationProcessor;
import com.infybuzz.springbatch.processor.migration.StudentMigrationProcessor;
import com.infybuzz.springbatch.processor.migration.SubjectsLearningMigrationProcessor;
import com.infybuzz.springbatch.reader.FirstItemReader;
import com.infybuzz.springbatch.service.FirstTasklet;
import com.infybuzz.springbatch.service.SecondTasklet;
import com.infybuzz.springbatch.service.StudentService;
import com.infybuzz.springbatch.writer.FirstItemWriter;
import com.infybuzz.springbatch.writer.SecondItemWriter;

import jakarta.persistence.EntityManagerFactory;
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

	// Chunk job
	private final CustomSkipListener skipListener;

	// Migration job
	private final JpaTransactionManager jpaTransactionManager;

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

			.processor(passthroughProcessor(Student.class, StudentCsv.class))

			// Writers
			.writer(studentCsvItemWriter(null))
			// .writer(studentJsonItemWriter(null))
			// .processor(student -> StudentXml.builder().id(student.getId()).firstName(student.getFirstName()).lastName(student.getLastName()).email(student.getEmail()).build())
			// .writer(studentXmlItemWriter(null))
			// .writer(studentDbItemWriter(null))
			// .writer(studentDbItemWriterPreparedStatement(null))
			// .writer(studentRestItemWriter())

			.faultTolerant()
			.skipLimit(100)
			// .skip(FlatFileParseException.class)
			// .skip(NullPointerException.class)
			.retryLimit(1)
			.retry(Exception.class)
			// .skip(FlatFileParseException.class)
			// .skipLimit(Long.MAX_VALUE)
			.skipPolicy(new AlwaysSkipItemSkipPolicy())
			.skipListener(skipListener)

			.build();
	}

	@Bean
	Job migrationJob() {
		return new JobBuilder("Migration Job", jobRepository)
			.start(migrationJobDepartmentStep())
			.next(migrationJobStudentStep())
			.next(migrationJobSubjectsLearningStep())
			.build();
	}

	private Step migrationJobDepartmentStep() {
		return new StepBuilder("Migration job department step", jobRepository)
			// Readers
			.<com.infybuzz.springbatch.entity.postgresql.Department, com.infybuzz.springbatch.entity.mysql.MysqlDepartment>chunk(3)
			.reader(jpaCursorItemReaderDepartment(null))
			// Processors
			.processor(new DepartmentMigrationProcessor())
			// Writers
			.writer(jpaItemWriterDepartment(null))
			.faultTolerant()
			.transactionManager(jpaTransactionManager)
			.build();
	}
	private Step migrationJobStudentStep() {
		return new StepBuilder("Migration job student step", jobRepository)
			// Readers
			.<com.infybuzz.springbatch.entity.postgresql.Student, com.infybuzz.springbatch.entity.mysql.MysqlStudent>chunk(100)
			.reader(jpaCursorItemReaderStudent(null))
			// Processors
			.processor(new StudentMigrationProcessor())
			// Writers
			.writer(jpaItemWriterStudent(null))
			.faultTolerant()
			.transactionManager(jpaTransactionManager)
			.build();
	}
	private Step migrationJobSubjectsLearningStep() {
		return new StepBuilder("Migration job subjects learning step", jobRepository)
			// Readers
			.<com.infybuzz.springbatch.entity.postgresql.SubjectsLearning, com.infybuzz.springbatch.entity.mysql.MysqlSubjectsLearning>chunk(100)
			.reader(jpaCursorItemReaderSubjectsLearning(null))
			// Processors
			.processor(new SubjectsLearningMigrationProcessor())
			// Writers
			.writer(jpaItemWriterSubjectsLearning(null))
			.faultTolerant()
			.transactionManager(jpaTransactionManager)
			.build();
	}

	@Bean
	<A, B extends A> ItemProcessor<B, A> passthroughProcessor(Class<A> clazzA, Class<B> clazzB) {
		return item -> {
			// Do anything
			if(Math.random() > 0.5) {
				throw new NullPointerException();
			}
			return item;
		};
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

	@Bean
	@StepScope
	FlatFileItemWriter<Student> studentCsvItemWriter(@Value("#{jobParameters['outputFile']}") WritableResource resource) {
		return new FlatFileItemWriterBuilder<Student>()
			.saveState(false)
			.resource(resource)
			.delimited(spec -> spec
				.delimiter("|")
				.fieldExtractor(item -> {
					if(Math.random() > 0.5) {
						throw new NullPointerException();
					}
					return new Object[] { item.getId(), item.getFirstName(), item.getLastName(), item.getEmail() };
				})
				// .fieldExtractor(new BeanWrapperFieldExtractor<>("id", "firstName", "lastName", "email"))
			)
			.headerCallback(writer -> writer.write("Id|First Name|Last Name|Email"))
			.footerCallback(writer -> writer.write("Created @ " + Instant.now()))
			.build();
	}
	@Bean
	@StepScope
	JsonFileItemWriter<Student> studentJsonItemWriter(@Value("#{jobParameters['outputFile']}") WritableResource resource) {
		return new JsonFileItemWriterBuilder<Student>()
			.saveState(false)
			.resource(resource)
			.jsonObjectMarshaller(new JacksonJsonObjectMarshaller<Student>())
			.build();
	}

	@Bean
	@StepScope
	StaxEventItemWriter<Student> studentXmlItemWriter(@Value("#{jobParameters['outputFile']}") WritableResource resource) {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(StudentXml.class);
		return new StaxEventItemWriterBuilder<Student>()
			.saveState(false)
			.resource(resource)
			.rootTagName("students")
			.marshaller(marshaller)
			.build();
	}

	@Bean
	@StepScope
	JdbcBatchItemWriter<Student> studentDbItemWriter(@Qualifier("university") DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Student>()
			.dataSource(dataSource)
			.sql("""
				INSERT INTO student_output(original_id, first_name, last_name, email)
				VALUES (:id, :firstName, :lastName, :email)
			""")
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.build();
	}
	@Bean
	@StepScope
	JdbcBatchItemWriter<Student> studentDbItemWriterPreparedStatement(@Qualifier("university") DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Student>()
			.dataSource(dataSource)
			.sql("""
				INSERT INTO student_output(original_id, first_name, last_name, email)
				VALUES (?, ?, ?, ?)
			""")
			.itemPreparedStatementSetter((student, ps) -> {
				ps.setLong(1, student.getId());
				ps.setString(2, student.getFirstName());
				ps.setString(3, student.getLastName());
				ps.setString(4, student.getEmail());
			})
			.build();
	}

	@Bean
	@StepScope
	ItemWriterAdapter<Student> studentRestItemWriter() {
		ItemWriterAdapter<Student> adapter = new ItemWriterAdapter<>();
		adapter.setTargetObject(new StudentService());
		adapter.setTargetMethod("restCallToCreateStudent");
		return adapter;
	}

	@Bean
	@StepScope
	JpaCursorItemReader<com.infybuzz.springbatch.entity.postgresql.Student> jpaCursorItemReaderStudent(@Qualifier("postgres") EntityManagerFactory entityManagerFactory) {
		return new JpaCursorItemReaderBuilder<com.infybuzz.springbatch.entity.postgresql.Student>()
			.saveState(false)
			.entityManagerFactory(entityManagerFactory)
			.queryString("FROM Student")
			.build();
	}

	@Bean
	@StepScope
	JpaItemWriter<com.infybuzz.springbatch.entity.mysql.MysqlStudent> jpaItemWriterStudent(@Qualifier("university") EntityManagerFactory entityManagerFactory) {
		return new JpaItemWriterBuilder<com.infybuzz.springbatch.entity.mysql.MysqlStudent>()
			.entityManagerFactory(entityManagerFactory)
			.build();
	}

	@Bean
	@StepScope
	JpaCursorItemReader<com.infybuzz.springbatch.entity.postgresql.Department> jpaCursorItemReaderDepartment(@Qualifier("postgres") EntityManagerFactory entityManagerFactory) {
		return new JpaCursorItemReaderBuilder<com.infybuzz.springbatch.entity.postgresql.Department>()
			.saveState(false)
			.entityManagerFactory(entityManagerFactory)
			.queryString("FROM Department")
			.build();
	}

	@Bean
	@StepScope
	JpaItemWriter<com.infybuzz.springbatch.entity.mysql.MysqlDepartment> jpaItemWriterDepartment(@Qualifier("university") EntityManagerFactory entityManagerFactory) {
		return new JpaItemWriterBuilder<com.infybuzz.springbatch.entity.mysql.MysqlDepartment>()
			.entityManagerFactory(entityManagerFactory)
			.build();
	}

	@Bean
	@StepScope
	JpaCursorItemReader<com.infybuzz.springbatch.entity.postgresql.SubjectsLearning> jpaCursorItemReaderSubjectsLearning(@Qualifier("postgres") EntityManagerFactory entityManagerFactory) {
		return new JpaCursorItemReaderBuilder<com.infybuzz.springbatch.entity.postgresql.SubjectsLearning>()
			.saveState(false)
			.entityManagerFactory(entityManagerFactory)
			.queryString("FROM SubjectsLearning")
			.build();
	}

	@Bean
	@StepScope
	JpaItemWriter<com.infybuzz.springbatch.entity.mysql.MysqlSubjectsLearning> jpaItemWriterSubjectsLearning(@Qualifier("university") EntityManagerFactory entityManagerFactory) {
		return new JpaItemWriterBuilder<com.infybuzz.springbatch.entity.mysql.MysqlSubjectsLearning>()
			.entityManagerFactory(entityManagerFactory)
			.build();
	}
}
