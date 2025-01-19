package com.mars.app.domain.auth.repository;

import com.mars.app.domain.auth.entity.OAuthProviderToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthProviderTokenRepository extends JpaRepository<OAuthProviderToken, Long> {
    boolean existsByProviderNameAndEmail(String providerName, String email);

    Optional<OAuthProviderToken> findByProviderNameAndEmail(String providerName, String email);

    void deleteByProviderNameAndEmail(String providerName, String email);
}
