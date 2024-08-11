package com.example.spring_batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
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
}
