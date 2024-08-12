package com.example.spring_batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobRepositoryListener implements JobExecutionListener {

	@Autowired
	private JobRepository jobRepository;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("^before job^");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
	}
}