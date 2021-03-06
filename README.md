# ๐ Spring Batch
![image](https://user-images.githubusercontent.com/69254943/160353865-8c559e5c-9a2c-412f-bc25-4bb0b1ffb50a.png)

<br>

### Job
- ํ๋์ ๋ฐฐ์น ์์ ๋จ์
- Job ์์๋ ์ฌ๋ฌ Step์ด ์กด์ฌํ๋ค. 

### Step 
- 2๊ฐ์ง ๊ตฌ์ฑ ๋ฐฉ์์ด ์กด์ฌํ๋ค.

1) Tasklet
- ๊ฐ๋จํ ์ฒ๋ฆฌ์ ์ ํฉํจ
- ex) Reader ์ญํ ๋ง ํ์ํ๊ณ , Writer ์ญํ ์ ์ฌ์ฉํ์ง ์๋ ๊ฒฝ์ฐ
    
2) Reader & Processor & Writer ๋ฌถ์ ==> ํ๋์ Tasklet์ ์ด๋ฃธ
- ๋ณดํธ์ ์ธ ๋ฐฐ์น ์กฐํฉ
- Chunk ๋จ์ ์ฒ๋ฆฌ๋ฅผ ์ํํจ (ChunkOrientedTasklet ๊ตฌํ์ฒด ์ฌ์ฉ)

<br>

# ๐ MySQL ํ๊ฒฝ์์ Spring Batch ์คํํด๋ณด๊ธฐ
Spring Batch์์๋ Meta Data Table๋ค์ด ํ์ํ๋ค.

Spring Batch์ ๋ฉํ ๋ฐ์ดํฐ๋ ๋ค์๊ณผ ๊ฐ์ ๋ด์ฉ๋ค์ ๋ด๊ณ  ์๋ค.
- ์ด์ ์ ์คํํ Job์ด ์ด๋ค ๊ฒ๋ค์ด ์๋์ง
- ์ต๊ทผ ์คํจํ Batch Parameter๊ฐ ์ด๋ค ๊ฒ๋ค์ด ์๊ณ , ์ฑ๊ณตํ Job์ ์ด๋ค ๊ฒ๋ค์ด ์๋์ง
- ๋ค์ ์คํํ๋ค๋ฉด ์ด๋์๋ถํฐ ์์ํ๋ฉด ๋ ์ง
- ์ด๋ค Job์ ์ด๋ค Step์ด ์์๊ณ , Step๋ค ์ค ์ฑ๊ณตํ Step๊ณผ ์คํจํ Step๋ค์ ์ด๋ค ๊ฒ๋ค์ด ์๋์ง
๋ฑ๋ฑ Batch ์ดํ๋ฆฌ์ผ์ด์์ ์ด์ํ๊ธฐ ์ํ ๋ฉํ ๋ฐ์ดํฐ๊ฐ ์ฌ๋ฌ ํ์ด๋ธ์ ๋๋ ์ ธ ์๋ค.
  
์ฐธ๊ณ ) https://docs.spring.io/spring-batch/docs/current/reference/html/schema-appendix.html#metaDataSchema

ํด๋น ํ์ด๋ธ๋ค์ด ์์ด์ผ๋ง Spring Batch๊ฐ ์ ์ ์๋ํ๋ค.
๊ธฐ๋ณธ์ ์ผ๋ก H2 ๋ฐ์ดํฐ๋ฒ ์ด์ค๋ฅผ ์ฌ์ฉํ  ๊ฒฝ์ฐ์ ํด๋น ํ์ด๋ธ์ Spring Boot๊ฐ ์คํ์ ์๋์ผ๋ก ์์ฑํด์ฃผ์ง๋ง, ๋ค๋ฅธ ๋ฐ์ดํฐ๋ฒ ์ด์ค๋ฅผ ์ฌ์ฉํ๋ ๊ฒฝ์ฐ ์ง์  ์์ฑํด์ผ ํ๋ค. (schema-mysql.sql ๊ฐ์ ํ์ผ์ด ์กด์ฌํ๋ฏ๋ก ์ด๋ฅผ ์ฌ์ฉํด ํ์ด๋ธ์ ์์ฑํ๋ฉด ๋๋ค)

