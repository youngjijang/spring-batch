package com.example.spring_batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

//@Component
public class JobRunner implements ApplicationRunner { // Spring boot가 초기화되고 완료되면 가장 먼저 호출하는 Class

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "youngji")
                .toJobParameters();
        jobLauncher.run(job, jobParameters);
    }
}
