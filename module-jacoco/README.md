# module-jacoco

##  Jacoco Gradle 설정

### What is JaCoCo
- Java 어플리케이션의 코드 커버리지를 분석해주는 도구
- 대안으로 Cobertura 및 Clover 등이 있다.
- 코드 커버리지에 대한 결과를 HTML, CSV, XML 등의 리포트로 산출
- 설정한 커버리지 만족 여부를 체크 가능

### JaCoCo Gradle Plugin Task
- jacocoTestReport : 바이너리 커버리지 결과를 읽기 좋은 형태의 리포트로 저장. html, xml, csv 지원
- jacocoTestCoverageVerification : 지정한 테스트 커버리지 기준을 만족하는지 확인해 주는 task

### Enable JaCoCo Plugin
```groovy
plugins {
    id 'jacoco'
}

jacoco {
    toolVersion = "0.8.7"
}
```

### test task 실행 시 JaCoCo task 실행하도록 설정
- test task 실행시, test -> jacocoTestReport -> jacocoTestCoverageVerification 순서로 실행
- jacocoTestReport task 실행시 test 먼저 실행후 처리됨
```groovy
test {
    finalizedBy jacocoTestReport
    useJUnitPlatform() // root build.gradle subprojects 에서 선언했을 경우 안해도됨
}
jacocoTestReport {
    dependsOn test
    finalizedBy jacocoTestCoverageVerification
    reports {
        html.required.set(true) // 기존 html.enabled = true deprecated 됨
        xml.required.set(false)
        csv.required.set(false)
    }
}
```

### Coverage Violation Rules
```groovy
jacocoTestCoverageVerification {
    violationRules {
        rule {
            // 'element'가 없을 경우 프로젝트의 전체 파일을 합친 값을 기준으로 처리
            limit {
                // 'counter'를 지정하지 않으면 default는 'INSTRUCTION'
                // 'value'를 지정하지 않으면 default는 'COVEREDRATIO'
                minimum = 0.30
            }
        }

        // 여러 룰을 생성 가능
        rule {
            // default true
            enabled = true

            // 룰을 체크할 단위는 클래스 단위
            element = 'CLASS'

            // 브랜치 커버리지를 최소한 90%
            limit {
                counter = 'BRANCH'
                value = 'COVEREDRATIO'
                minimum = 0.90
            }

            // 라인 커버리지를 최소한 80%
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.80
            }

            // 빈 줄을 제외한 코드의 라인수를 최대 200라인
            limit {
                counter = 'LINE'
                value = 'TOTALCOUNT'
                maximum = 200
            }
        }
    }
}
```
**[element](https://docs.gradle.org/current/javadoc/org/gradle/testing/jacoco/tasks/rules/JacocoViolationRule.html#getElement--)** : 커버리지 체크 기준
- BUNDLE (default): 패키지 번들
- PACKAGE: 패키지
- CLASS: 클래스
- SOURCEFILE: 소스파일
- METHOD: 메소드

**[counter](https://docs.gradle.org/current/javadoc/org/gradle/testing/jacoco/tasks/rules/JacocoLimit.html#getCounter--)**
- INSTRUCTION (default): Java 바이트코드 명령 수. Java bytecode instruction listings
- LINE: 빈 줄을 제외한 실제 코드의 라인 수, 한 라인이라도 실행되었다면 측정, 소스 코드 포맷에 영향을 받는 측정방식
- BRANCH: if, switch 분기에 대한 커버리지 측정
- CLASS: 클래스 내부 메소드가 한번이라도 실행된다면 실행된 것으로 간주
- METHOD: METHOD가 한번이라도 실행되면 실행된 것으로 간주
- COMPLEXITY: 복잡도. 자세한 복잡도 계산은 Coverage Counters - [JaCoCo docs](https://www.eclemma.org/jacoco/trunk/doc/counters.html)

**[value](https://docs.gradle.org/current/javadoc/org/gradle/testing/jacoco/tasks/rules/JacocoLimit.html#getValue--)**
- COVEREDRATIO (default): 커버된 비율. 0부터 1 사이의 숫자로, 1이 100%입니다.
- TOTALCOUNT: 전체 개수
- MISSEDCOUNT: 커버되지 않은 개수
- COVEREDCOUNT: 커버된 개수
- MISSEDRATIO: 커버되지 않은 비율. 0부터 1 사이의 숫자로, 1이 100%입니다.

### Exclude Jacoco Test
- Report 대상에서 제외할 경우 디렉토리 기준 파일 경로로 설정해야함 ex) `**/ModuleJacocoApplication`
- Coverage Verification 대상에서 제외할 경우 패키지 + 클래스명 으로 설정해야함 ex) `*.ModuleJacocoApplication*`
```groovy
jacocoTestReport {
    // QueryDSL 사용시 QDomain 제외
    def qDomains = []
    for (qPattern in '**/QA'..'**/QZ') {
        qDomains.add(qPattern + '*')
    }

    // 디렉토리 기준 파일 경로명으로 설정 해야함 (**, *)
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                    '**/ModuleJacocoApplication*',
                    '**/*Request*',
                    '**/*Response*',
                    '**/*VO*',
                    '**/*Interceptor*',
                    '**/*Exception*'
            ] + qDomains)
        }))
    }
}

jacocoTestCoverageVerification {
    // QueryDSL 사용시 QDomain 제외
    def qDomains = []
    for (qPattern in '*.QA'..'*.QZ') {
        qDomains.add(qPattern + '*')
    }

    violationRules {
        rule {
            element = 'CLASS'

            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.50
            }

            //  Names can use wildcard (* and ?). Defaults to an empty list.
            //  패키지 + 클래스명 으로 설정해야함
            excludes = [
                    '*.ModuleJacocoApplication*',
                    '*.*Request*',
                    '*.*Response*',
                    '*.*VO*',
                    '*.*Interceptor*',
                    '*.*Exception*'
            ] + qDomains
        }
    }
}
```

### Exclude Lombok Code
- @Data, @Builder, @Getter 등의 롬복이 생성한 코드를 제외함
- `lombok.config` 프로젝트 build.gradle과 같은 레벨로 생성
```lombok.config
lombok.addLombokGeneratedAnnotation = true
```

## 참조
- [Gradle, Jacoco Plugin](https://docs.gradle.org/current/userguide/jacoco_plugin.html)
- [Gradle, Interface ConfigurableReport](https://docs.gradle.org/current/javadoc/org/gradle/api/reporting/ConfigurableReport.html)
- [Gradle 프로젝트에 JaCoCo & SonarQube 적용](https://xlffm3.github.io/devops/jacoco-sonarcube/)