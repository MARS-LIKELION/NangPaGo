package com.mars.NangPaGo.domain.jwt.repository;

import com.mars.NangPaGo.domain.jwt.entity.RefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByRefreshToken(String refreshToken);

    void deleteByEmail(String email);
}
