package com.mars.NangPaGo.domain.user.repository;

import com.mars.NangPaGo.domain.user.entity.RefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);
}
