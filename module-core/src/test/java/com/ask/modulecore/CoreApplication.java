package com.ask.modulecore;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CoreApplication {

  @Test
  void contextLoads() {
    log.info("CoreApplication.contextLoads");
  }
}
