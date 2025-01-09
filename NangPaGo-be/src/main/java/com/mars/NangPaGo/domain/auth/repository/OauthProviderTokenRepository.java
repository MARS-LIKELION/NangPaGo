package com.mars.NangPaGo.domain.auth.repository;

import com.mars.NangPaGo.domain.auth.entity.OauthProviderToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthProviderTokenRepository extends JpaRepository<OauthProviderToken, Long> {
    boolean existsByProviderNameAndEmail(String providerName, String email);

    Optional<OauthProviderToken> findByProviderNameAndEmail(String providerName, String email);

    void deleteByProviderNameAndEmail(String providerName, String email);
}
