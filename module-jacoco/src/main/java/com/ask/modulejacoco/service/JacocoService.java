package com.ask.modulejacoco.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JacocoService {

  public void print(String value) {
    if ("a".equals(value)) {
      log.info("value is a");
    } else if ("b".equals(value)) {
      log.info("value is b");
    } else {
      log.info("value is not a or b");
    }
  }
}
