# Gradle Multi Module

## Gradle 설정

### What is Gradle
- 빌드 자동화 시스템
- Maven을 대체할 수 있는 프로젝트 구성 관리 및 범용 빌드 툴
- 스크립트 기반의 build.gradle 파일로 관리
- JVM의 **동적 타이핑 언어**인 groovy로 만들었다.

### Gradle 특징
- Incrementality (증분성) : Gradle은 가능한 경우 변경된 파일만 작업해 중복 작업을 피한다. → 증분 빌드
- Build cache : 동일한 입력에 대해서 gradle 빌드를 재사용 한다.
- Gradle 데몬 : 빌드 정보를 메모리에 유지하는 프로세스를 구동한다.
- [Compile avoidance](https://blog.gradle.org/incremental-compiler-avoidance) (컴파일 회피) : 
  app -> core -> utils 종속 구성일 경우 utils 변경시에 core를 컴파일하지 않고 **utils 만 재컴파일** 하며 app 에서 바로 변경된 코드 적용, 개발시에 다시 build 하지 않아도 사용 가능함
- 의존성 관리 : api, implementation 등의 다양한 의존성 선언 방법으로 원치 않는 종속성을 처리할 수 있음. 

### configurations
- api
  - A -> B 의존 관계일 경우 A의 의존성이 B에도 전달됨
  - 해당 의존성을 직/간접적으로 의존하고 있는 모든 의존성 재빌드
- implementation
  - A -> B 의존 관계일 경우 A의 의존성이 B에도 전달되지 않음
  - Maven Optional true 와 유사함
  - 해당 의존성을 직접 의존하고 있는 의존성만 재빌드
- testImplementation
  - 테스트 코드를 수행할 때만 적용
  - Maven `<scope>test</scope>` 와 유사함
- compileOnly
  - compile 시에만 빌드하고 빌드 결과물에는 포함하지 않음
- runtimeOnly
    - runtime 시에만 필요한 라이브러리인 경우
- developmentOnly
  - gradle java plugin 과 spring boot plugin 사용시 자동으로 configuration 에 추가되며
  - Devtools와 같이 개발 시에만 필요한 종속성에 대해 선언하며 executable jar, war 패키징시에 제외됨
- annotationProcessor
  - 컴파일 시점에 코드를 생성함
  - gradle 에 이미 정의되어 있는 설정 이지만 IntelliJ 에서 compileClasspath 에 추가해주기 위해 하단 코드 필요함
```groovy
configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}
```

### [Task 사이의 의존 및 순서](https://docs.gradle.org/current/userguide/more_about_tasks.html)
- taskA dependsOn taskB
  - taskA 를 실행하기전에 taskB 이 먼저 수행됨
- taskA mustRunAfter taskB
    - 2개의 작업이 실행될 순서를 제어함
    - 동시에 작업을 할경우 taskB 이후에 teskA가 실행됨
- taskA finalizedBy taskB
  - taskA 후에 taskB 가 수행 되도록 작업 그래프에 추가
  
```groovy
dependencies {
    testImplementation 'org.dbunit:dbunit'
}

jar {
    enabled = true
    archiveClassifier.set('')
}

bootJar {
    enabled = false
}

// @Tag 이용
task initDBTaskWithTag(type: Test) {
    useJUnitPlatform {
        includeTags 'init-db'
    }
}

test {
    dependsOn('initDBTaskWithTag')
    useJUnitPlatform {
        excludeTags 'init-db'
    }
}

// filter, 클래스 이름 이용
task initDBTaskWithClassName(type: Test) {
    useJUnitPlatform {
        filter {
            includeTestsMatching('InitDB')
        }
    }
}

test {
    dependsOn('initDBTaskWithClassName')
    useJUnitPlatform {
        filter {
            excludeTestsMatching('InitDB')
        }
    }
}

// test Task include, exclude 이용
task initDBTaskWithPath(type: Test) {
    useJUnitPlatform()

    // 경로로 설정해야함
    include '**/InitDB*'

    // 시스템 프로퍼티 테스트
    systemProperty 'init.db.enabled', 'true'

//    Fail the 'test' task on the first test failure
//    failFast = true
}

test {
    dependsOn 'initDBTaskWithPath'
    useJUnitPlatform()

    exclude '**/InitDB*'
}
```

### Test Filtering
- [TestFilter](https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/testing/TestFilter.html)
  - includeTest(String className, String methodName)
  - includeTestsMatching(String testNamePattern)
  - excludeTest(String className, String methodName)
  - excludeTestsMatching(String testNamePattern)
- [JUnitPlatformOptions](https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/testing/junitplatform/JUnitPlatformOptions.html)
  - includeTags(String... includeTags)
  - includeEngines(String... includeEngines)
  - excludeTags(String... excludeTags)
  - excludeEngines(String... excludeEngines)
  
```groovy
apply plugin: 'java'

test {

   // 디렉토리 기준 파일 경로명으로 설정 해야함 (**, *)
   include '**/InitDB*'
   exclude 'org/boo/**'

   filter {
       // FQCN, 클래스명, 클래스명.메서드명 (wildcard * 사용 가능)
       includeTestsMatching 'SomeTest'
       includeTestsMatching 'SomeTest.someTestMethod*'

       // FQCN
       includeTestsMatching 'org.gradle.SomeTest'

       // 클래스명 및 메서드에 wildcard 사용
       includeTestsMatching '*SomeTest.*someSpecificFeature'

       // 패키지에 wildcard
       includeTestsMatching '*.SomeTest'
   }
   
   useJUnitPlatform {
       includeTags 'fast', 'unit'
       excludeTags 'slow', 'integration'

       includeEngines 'junit-jupiter'
       excludeEngines 'junit-vintage'
   }
}
```

### ROOT 프로젝트 빌드시에 의존성 지정
- 하위 프로젝트중에 특정 모듈 먼저 빌드 할때 지정
- jar disabled 처리해야 ROOT 모듈의 lib 생성 안됨
```groovy
tasks {
    jar {
        enabled = false
    }
    test {
        dependsOn ':module-core:test'
    }
    build {
        dependsOn ':module-core:build'
    }
}
```

### [transitive dependencies](https://docs.gradle.org/current/userguide/dependency_constraints.html)
- root project 에서 constraints 선언후 sub project 에서 버전 없이 사용 가능
```groovy
dependencies {
    constraints {
        testImplementation 'org.dbunit:dbunit:2.7.2'
    }
    testImplementation 'org.dbunit:dbunit'
}
```


### SPRING BOOT BUILD INFO
- org.springframework.boot.info.BuildProperties 인젝션 받아 사용
- 빌드성능과 재빌드시에 속도 향상을 위해선 time 을 null 주면됨
- additional 을 이용하여 BuildProperties 에 값 추가 가능
```groovy
 springBoot {
    buildInfo {
        properties {
            time = null
            if (System.getenv('build_number') != null) {
                additional = ['build_number': System.getenv('build_number')]
            }
        }
    }
}
```

### Gradle Properties
**Local Variables**
- def key = 'value'


**[Extra Properties](https://docs.gradle.org/current/dsl/org.gradle.api.plugins.ExtensionAware.html)**
- 모든 Extra Properties 는 "ext" 네임스페이스를 통해 정의해야 한다.
- ext 에 선언시에 project.properties 로 자동으로 들어감.
- {task or project}.ext.{key} = 'value'
- {task or project}.ext == {task or project}.extensions.extraProperties

```groovy
// read gradle.properties
assert project.hasProperty('sample.name')
assert project.properties.get('sample.name') != null

// ext 이용
project.ext.myProperty = 'myValue'
assert project.myProperty == 'myValue'
assert project.hasProperty('myProperty')

project.ext {
    valueTest = 'secondeValue'
}
assert project.hasProperty('valueTest')
assert valueTest == 'secondeValue'
```

<br>

**[Spring Boot Application.yml 에서 프로퍼티 사용](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.properties-and-configuration.expand-properties.gradle)**
- [관련 블로그](https://tristanfarmer.dev/blog/gradle_property_expansion_spring_boot)

> Gradle 빌드 스크립트는 기본적으로 Groovy 스크립트이기 때문 Maven 스타일의 점으로 구분된 이름을 사용할 경우  
> 객체 필드 액세스로 해석한다. 따라서 [Gradle 에서 권장 되지 않음](https://discuss.gradle.org/t/dotted-properties-in-gradle/6845)  
> `project.properties['database.host']`, `${project['sample.name']}`

```groovy
processResources {
    filesMatching('**/application.yml') {
        expand project.properties
    }
}
```

## 참조
- [Gradle, The Java Library Plugin](https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_configurations_graph)
- [Gradle, Declaring dependencies
  ](https://docs.gradle.org/current/userguide/declaring_dependencies.html)
- [Gradle, test task](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.testing.Test.html)
- [Spring Boot, the Java Plugin
  ](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#reacting-to-other-plugins.java)