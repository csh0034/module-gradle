package com.ask.moduleapi;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SampleComponent {

  @Value("${sample.name}")
  private String name;

  @Value("${sample.name2}")
  private String name2;

  @PostConstruct
  public void init() {
    log.info("sample.name : {}", name);
    log.info("sample.name2 : {}", name2);
  }
}
