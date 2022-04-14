package com.example.springbatchdemo;

import com.example.springbatchdemo.entity.Pay;
import com.example.springbatchdemo.entity.Pay2;
import com.example.springbatchdemo.job.JpaItemWriterJobConfiguration;
import com.example.springbatchdemo.job.TestBatchConfig;
import com.example.springbatchdemo.repository.Pay2Repository;
import com.example.springbatchdemo.repository.PayRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBatchTest    //실행컨텍스트에 테스트 시 필요한 여러 유틸 Bean 등록해줌
@SpringBootTest(classes = {JpaItemWriterJobConfiguration.class, TestBatchConfig.class})
@ActiveProfiles("mysql")
public class JpaItemWriterJobConfigurationIntegrationTest {

    @Autowired
    private PayRepository payRepository;

    @Autowired
    private Pay2Repository pay2Repository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @After
    public void tearDown() throws Exception {
        payRepository.deleteAllInBatch();
        pay2Repository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Pay 테이블에 존재하는 Pay 데이터 개수만큼 Pay2 테이블에 새로운 Pay 데이터 생성")
    public void jpaItemWriterJobConfigTest() throws Exception {
        //given
        List<Pay> lists = new ArrayList<>();

        lists.add(Pay.builder().amount(1000L).txName("test1").build());
        lists.add(Pay.builder().amount(2000L).txName("test2").build());
        lists.add(Pay.builder().amount(3000L).txName("test3").build());

        payRepository.saveAll(lists);

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        List<Pay2> payList = pay2Repository.findAll();
        assertThat(payList.size()).isEqualTo(3);
    }

}
