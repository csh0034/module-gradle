package com.ask;

import com.ask.modulecore.common.Constants;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ModuleBatchApplication {

  public static void main(String[] args) {
    SpringApplication.run(ModuleBatchApplication.class, args);
  }

  @Bean
  public ApplicationRunner applicationRunner() {
    return args -> System.out.println(Constants.VERSION);
  }
}
