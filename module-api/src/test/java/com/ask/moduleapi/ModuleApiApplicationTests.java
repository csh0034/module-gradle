package com.ask.moduleapi;

import static org.assertj.core.api.Assertions.assertThat;

import com.ask.modulecore.entity.User;
import com.ask.modulecore.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class ModuleApiApplicationTests {

  @Autowired
  UserRepository userRepository;

  @Test
  void contextLoads() {
  }

  @DisplayName("User 저장")
  @Test
  void save() {
    // given
    log.info("UserRepositoryTest.save");
    User user = User.create("ask", "1234");

    // when
    User savedUser = userRepository.saveAndFlush(user);

    // then
    assertThat(user).isEqualTo(savedUser);
  }

}
