package org.intensiv.userapi.repository;

import org.intensiv.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}