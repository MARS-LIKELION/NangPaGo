package com.mars.NangPaGo.domain.user.repository;

import com.mars.NangPaGo.domain.user.entity.Refresh;

import jakarta.transaction.Transactional;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<Refresh, Long> {

    Optional<Refresh> findByToken(String token);

    Optional<Refresh> findByEmail(String email);

    @Transactional
    int deleteByEmail(String email);
}