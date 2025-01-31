package com.mars.app.domain.auth.repository;

import com.mars.common.model.auth.OAuthProviderToken;
import com.mars.common.model.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OAuthProviderTokenRepository extends JpaRepository<OAuthProviderToken, Long> {

    @Query("SELECT oapt FROM OAuthProviderToken oapt where oapt.user.id = :userId")
    Optional<OAuthProviderToken> findByUserId(@Param("userId") Long userId);

    void deleteByUser(User user);
}
