package com.mars.NangPaGo.domain.user.repository;

import com.mars.NangPaGo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
