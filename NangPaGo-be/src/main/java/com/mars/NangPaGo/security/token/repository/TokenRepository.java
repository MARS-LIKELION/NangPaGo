package com.mars.NangPaGo.security.token.repository;

import com.mars.NangPaGo.security.token.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,String> {
    Optional<Token> findByAccesstoken(String accessToken);
}