<br>

### BATCH_JOB_INSTANCE ํ์ด๋ธ
![image](https://user-images.githubusercontent.com/69254943/160358737-a30e7da0-9566-4473-ac35-5e2475da8735.png)
- JOB_NAME: ์ํํ Batch Job Name
- ํด๋น ํ์ด๋ธ์ Job Parameter์ ๋ฐ๋ผ ์์ฑ๋๋ ํ์ด๋ธ์ด๋ค.
    - Job Parameter: Spring Batch๊ฐ ์คํ๋  ๋ ์ธ๋ถ์์ ๋ฐ์ ์ ์๋ ํ๋ผ๋ฏธํฐ
    - ์๋ฅผ ๋ค์ด, ํน์  ๋ ์ง๋ฅผ Job Parameter๋ก ๋๊ธฐ๋ฉด, Spring Batch์์๋ ํด๋น ๋ ์ง ๋ฐ์ดํฐ๋ก ์กฐํ/๊ฐ๊ณต/์๋ ฅ ๋ฑ์ ์์์ ํ  ์ ์๋ค. 
      
- ๋์ผํ Batch Job์ด๋ผ๋ ์ ๋ฌ๋ Job Parameter๊ฐ ๋ค๋ฅด๋ฉด, BATCH_JOB_INSTANCE์๋ ๊ธฐ๋ก๋๊ณ , Job Parameter๊ฐ ๊ฐ๋ค๋ฉด, ๊ธฐ๋ก๋์ง ์๋๋ค.
    ```BASH
  Caused by: org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException: A job instance already exists and is complete for parameters
    ```
    
<br>

### BATCH_JOB_EXECUTION
- JOB_EXECUTION์ JOB_INSTANCE์ N:1 ๊ด๊ณ์ด๋ค.
- JOB_INSTANCE๊ฐ ์ฑ๊ณต/์คํจํ๋ ๋ชจ๋  ๋ด์ญ์ ๊ฐ์ง๊ณ  ์๋ค.
  
![image](https://user-images.githubusercontent.com/69254943/160363852-d232c2b8-02d6-428a-bcbe-a5624642b32b.png)
- ์์ ์ฌ์ง์ BATCH_JOB_EXECUTION ํ์ด๋ธ์ด๊ณ , ๋์ผํ JOB_INSTANCE_ID๋ฅผ ๊ฐ์ง 2๊ฐ์ row๊ฐ ์กด์ฌํ๊ณ , ๊ฐ๊ฐ FAILED์ COMPLETED์ด๋ค.
    - BATCH_JOB_INSTANCE ํ์ด๋ธ์๋ 1๊ฐ์ row๋ง ์์ฑ๋๋ค.
- ์ฆ, ๋์ผํ Job Parameter๋ก ์์ฑ๋ BATCH_JOB_INSTANCE๊ฐ 2๋ฒ ์คํ๋์๊ณ , ์ฒซ ๋ฒ์งธ๋ ์คํจ, ๋ ๋ฒ์งธ๋ ์ฑ๊ณตํ๋ค๋ ๊ฒ์ด๋ค.
- ๋์ผํ Job Parameter๋ก 2๋ฒ ์คํํ๋๋ฐ ์๋ฌ๊ฐ ๋ฐ์ํ์ง ์์๋ค๋ ์ ์ ํตํด Spring Batch๋ '๋์ผํ Job Parameter๋ก ์ฑ๊ณตํ ๊ธฐ๋ก์ด ์์ ๋๋ง ์ฌ์ํ์ด ๋ถ๊ฐ๋ฅ'ํ๋ค๋ ๊ฒ์ ์ ์ ์๋ค.

<br>

### JOB, JOB_INSTANCE, JOB_EXECUTION์ ๊ด๊ณ
![image](https://user-images.githubusercontent.com/69254943/160365372-8bf6890e-b940-4241-b50d-5815caf11237.png)

<br>

# ๐ Spring Batch Job Flow
- Step์ ์ค์  Batch ์์์ ์ํํ๋ ์ญํ ๋ก, Batch ๋น์ฆ๋์ค ๋ก์ง์ ์ฒ๋ฆฌํ๋ ๊ธฐ๋ฅ์ Step์ ๊ตฌํ๋์ด ์๋ค.
- Step์์๋ Batch๋ก ์ค์  ์ฒ๋ฆฌํ๊ณ ์ ํ๋ ๊ธฐ๋ฅ๊ณผ ์ค์ ์ ๋ชจ๋ ํฌํจํ๊ณ  ์๋ค.

<br>

## Step๋ค๊ฐ์ ์์ ํน์ ์ฒ๋ฆฌ ํ๋ฆ์ ์ ์ดํ๊ธฐ ์ํ ๋ฐฉ๋ฒ

<br>

### 1) Next
```JAVA
@Bean
public Job stepNextJob() {
    
    return jobBuilderFactory.get("stepNextJob")
            .start(step1())
            .next(step2())
            .next(step3())
            .build();
    }
```
- next()๋ฅผ ์ฌ์ฉํด ์์ฐจ์ ์ผ๋ก Step๋ค์ ์ฐ๊ฒฐ์ํฌ ์ ์๋ค.

<br>

### ์ถ๊ฐ) ์ง์ ํ Batch Job๋ง ์คํ
```yml
spring.batch.job.names: ${job.name:NONE}
```
- .yml ํ์ผ์ ์์ ์ต์์ ์ถ๊ฐํ๋ฉด, Spring Batch๊ฐ ์คํ๋  ๋, Program Arguments๋ก job.name ๊ฐ์ด ๋์ด์ค๋ฉด ํด๋น ๊ฐ๊ณผ ์ผ์นํ๋ Job๋ง ์คํ์ํจ๋ค.
- ์ ๋ฌ๋ ๊ฐ์ด ์๋ค๋ฉด, NONE์ ํ ๋นํ์ฌ ์ด๋ค Job๋ ์คํ๋์ง ์๊ฒ ํ๋ค.

<br>

### 2) ์กฐ๊ฑด๋ณ ํ๋ฆ ์ ์ด (Flow)
```JAVA
@Bean
public Job stepNextConditionalJob() {
    
    return jobBuilderFactory.get("stepNextConditionalJob")
        .start(conditionalJobStep1())
        .on("FAILED")   //์บ์นํ  ExitStatus ์ง์  --> FAILDED์ผ ๊ฒฝ์ฐ
        .to(conditionalJobStep3())  //๋ค์์ผ๋ก ์ด๋ํ  Step์ ์ง์  --> step3๋ก ์ด๋
        .on("*")    //step3์ ๊ฒฐ๊ณผ์ ๊ด๊ณ์์ด
        .end() //FlowBuilder๋ฅผ ๋ฐํํ๋ end() --> flow๋ฅผ ์ข๋ฃ
        .from(conditionalJobStep1())    //step1์ผ๋ก๋ถํฐ
        .on("*")    //FAILDED ์ธ์ ๋ชจ๋  ๊ฒฝ์ฐ
        .to(conditionalJobStep2())  //step2๋ก ์ด๋
        .next(conditionalJobStep3())    //step2๊ฐ ์ ์ ์ข๋ฃ๋๋ฉด step3๋ก ์ด๋
        .on("*")    //step3์ ๊ฒฐ๊ณผ์ ๊ด๊ณ์์ด
        .end()  //flow ์ข๋ฃ
        .end()  //FlowBuilder๋ฅผ ์ข๋ฃํ๋ end()
        .build();
}
```

<br>

### ์ถ๊ฐ) Batch Status vs. Exit Status
- Batch Status 
  - Job ๋๋ Step์ ์คํ ๊ฒฐ๊ณผ๋ฅผ Spring์์ ๊ธฐ๋กํ  ๋ ์ฌ์ฉํ๋ Enum
- Exit Status
  - ์์ ์ฝ๋์์ .on()์ด ์ฐธ์กฐํ๋ ๊ฐ์ Batch Status๊ฐ ์๋๋ผ, Step์ Exit Status์ด๋ค.
  - Step์ ์คํ ํ ์ํ (static ๊ฐ)

<br>

### 2๊ฐ์ง ๋ฌธ์ ์ 
- Step์ด ๋ด๋นํ๋ ์ญํ ์ด 2๊ฐ ์ด์์ด ๋๋ค.
  - ์คํ๋๋ Step์ด ์ฒ๋ฆฌํด์ผ ํ  ๋ก์ง ์ธ์๋ ๋ถ๊ธฐ ์ฒ๋ฆฌ๋ฅผ ์ํ Exit Status ๊ฐ ์ค์ ์ด ํ์ํ๋ค.
  
- ๋ค์ํ ๋ถ๊ธฐ ๋ก์ง ์ฒ๋ฆฌ๊ฐ ์ด๋ ต๋ค.
  - Exit Status์ ์ปค์คํ ๊ฐ์ ์ถ๊ฐํ๊ธฐ ์ํด์๋ Listener๋ฅผ ์์ฑํ๊ณ , Job Flow์ ๋ฑ๋กํ๋ ๋ฒ๊ฑฐ๋ก์์ด ์กด์ฌํ๋ค.

<br>

### 3) Decide
- Spring Batch์์ ์ ๊ณตํด์ฃผ๋ JobExecutionDecider๋ก, Step๋ค๊ฐ์ Flow ๋ถ๊ธฐ๋ง ๋ด๋นํ๋ ์ญํ ์ ํ๋ค.
- ๋ค์ํ ๋ถ๊ธฐ์ฒ๋ฆฌ๊ฐ ๊ฐ๋ฅํ๋ค.

```JAVA
@Bean
public Job deciderJob() {

    return jobBuilderFactory.get("deciderJob")
            .start(startStep())
            .next(decider())    //ํ์ or ์ง์ ๊ตฌ๋ถ
            .from(decider())    //decider์ ์ํ๊ฐ
                .on("ODD")  //ODD์ผ ๋
                .to(oddStep())  //oddStep์ผ๋ก ๊ฐ๋ค.
            .from(decider())    //decider์ ์ํ๊ฐ
                .on("EVEN") //EVEN์ผ ๋   
                .to(evenStep()) //evenStep์ผ๋ก ๊ฐ๋ค.
            .end()  //builder ์ข๋ฃ
            .build();
}

@Bean
public JobExecutionDecider decider() {

        return new OddDecider();
}

public static class OddDecider implements JobExecutionDecider {

  @Override
  public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

    Random random = new Random();

    int randomNum = random.nextInt(50) + 1;
    log.info("๋๋ค์ซ์: {}", randomNum);

    if (randomNum % 2 == 0) {
      return new FlowExecutionStatus("EVEN");
    } else {
      return new FlowExecutionStatus("ODD");
    }
  }
}
```
- Step์ผ๋ก ๋ถ๊ธฐ ์ฒ๋ฆฌ๋ฅผ ํ๋ ๊ฒ์ด ์๋๋ฏ๋ก, ExitStatus๊ฐ ์๋ FlowExecutionStatus๋ก ์ํ๋ฅผ ๊ด๋ฆฌํ๋ค.

Q. ์ค๋ฒ๋ผ์ด๋ฉํ decide() ๋ฉ์๋๋ ์ธ์  ์คํ๋๋ ๊ฒ์ผ๊น?   
์ฝ๋์ ์คํ์ ๋ณด๋ฉด, JobExecutionDecider๋ฅผ ๊ตฌํํ ํด๋์ค์ ๊ฐ์ฒด(์ฌ๊ธฐ์๋ OddDecider์ ๊ฐ์ฒด)๊ฐ ์์ฑ๋  ๋ ์คํ๋๋ ๊ฒ์ผ๋ก ๋ณด์

<br>

# ๐ Spring Batch Scope & Job Parameter

<br>

## Job Parameter์ Scope
- Spring Batch์ ๊ฒฝ์ฐ ์ธ๋ถ or ๋ด๋ถ์์ Job Parameter๋ฅผ ๋ฐ์ ์ฌ๋ฌ Batch ์ปดํฌ๋ํธ์์ ์ฌ์ฉํ  ์ ์๊ฒ ์ง์ํ๋ค.
- Job Parameter๋ฅผ ์ฌ์ฉํ๊ธฐ ์ํด์๋ ํญ์ Spring Batch ์ ์ฉ Scope๋ฅผ ์ ์ธํด์ผ ํ๋ค.
  - ํฌ๊ฒ @JobScope์ @StepScope 2๊ฐ์ง๊ฐ ์๊ณ , ํด๋น Scope๋ก Bean์ ์์ฑํ  ๋๋ง Job Parameters๊ฐ ์์ฑ๋๋ค.
    ```BASH
    # Job Parameter๋ฅผ ์ฌ์ฉํ๋๋ฐ, ์์ 2๊ฐ์ง Scope ์ค ํ๋๋ผ๋ ์ง์ ํ์ง ์์์ ๋ ๋ฐ์ํ๋ ์๋ฌ
    Caused by: org.springframework.expression.spel.SpelEvaluationException: EL1008E: Property or field 'jobParameters' cannot be found on object of type
    ```
  - Job Parameter๋ฅผ ์ ๋ฌ๋ฐ์์ผ ํ๋ ๋ฉ์๋์๋ Scope๋ฅผ ์ง์ ํด์ค์ผ ํ๊ณ , Bean ์์ฑ ์์ ์ด ์ธ์ ๋์ ๋ฐ๋ผ JobScope or StepScope๊ฐ ๋ฌ๋ผ์ง๋ค.

<br>
  
## ์์ ์ฝ๋
```java
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ScopeConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job scopeJob() {

        return jobBuilderFactory.get("scopeJob")
                .start(scopeStep1(null))
                .next(scopeStep2())
                .build();
    }

    @Bean
    @JobScope
    public Step scopeStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {

        return stepBuilderFactory.get("scopeStep1")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>>> This is scopeStep1");
                    log.info(">>>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step scopeStep2() {

        return stepBuilderFactory.get("scopeStep2")
                .tasklet(scopeStep2Tasklet(null))
                .build();
    }

    @Bean
    @StepScope
    public Tasklet scopeStep2Tasklet(@Value("#{jobParameters[requestDate]}") String requestDate) {

        return ((contribution, chunkContext) -> {
            log.info(">>>>> This is scopeStep2");
            log.info(">>>>> requestDate = {}", requestDate);
            return RepeatStatus.FINISHED;
        });
    }
}
```
- @JobScope๋ Step ์ ์ธ๋ฌธ์์ ์ฌ์ฉ ๊ฐ๋ฅ
- @StepScope๋ Tasklet์ด๋ ItemReader, ItemWriter, ItemProcessor์์ ์ฌ์ฉ ๊ฐ๋ฅ

<br>

## @JobScope์ @StepScope๋

<br>

### Spring Bean์ ๊ธฐ๋ณธ Scope๋ Singleton์ด๋ค.
- Bean ์์ฑ ์์  : ์ดํ๋ฆฌ์ผ์ด์ ์คํ ์
- ์ดํ๋ฆฌ์ผ์ด์ ์คํ ์ JVM ๋ด์์ ์คํ๋ง์ด Bean๋ง๋ค ํ๋์ ๊ฐ์ฒด๋ง ์์ฑํ๋ค.
  
<br>  
  
### @JobScope & @StepScope
- @JobScope ์ง์  : Spring Batch๊ฐ Spring ์ปจํ์ด๋๋ฅผ ํตํด ์ง์ ๋ Job ์คํ์์ ์ ํด๋น ์ปดํฌ๋ํธ๋ฅผ Spring Bean์ผ๋ก ์์ฑ & Job์ด ๋๋๋ฉด ์ญ์ ๋จ
- @StepScope ์ง์  : Spring Batch๊ฐ Spring ์ปจํ์ด๋๋ฅผ ํตํด ์ง์ ๋ Step ์คํ์์ ์ ํด๋น ์ปดํฌ๋ํธ๋ฅผ Spring Bean์ผ๋ก ์์ฑ & Step์ด ๋๋๋ฉด ์ญ์ ๋จ
- Bean ์์ฑ ์์  : ์ง์ ๋ Job or Step์ด ์คํ๋๋ ์์ ์ผ๋ก ์ง์ฐ (ํคํฌ์ธํธ!)

<br>

### ์ฅ์ 
1. Job Parameter์ Late Binding์ด ๊ฐ๋ฅํ๋ค.
    - Job Parameter๋ฅผ StepExecutionContext๋ JobExecutionContext ๋ ๋ฒจ์์ ํ ๋น์ํฌ ์ ์๋ค.
    - ์ดํ๋ฆฌ์ผ์ด์์ด ์คํ๋๋ ์์ ์ Job Parameter๋ฅผ ํ ๋นํด์ค์ผ ํ๋ ๊ฒ์ด ์๋๋ผ, Controller๋ Service ๊ฐ์ ๋น์ฆ๋์ค ๋ก์ง์์ Job Parameter๋ฅผ ํ ๋น์ํฌ ์ ์๋ค.

2. ๋์ผํ ์ปดํฌ๋ํธ๋ฅผ ๋ณ๋ ฌ or ๋์์ ์ฌ์ฉํ  ๋ ์ ์ฉํ๋ค.
    - ์์ ์ํฉ)
        - Step ๋ด์ Tasklet์ด ์ํ๋๊ณ , ํด๋น Tasklet ๊ตฌํ์ฒด์ ๋ฉค๋ฒ ๋ณ์, ํด๋น ๋ฉค๋ฒ ๋ณ์๋ฅผ ๋ณ๊ฒฝํ๋ ๋ก์ง์ด ์กด์ฌํ๋ ๊ฒฝ์ฐ
    - @StepScope๋ฅผ ์ง์ ํ์ง ์์์ ๋์ ๋ฌธ์ ์ 
        - ํด๋น Tasklet์ Bean์ด ์ดํ๋ฆฌ์ผ์ด์ ์คํ ์ 1๊ฐ ์์ฑ๋๋ค.
        - Step๋ค์ ๋ณ๋ ฌ๋ก ์คํํ๊ฒ ๋๋ฉด, ์๋ก ๋ค๋ฅธ Step์์ ํ๋์ Tasklet์ ๋๊ณ  ์ํ๋ฅผ ๋ณ๊ฒฝํ๋ ค๊ณ  ํ  ๊ฒ์ด๋ค.
    - @StepScope๋ฅผ ์ง์ ํ์ ๋์ ์ฅ์ 
        - ๊ฐ๊ฐ์ Step ์คํ ์ ๋ณ๋์ Tasklet Bean์ ์์ฑํ๊ณ  ๊ด๋ฆฌํ๊ธฐ ๋๋ฌธ์ ์๋ก์ ์ํ์ ์ํฅ์ ์ฃผ์ง ์๋๋ค.
  
  
์ฐธ๊ณ ) https://jojoldu.tistory.com/330?category=902551

<br>

# ๐ Chunk Oriented Processing

<br>

## Chunk๋?
![image](https://user-images.githubusercontent.com/69254943/163122763-fc5b1cd6-2a2a-4769-8b39-2e0e085c1858.png)
- Item ๋จ์๋ก ํ ๋ฒ์ ํ๋์ฉ ๋ฐ์ดํฐ๋ฅผ ์ฝ๊ณ  ์ฒ๋ฆฌ --> ๊ฐ๊ณต๋ ๋ฐ์ดํฐ๋ค์ ๋ณ๋์ ๊ณต๊ฐ์ ๋ชจ์ --> Chunk ๋จ์๋งํผ ์์ด๋ฉด Writer์ ์ ๋ฌํ๊ณ  ์ผ๊ด ์ ์ฅ
- Chunk ๋จ์๋ก ํธ๋์ญ์์ ์ํํ๊ธฐ ๋๋ฌธ์ ์คํจํ  ๊ฒฝ์ฐ Chunk ๋งํผ๋ง ๋กค๋ฐฑ์ด ๋๊ณ , ์ด์  ์ปค๋ฐ๋ ํธ๋์ญ์์ ๋ฐ์์ด ๋๋ค. (DB์ ํธ๋์ญ์๊ณผ ๋์ผ)

<br>

## Spring Batch์ Chunk Tasklet ์งํ ๊ณผ์ 

<br>

### ๊ด๋ จ ํด๋์ค
- Spring Batch์์ Chunk ์งํฅ ์ฒ๋ฆฌ์ ์ ์ฒด ๋ก์ง์ ๋ค๋ฃจ๋ ํด๋์ค๊ฐ ๋ฐ๋ก spring-batch-core์ 'ChunkOrientedTasklet' ํด๋์ค์ด๋ค.
    - ๋ฉค๋ฒ๋ณ์ ChunkProcessor : Processor & Writer ์ฒ๋ฆฌ
    - ๋ฉค๋ฒ๋ณ์ ChunkProvider : Reader์์ ๋ฐ์ดํฐ ๊ฐ์ ธ์ด

<br>

### ์งํ ๊ณผ์ 
![image](https://user-images.githubusercontent.com/69254943/163134118-cf4b76cc-6b7c-4648-8cbf-edc2d5b8d559.png)
- Processor๋ ์ ํ์ผ๋ก, Processor๊ฐ ์์ด๋ ChunkOrientedTasklet ํด๋์ค๋ฅผ ๊ตฌ์ฑํ  ์ ์์ง๋ง, Reader์ Writer๋ ํ์์ด๋ค.

<br>

# ๐ ItemReader
- ๋ฐ์ดํฐ๋ฅผ ์ฝ์ด์ค๋ ์ญํ ๋ก, DB ๋ฟ๋ง ์๋๋ผ File, XML, JSON ๋ฑ ๋ค์ํ ๋ฐ์ดํฐ ์์ค๋ฅผ ๋ฐฐ์น ์ฒ๋ฆฌ์ ์๋ ฅ์ผ๋ก ์ฌ์ฉํ  ์ ์๋ค.

<br>
  
### ItemReader ์ธํฐํ์ด์ค ๊ตฌํ์ฒด ์์
![image](https://user-images.githubusercontent.com/69254943/163134917-0fcca7e9-dc12-4c0c-abe2-793d67a5491e.png)
- ItemReader ์ธํฐํ์ด์ค
    - ๋ฐ์ดํฐ๋ฅผ ์ฝ์ด์ค๋ ๋ฉ์๋ ํฌํจ
- ItemStream ์ธํฐํ์ด์ค
    - ์ฃผ๊ธฐ์ ์ผ๋ก ์ํ๋ฅผ ์ ์ฅํ๊ณ  ์ค๋ฅ๊ฐ ๋ฐ์ํ๋ฉด ํด๋น ์ํ์์ ๋ณต์ํ๊ธฐ ์ํ ๋ง์ปค ๊ธฐ๋ฅ ์ํ
    - ๋ฐฐ์น ํ๋ก์ธ์ค์ ์คํ ์ปจํ์คํธ์ ์ฐ๊ณํ์ฌ ItemReader์ ์ํ๋ฅผ ์ ์ฅํ๊ณ , ์คํจํ ๊ณณ์์ ๋ค์ ์คํํ  ์ ์๊ฒ ํด์ฃผ๋ ์ญํ 

<br>

### ItemReader ๊ตฌํ์ฒด ์ข๋ฅ
1. Cursor ๊ธฐ๋ฐ
- Database์ SocketTimeout์ ์ถฉ๋ถํ ํฐ ๊ฐ์ผ๋ก ์ค์ ํด์ผ ํ๋ค.
- Cursor๋ ํ๋์ Connection์ผ๋ก Batch๊ฐ ๋๋  ๋๊น์ง ์ฌ์ฉ๋๊ธฐ ๋๋ฌธ์ Batch๊ฐ ๋๋๊ธฐ ์ ์ Database์ ์ดํ๋ฆฌ์ผ์ด์์ Connection์ด ๋จผ์  ๋์ด์ง ์ ์๋ค.

2. Paging ๊ธฐ๋ฐ
- Batch ์ํ ์๊ฐ์ด ์ค๋ ๊ฑธ๋ฆฌ๋ ๊ฒฝ์ฐ PagingItemReader๋ฅผ ์ฌ์ฉํ๋๊ฒ ๋ซ๋ค.
- Paging์ ๊ฒฝ์ฐ ํ ํ์ด์ง๋ฅผ ์ฝ์ ๋๋ง๋ค Connection์ ๋งบ๊ณ  ๋๊ธฐ ๋๋ฌธ์ ์๋ฌด๋ฆฌ ๋ง์ ๋ฐ์ดํฐ๋ผ๋ DB ์ฐ๊ฒฐ ํ์์์๊ณผ ๋ถํ์์ด ์ํ์ด ๊ฐ๋ฅํ๋ค. 
- Spring Batch์์๋ AbstractPagingItemReader ์ถ์ ํด๋์ค์ setPageSize()๋ฅผ ํตํด PageSize๋ฅผ ์ง์ ํด์ฃผ๋ฉด, offset๊ณผ limit์ ์ง์ ํ ๊ฐ์ ๋ง๊ฒ ์๋์ผ๋ก ์์ฑํ์ฌ ์ฟผ๋ฆฌ๋ฅผ ์ํํ๋ค.
    - ๊ฐ ์ฟผ๋ฆฌ๊ฐ ๊ฐ๋ณ์ ์ผ๋ก ์คํ๋๋ฏ๋ก, ์คํ ์ฟผ๋ฆฌ์ ์กฐํ ๊ฒฐ๊ณผ๋ฅผ ์ ๋ ฌํ๋ ๊ฒ(order by)์ ๊ผญ!! ๋ฃ์ด์ค์ผ ํ๋ค.
    

์ฐธ๊ณ ) https://jojoldu.tistory.com/336?category=902551

<br>

# ๐ ItemWriter
- Spring Batch์์ ์ฌ์ฉํ๋ ์ถ๋ ฅ ๊ธฐ๋ฅ์ด๋ค.
- Chunk ๋จ์๋ก ๋ฌถ์ธ Item List๋ฅผ ๋ค๋ฃฌ๋ค. (์์ 'Chunk ์งํฅ ์ฒ๋ฆฌ' ์ฌ์ง ์ฐธ๊ณ )
    - Reader์ Processor๋ฅผ ๊ฑฐ์ณ ์ฒ๋ฆฌ๋ Item์ Chunk ๋จ์ ๋งํผ ์์ ๋ค Writer์ ์ ๋ฌํ๋ค.
    
- Writer๊ฐ ๋ฐ์ ๋ชจ๋  Item์ด ์ฒ๋ฆฌ๋ ํ, Spring Batch๊ฐ ํ์ฌ ํธ๋์ญ์์ ์ปค๋ฐํ๋ค.

์ฐธ๊ณ ) https://jojoldu.tistory.com/339?category=902551

<br>

# Spring Batch ํ์คํธ ์ฝ๋ ์์ฑ
์ฐธ๊ณ 
- https://velog.io/@miz/SpringBatch-Jpa-Test-%EC%84%A4%EC%A0%95
- https://jojoldu.tistory.com/455
