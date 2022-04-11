Spring Batch에서 Job은 하나의 배치 작업 단위이다.   
Job 안에는 여러 Step이 존재하고, Step 안에 Tasklet 혹은 Reader & Writer & Processor 묶음이 존재한다.   
![image](https://user-images.githubusercontent.com/69254943/160353865-8c559e5c-9a2c-412f-bc25-4bb0b1ffb50a.png)
- Tasklet 하나와 Reader & Processor & Writer 한 묶음이 같은 레벨이다.
- 따라서, Reader & Processor가 끝나고, Tasklet으로 마무리 짓는 등으로는 만들순 없다.

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

    