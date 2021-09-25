package com.ask.modulecore.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ask.modulecore.entity.User;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class UserRepositoryTest {

  @Autowired
  UserRepository userRepository;

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

  @DisplayName("User 삭제")
  @Test
  void delete() {
    // given
    log.info("UserRepositoryTest.delete");
    List<String> userIds = Arrays.asList("user-01", "user-02");

    // when
    userRepository.deleteAllById(userIds);

    // then
    assertThat(userRepository.count()).isZero();
  }
}