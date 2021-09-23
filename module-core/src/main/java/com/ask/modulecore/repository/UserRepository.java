package com.ask.modulecore.repository;

import com.ask.modulecore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
