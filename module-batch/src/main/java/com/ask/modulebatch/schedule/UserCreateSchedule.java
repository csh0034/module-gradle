package com.ask.modulebatch.schedule;

import com.ask.modulecore.component.DateUtils;
import com.ask.modulecore.entity.User;
import com.ask.modulecore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreateSchedule {

  private final UserRepository userRepository;
  private final DateUtils dateUtils;

  @Scheduled(fixedRate = 5000, initialDelay = 5000)
  public void schedule() {
    log.info("dateUtils.getNow() : {}", dateUtils.getNow());
    userRepository.save(User.create(String.valueOf(System.currentTimeMillis()), "1234"));
  }
}
