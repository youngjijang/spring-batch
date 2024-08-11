package com.example.spring_batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class JobRepositoryListener implements JobExecutionListener {

	@Autowired
	private JobRepository jobRepository;

	@Override
	public void beforeJob(JobExecution jobExecution) {
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
	}
}