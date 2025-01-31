package com.mars.app.domain.auth.service;

import com.mars.app.domain.auth.client.OAuth2ApiClient;
import com.mars.common.exception.NPGExceptionType;
import com.mars.common.model.auth.OAuthProviderToken;
import com.mars.app.domain.auth.factory.OAuth2TokenFactory;
import com.mars.app.domain.auth.repository.OAuthProviderTokenRepository;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuth2ProviderTokenService {

    private final OAuth2TokenFactory oauth2TokenFactory;
    private final OAuthProviderTokenRepository oauthProviderTokenRepository;
    private final UserRepository userRepository;
    private final OAuth2ApiClient oAuth2ApiClient;

    @Transactional
    public void renewOauth2ProviderToken(String providerName, String refreshToken, Long userId) {
        Optional<OAuthProviderToken> token = oauthProviderTokenRepository
            .findByUserId(userId);

        token.ifPresentOrElse(
            existingToken -> updateTokenIfNeeded(existingToken, refreshToken),
            () -> saveOauth2ProviderToken(providerName, refreshToken, userId)
        );
    }

    @Transactional
    public void deactivateUser(Long userId) throws IOException, InterruptedException {
        User user = userRepository.findById(userId)
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of);
        String providerName = user.getOauth2Provider().name();
        OAuthProviderToken oAuthProviderToken = oauthProviderTokenRepository.findByUserId(user.getId())
            .orElseThrow(NPGExceptionType.NOT_FOUND_OAUTH2_PROVIDER_TOKEN::of);

        disconnectThirdPartyService(providerName, oAuthProviderToken);
        oauthProviderTokenRepository.deleteByUser(user);
        user.withdraw();
    }

    private void updateTokenIfNeeded(OAuthProviderToken existingToken, String refreshToken) {
        if (!existingToken.getProviderRefreshToken().equals(refreshToken)) {
            existingToken.updateRefreshToken(refreshToken);
        }
    }

    private void saveOauth2ProviderToken(String providerName, String refreshToken, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of);

        OAuthProviderToken token = OAuthProviderToken.of(providerName, refreshToken, user);
        oauthProviderTokenRepository.save(token);
    }

    private void disconnectThirdPartyService(String providerName, OAuthProviderToken oAuthProviderToken)
        throws IOException, InterruptedException {

        String accessToken = oAuth2ApiClient.getAccessToken(providerName, oAuthProviderToken.getProviderRefreshToken());
        oAuth2ApiClient.disconnect(providerName, accessToken);
    }
}
