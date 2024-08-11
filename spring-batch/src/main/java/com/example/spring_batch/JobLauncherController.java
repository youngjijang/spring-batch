package com.example.spring_batch;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobLauncherController {

	@Autowired
	private Job job;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private DefaultBatchConfiguration defaultBatchConfiguration;

	@PostMapping("/batch")
	public JobExecution launcher(@RequestBody String id) throws
		JobInstanceAlreadyCompleteException,
		JobExecutionAlreadyRunningException,
		JobParametersInvalidException,
		JobRestartException {
		JobParameters jobParameters = new JobParametersBuilder()
			.addString("id", id)
			.addDate("date", new Date())
			.toJobParameters();

		// 비동기적 방식
		TaskExecutorJobLauncher taskExecutorJobLauncher = (TaskExecutorJobLauncher)defaultBatchConfiguration.jobLauncher();
		taskExecutorJobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());

		return taskExecutorJobLauncher.run(job, jobParameters);
	}
}
