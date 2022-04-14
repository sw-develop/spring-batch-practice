package com.example.springbatchdemo.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScopeScheduler {

    private final Job scopeJob;
    private final JobLauncher jobLauncher;

    @Scheduled(cron = "20 * * * * *")   //20초마다 실행
    public void scopeJob() {

        try {
            log.info("********************배치 시작!!*************************");
            jobLauncher.run(
                    scopeJob,
                    new JobParametersBuilder()
                            .addString("requestDate", LocalDateTime.now().toString()).toJobParameters()
            );
            log.info("********************배치 종료!!*************************");

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
