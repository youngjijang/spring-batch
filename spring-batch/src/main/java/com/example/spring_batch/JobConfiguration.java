package com.example.spring_batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

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
                //.<String, String>chunk(5, transactionManager)// input, output type 지정
                .<String, String>chunk(new CompletionPolicy() { // chunk 프로세서를 완료하기 위한 정책 설정 클래스
                    @Override
                    public boolean isComplete(RepeatContext context, RepeatStatus result) {
                        return false;
                    }

                    @Override
                    public boolean isComplete(RepeatContext context) {
                        return false;
                    }

                    @Override
                    public RepeatContext start(RepeatContext parent) {
                        return null;
                    }

                    @Override
                    public void update(RepeatContext context) {

                    }
                }, transactionManager)
                .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        Thread.sleep(300);
                        return "my " + item;
                    }
                }) // chunk 프로세서를 위한 필수 설정은 아니다. (optional)
                .writer(new ItemStreamWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        System.out.println("result : " + chunk);
                    }
                })
                .stream(null) // 재시작 데이터를 관리하는 콜백에 대한 스트림 등록
                .readerIsTransactionalQueue() // item이 JMS. Message Queue 같은 트랜잭션 외부에서 사용할것인지 옵션
                .listener(new ChunkListener() {
                    @Override
                    public void beforeChunk(ChunkContext context) {
                        ChunkListener.super.beforeChunk(context);
                    }
                }) // chunk 프로세스가 진행되는 시전에 콜백을 제공하는 Listener 설정
                .build();
    }
}
