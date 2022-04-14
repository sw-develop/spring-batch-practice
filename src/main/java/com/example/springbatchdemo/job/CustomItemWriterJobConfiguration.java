package com.example.springbatchdemo.job;

import com.example.springbatchdemo.entity.Pay;
import com.example.springbatchdemo.repository.PayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CustomItemWriterJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final PayRepository payRepository;

    private static int chunkSize = 10;

    @Bean
    public Job customItemWriterJob() {

        return jobBuilderFactory.get("customItemWriterJob")
                .start(customItemWriterStep())
                .build();
    }

    @Bean
    public Step customItemWriterStep() {

        return stepBuilderFactory.get("customItemWriterStep")
                .<Pay, Pay>chunk(chunkSize)
                .reader(customItemWriterReader())
                .writer(customItemWriter())
                .build();
    }

    /**
     * amount 값이 1000인 row만 가져오기
     * 
     * @return
     */
    @Bean
    public JpaPagingItemReader<Pay> customItemWriterReader() {

        return new JpaPagingItemReaderBuilder<Pay>()
                .name("customItemWriterReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT p FROM Pay p WHERE p.amount = 1000")
                .build();
    }

    /**
     * 전달받은 row 삭제 처리
     *
     * @return
     */
    @Bean
    public ItemWriter<Pay> customItemWriter() {

        return items -> payRepository.deleteAll(items);
    }

}
