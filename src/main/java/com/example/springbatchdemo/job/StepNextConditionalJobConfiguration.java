package com.example.springbatchdemo.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepNextConditionalJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepNextConditionalJob() {
        return jobBuilderFactory.get("stepNextConditionalJob")
                .start(conditionalJobStep1())
                    .on("FAILED")   //캐치할 ExitStatus 지정 --> FAILDED일 경우
                    .to(conditionalJobStep3())  //다음으로 이동할 Step을 지정 --> step3로 이동
                    .on("*")    //step3의 결과에 관계없이
                    .end() //FlowBuilder를 반환하는 end() --> flow를 종료
                .from(conditionalJobStep1())    //step1으로부터
                    .on("*")    //FAILDED 외에 모든 경우
                    .to(conditionalJobStep2())  //step2로 이동
                    .next(conditionalJobStep3())    //step2가 정상 종료되면 step3로 이동
                    .on("*")    //step3의 결과에 관계없이
                    .end()  //flow 종료
                .end()  //FlowBuilder를 종료하는 end()
                .build();
    }

    private Step conditionalJobStep1() {
        return stepBuilderFactory.get("step1")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextCondition Step1");

                    /**
                     * ExitStatus를 FAILED로 지정
                     * 해당 Status를 보고 flow가 진행된다.
                     */
                    contribution.setExitStatus(ExitStatus.FAILED);  //분기로직 작성을 위한 값 설정 --> 끝났지만 에러가 발생함

                    return RepeatStatus.FINISHED;   //Step이 끝났음을 명시
                })).build();
    }

    private Step conditionalJobStep2() {
        return stepBuilderFactory.get("step2")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextCondition Step2");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    private Step conditionalJobStep3() {
        return stepBuilderFactory.get("step3")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextCondition Step3");
                    return RepeatStatus.FINISHED;
                })).build();
    }
}
