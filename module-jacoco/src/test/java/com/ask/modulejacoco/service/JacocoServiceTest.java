package com.ask.modulejacoco.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JacocoServiceTest {

  @Autowired
  JacocoService jacocoService;

  @ParameterizedTest
  @ValueSource(strings = {"a", "b", "c"})
  void print(String value) {
    jacocoService.print(value);
  }
}