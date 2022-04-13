# ✔ Spring Batch
![image](https://user-images.githubusercontent.com/69254943/160353865-8c559e5c-9a2c-412f-bc25-4bb0b1ffb50a.png)
### Job
- 하나의 배치 작업 단위
- Job 안에는 여러 Step이 존재한다. 

### Step 
- 2가지 구성 방식이 존재한다.

1) Tasklet
- 간단한 처리에 적합함
- ex) Reader 역할만 필요하고, Writer 역할은 사용하지 않는 경우
    
2) Reader & Processor & Writer 묶음 ==> 하나의 Tasklet을 이룸
- 보편적인 배치 조합
- Chunk 단위 처리를 수행함 (ChunkOrientedTasklet 구현체 사용)

# ✔ MySQL 환경에서 Spring Batch 실행해보기
Spring Batch에서는 Meta Data Table들이 필요하다.

Spring Batch의 메타 데이터는 다음과 같은 내용들을 담고 있다.
- 이전에 실행한 Job이 어떤 것들이 있는지
- 최근 실패한 Batch Parameter가 어떤 것들이 있고, 성공한 Job은 어떤 것들이 있는지
- 다시 실행한다면 어디서부터 시작하면 될지
- 어떤 Job에 어떤 Step이 있었고, Step들 중 성공한 Step과 실패한 Step들은 어떤 것들이 있는지
등등 Batch 어플리케이션을 운영하기 위한 메타 데이터가 여러 테이블에 나눠져 있다.
  
참고) https://docs.spring.io/spring-batch/docs/current/reference/html/schema-appendix.html#metaDataSchema

해당 테이블들이 있어야만 Spring Batch가 정상 작동한다.
기본적으로 H2 데이터베이스를 사용할 경우엔 해당 테이블을 Spring Boot가 실행시 자동으로 생성해주지만, 다른 데이터베이스를 사용하는 경우 직접 생성해야 한다. (schema-mysql.sql 같은 파일이 존재하므로 이를 사용해 테이블을 생성하면 된다)

