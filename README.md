# Gradle Multi Module

## Gradle 설정
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

## 참조
- [Gradle, The Java Library Plugin](https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_configurations_graph)
- [Gradle, Declaring dependencies
  ](https://docs.gradle.org/current/userguide/declaring_dependencies.html)
- [Spring Boot, the Java Plugin
  ](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#reacting-to-other-plugins.java)