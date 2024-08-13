package com.example.spring_batch;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class JobConfiguration {

	private final JobExecutionListener jobExecutionListener;

	@Bean
	public Job job(JobRepository jobRepository, Step step) {
		return new JobBuilder("myJob", jobRepository)
			.start(step)
			.listener(jobExecutionListener)
			.build();
	}

	public Job simpleJob(JobRepository jobRepository, Step step) {
		return new JobBuilder("myJob", jobRepository)
			.start(step)
			.next(step)
			.incrementer(new CustomJobParametersIncrementer()) /// JobParameter의 값을 자동 증가해주는 설정
			.preventRestart() // Job의 재시작 방어 (Default true)
			.validator(new CustomJobParametersValidator()) // JobParameter 검증
			.listener(jobExecutionListener) // Job 라이프 사이클의 특정 시점에 콜백을 제공하는 listener 설정
			.build(); // Job 생성
	}

	@Bean
	public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("myStep", jobRepository)
			.tasklet(new Tasklet() {
				@Override
				public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
					System.out.println("hello spring batch!!");

					JobParameters jobParameters = contribution.getStepExecution().getJobParameters();
					System.out.println(jobParameters.getString("name"));
					return null;
				}
			}, transactionManager)
			.build();
	}

	@Bean
	public Step chunkStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("chunkStep", jobRepository)
			.<String, String>chunk(5)// input, output type 지정
			.reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
			.processor(new ItemProcessor<String, String>() {
				@Override
				public String process(String item) throws Exception {
					Thread.sleep(300);
					return "my " + item;
				}
			})
			.writer(new ItemStreamWriter<String>() {
				@Override
				public void write(Chunk<? extends String> chunk) throws Exception {
					System.out.println("result : " + chunk);
				}
			})
			.build();
	}
}