### BATCH_JOB_INSTANCE 테이블
![image](https://user-images.githubusercontent.com/69254943/160358737-a30e7da0-9566-4473-ac35-5e2475da8735.png)
- JOB_NAME: 수행한 Batch Job Name
- 해당 테이블은 Job Parameter에 따라 생성되는 테이블이다.
    - Job Parameter: Spring Batch가 실행될 때 외부에서 받을 수 있는 파라미터
    - 예를 들어, 특정 날짜를 Job Parameter로 넘기면, Spring Batch에서는 해당 날짜 데이터로 조회/가공/입력 등의 작업을 할 수 있다. 
      
- 동일한 Batch Job이라도 전달된 Job Parameter가 다르면, BATCH_JOB_INSTANCE에는 기록되고, Job Parameter가 같다면, 기록되지 않는다.
    ```BASH
  Caused by: org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException: A job instance already exists and is complete for parameters
    ```

### BATCH_JOB_EXECUTION
- JOB_EXECUTION은 JOB_INSTANCE와 N:1 관계이다.
- JOB_INSTANCE가 성공/실패했던 모든 내역을 가지고 있다.
  
![image](https://user-images.githubusercontent.com/69254943/160363852-d232c2b8-02d6-428a-bcbe-a5624642b32b.png)
- 위의 사진은 BATCH_JOB_EXECUTION 테이블이고, 동일한 JOB_INSTANCE_ID를 가진 2개의 row가 존재하고, 각각 FAILED와 COMPLETED이다.
    - BATCH_JOB_INSTANCE 테이블에는 1개의 row만 생성된다.
- 즉, 동일한 Job Parameter로 생성된 BATCH_JOB_INSTANCE가 2번 실행되었고, 첫 번째는 실패, 두 번째는 성공했다는 것이다.
- 동일한 Job Parameter로 2번 실행했는데 에러가 발생하지 않았다는 점을 통해 Spring Batch는 '동일한 Job Parameter로 성공한 기록이 있을 때만 재수행이 불가능'하다는 것을 알 수 있다.

### JOB, JOB_INSTANCE, JOB_EXECUTION의 관계
![image](https://user-images.githubusercontent.com/69254943/160365372-8bf6890e-b940-4241-b50d-5815caf11237.png)

# ✔ Spring Batch Job Flow
- Step은 실제 Batch 작업을 수행하는 역할로, Batch 비즈니스 로직을 처리하는 기능은 Step에 구현되어 있다.
- Step에서는 Batch로 실제 처리하고자 하는 기능과 설정을 모두 포함하고 있다.

## Step들간의 순서 혹은 처리 흐름을 제어하기 위한 방법
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
- next()를 사용해 순차적으로 Step들을 연결시킬 수 있다.

### 추가) 지정한 Batch Job만 실행
```yml
spring.batch.job.names: ${job.name:NONE}
```
- .yml 파일에 위의 옵션을 추가하면, Spring Batch가 실행될 때, Program Arguments로 job.name 값이 넘어오면 해당 값과 일치하는 Job만 실행시킨다.
- 전달된 값이 없다면, NONE을 할당하여 어떤 Job도 실행되지 않게 한다.

### 2) 조건별 흐름 제어 (Flow)
```JAVA
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
```

### 추가) Batch Status vs. Exit Status
- Batch Status 
  - Job 또는 Step의 실행 결과를 Spring에서 기록할 때 사용하는 Enum
- Exit Status
  - 위의 코드에서 .on()이 참조하는 값은 Batch Status가 아니라, Step의 Exit Status이다.
  - Step의 실행 후 상태 (static 값)

### 2가지 문제점
- Step이 담당하는 역할이 2개 이상이 된다.
  - 실행되는 Step이 처리해야 할 로직 외에도 분기 처리를 위한 Exit Status 값 설정이 필요하다.
  
- 다양한 분기 로직 처리가 어렵다.
  - Exit Status의 커스텀 값을 추가하기 위해서는 Listener를 생성하고, Job Flow에 등록하는 번거로움이 존재한다.

### 3) Decide
- Spring Batch에서 제공해주는 JobExecutionDecider로, Step들간의 Flow 분기만 담당하는 역할을 한다.
- 다양한 분기처리가 가능하다.

```JAVA
@Bean
public Job deciderJob() {

    return jobBuilderFactory.get("deciderJob")
            .start(startStep())
            .next(decider())    //홀수 or 짝수 구분
            .from(decider())    //decider의 상태가
                .on("ODD")  //ODD일 때
                .to(oddStep())  //oddStep으로 간다.
            .from(decider())    //decider의 상태가
                .on("EVEN") //EVEN일 때   
                .to(evenStep()) //evenStep으로 간다.
            .end()  //builder 종료
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
    log.info("랜덤숫자: {}", randomNum);

    if (randomNum % 2 == 0) {
      return new FlowExecutionStatus("EVEN");
    } else {
      return new FlowExecutionStatus("ODD");
    }
  }
}
```
- Step으로 분기 처리를 하는 것이 아니므로, ExitStatus가 아닌 FlowExecutionStatus로 상태를 관리한다.

Q. 오버라이딩한 decide() 메서드는 언제 실행되는 것일까?   
코드의 실행을 보면, JobExecutionDecider를 구현한 클래스의 객체(여기서는 OddDecider의 객체)가 생성될 때 실행되는 것으로 보임

# ✔ Spring Batch Scope & Job Parameter
## Job Parameter와 Scope
- Spring Batch의 경우 외부 or 내부에서 Job Parameter를 받아 여러 Batch 컴포넌트에서 사용할 수 있게 지원한다.
- Job Parameter를 사용하기 위해서는 항상 Spring Batch 전용 Scope를 선언해야 한다.
  - 크게 @JobScope와 @StepScope 2가지가 있고, 해당 Scope로 Bean을 생성할 때만 Job Parameters가 생성된다.
    ```BASH
    # Job Parameter를 사용하는데, 위의 2가지 Scope 중 하나라도 지정하지 않았을 때 발생하는 에러
    Caused by: org.springframework.expression.spel.SpelEvaluationException: EL1008E: Property or field 'jobParameters' cannot be found on object of type
    ```
  - Job Parameter를 전달받아야 하는 메서드에는 Scope를 지정해줘야 하고, Bean 생성 시점이 언제냐에 따라 JobScope or StepScope가 달라진다.
  
## 예시 코드
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
- @JobScope는 Step 선언문에서 사용 가능
- @StepScope는 Tasklet이나 ItemReader, ItemWriter, ItemProcessor에서 사용 가능

## @JobScope와 @StepScope란
### Spring Bean의 기본 Scope는 Singleton이다.
- Bean 생성 시점 : 어플리케이션 실행 시
- 어플리케이션 실행 시 JVM 내에서 스프링이 Bean마다 하나의 객체만 생성한다.
  
### @JobScope & @StepScope
- @JobScope 지정 : Spring Batch가 Spring 컨테이너를 통해 지정된 Job 실행시점에 해당 컴포넌트를 Spring Bean으로 생성 & Job이 끝나면 삭제됨
- @StepScope 지정 : Spring Batch가 Spring 컨테이너를 통해 지정된 Step 실행시점에 해당 컴포넌트를 Spring Bean으로 생성 & Step이 끝나면 삭제됨
- Bean 생성 시점 : 지정된 Job or Step이 실행되는 시점으로 지연 (키포인트!)

### 장점
1. Job Parameter의 Late Binding이 가능하다.
    - Job Parameter를 StepExecutionContext나 JobExecutionContext 레벨에서 할당시킬 수 있다.
    - 어플리케이션이 실행되는 시점에 Job Parameter를 할당해줘야 하는 것이 아니라, Controller나 Service 같은 비즈니스 로직에서 Job Parameter를 할당시킬 수 있다.

2. 동일한 컴포넌트를 병렬 or 동시에 사용할 때 유용하다.
    - 예시 상황)
        - Step 내에 Tasklet이 수행되고, 해당 Tasklet 구현체에 멤버 변수, 해당 멤버 변수를 변경하는 로직이 존재하는 경우
    - @StepScope를 지정하지 않았을 때의 문제점
        - 해당 Tasklet의 Bean이 어플리케이션 실행 시 1개 생성된다.
        - Step들을 병렬로 실행하게 되면, 서로 다른 Step에서 하나의 Tasklet을 두고 상태를 변경하려고 할 것이다.
    - @StepScope를 지정했을 때의 장점
        - 각각의 Step 실행 시 별도의 Tasklet Bean을 생성하고 관리하기 때문에 서로의 상태에 영향을 주지 않는다.
  
  
참고) https://jojoldu.tistory.com/330?category=902551

# ✔ Chunk Oriented Processing
## Chunk란?
![image](https://user-images.githubusercontent.com/69254943/163122763-fc5b1cd6-2a2a-4769-8b39-2e0e085c1858.png)
- Item 단위로 한 번에 하나씩 데이터를 읽고 처리 --> 가공된 데이터들을 별도의 공간에 모음 --> Chunk 단위만큼 쌓이면 Writer에 전달하고 일괄 저장
- Chunk 단위로 트랜잭션을 수행하기 때문에 실패할 경우 Chunk 만큼만 롤백이 되고, 이전 커밋된 트랜잭션은 반영이 된다. (DB의 트랜잭션과 동일)

## Spring Batch의 Chunk Tasklet 진행 과정

### 관련 클래스
- Spring Batch에서 Chunk 지향 처리의 전체 로직을 다루는 클래스가 바로 spring-batch-core의 'ChunkOrientedTasklet' 클래스이다.
    - 멤버변수 ChunkProcessor : Processor & Writer 처리
    - 멤버변수 ChunkProvider : Reader에서 데이터 가져옴

### 진행 과정
![image](https://user-images.githubusercontent.com/69254943/163134118-cf4b76cc-6b7c-4648-8cbf-edc2d5b8d559.png)


# ✔ ItemReader
- 데이터를 읽어오는 역할로, DB 뿐만 아니라 File, XML, JSON 등 다양한 데이터 소스를 배치 처리의 입력으로 사용할 수 있다.
  
### ItemReader 인터페이스 구현체 예시
![image](https://user-images.githubusercontent.com/69254943/163134917-0fcca7e9-dc12-4c0c-abe2-793d67a5491e.png)
- ItemReader 인터페이스
    - 데이터를 읽어오는 메소드 포함
- ItemStream 인터페이스
    - 주기적으로 상태를 저장하고 오류가 발생하면 해당 상태에서 복원하기 위한 마커 기능 수행
    - 배치 프로세스의 실행 컨텍스트와 연계하여 ItemReader의 상태를 저장하고, 실패한 곳에서 다시 실행할 수 있게 해주는 역할

### ItemReader 구현체 종류
1. Cursor 기반
- Database와 SocketTimeout을 충분히 큰 값으로 설정해야 한다.
- Cursor는 하나의 Connection으로 Batch가 끝날 때까지 사용되기 때문에 Batch가 끝나기 전에 Database와 어플리케이션의 Connection이 먼저 끊어질 수 있다.

2. Paging 기반
- Batch 수행 시간이 오래 걸리는 경우 PagingItemReader를 사용하는게 낫다.
- Paging의 경우 한 페이지를 읽을 때마다 Connection을 맺고 끊기 때문에 아무리 많은 데이터라도 DB 연결 타임아웃과 부하없이 수행이 가능하다. 
- Spring Batch에서는 AbstractPagingItemReader 추상 클래스의 setPageSize()를 통해 PageSize를 지정해주면, offset과 limit을 지정한 값에 맞게 자동으로 생성하여 쿼리를 수행한다.
    - 각 쿼리가 개별적으로 실행되므로, 실행 쿼리에 조회 결과를 정렬하는 것(order by)를 꼭!! 넣어줘야 한다.