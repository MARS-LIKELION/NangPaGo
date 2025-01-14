package com.mars.NangPaGo.domain.auth.service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_OAUTH2_PROVIDER_TOKEN;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.UNAUTHORIZED_OAUTH2_PROVIDER_TOKEN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.auth.entity.OAuthProviderToken;
import com.mars.NangPaGo.domain.auth.factory.OAuth2TokenFactory;
import com.mars.NangPaGo.domain.auth.factory.oauth2tokeninfo.OAuth2TokenInfo;
import com.mars.NangPaGo.domain.auth.repository.OAuthProviderTokenRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuth2ProviderTokenService {

    private final OAuth2TokenFactory oauth2TokenFactory;
    private final OAuthProviderTokenRepository oauthProviderTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void checkOauth2ProviderToken(String providerName, String refreshToken, String email) {
        Optional<OAuthProviderToken> token = oauthProviderTokenRepository.findByProviderNameAndEmail(providerName,
            email);

        token.ifPresentOrElse(
            existingToken -> updateTokenIfNeeded(existingToken, refreshToken),
            () -> saveOauth2ProviderToken(providerName, refreshToken, email)
        );
    }

    @Transactional
    public void deactivateUser(String email)
        throws IOException, InterruptedException {
        User user = findUserByEmail(email);
        String providerName = user.getOauth2Provider().name();
        String refreshToken = findProviderRefreshToken(providerName, email);

        String accessToken = oAuth2GetAccessToken(providerName, refreshToken);
        disconnectThirdPartyService(user, providerName, accessToken);
    }

    private void updateTokenIfNeeded(OAuthProviderToken existingToken, String refreshToken) {
        if (!existingToken.getProviderRefreshToken().equals(refreshToken)) {
            updateOauth2ProviderToken(existingToken, refreshToken);
        }
    }

    private String oAuth2GetAccessToken(String providerName, String refreshToken)
        throws IOException, InterruptedException {

        OAuth2TokenInfo oauth2TokenInfo = oauth2TokenFactory.from(providerName);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(oauth2TokenInfo.getTokenUri()))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(oauth2TokenInfo.getRequestBody(refreshToken)))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return getAccessToken(response);
    }

    private void disconnectThirdPartyService(User user, String providerName, String accessToken)
        throws IOException, InterruptedException {

        OAuth2TokenInfo oauth2TokenInfo = oauth2TokenFactory.from(providerName);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(oauth2TokenInfo.getDisconnectUri(accessToken)))
            .header("Authorization", "Bearer " + accessToken);

        requestProvider(providerName).accept(requestBuilder);

        HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        checkHttpStatusOk(response);

        softDeleteUser(user);
        deleteProviderToken(providerName, user.getEmail());
    }

    private String getAccessToken(HttpResponse<String> response) throws JsonProcessingException {
        checkHttpStatusOk(response);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.body());

        return jsonNode.get("access_token").asText();
    }

    private void checkHttpStatusOk(HttpResponse<String> response){
        if (response.statusCode() != HttpStatus.SC_OK) {
            throw UNAUTHORIZED_OAUTH2_PROVIDER_TOKEN.of();
        }
    }

   private Consumer<HttpRequest.Builder> requestProvider(String providerName){
        Map<String, Consumer<HttpRequest.Builder>> requestMap = Map.of(
            "KAKAO", builder -> builder.POST(HttpRequest.BodyPublishers.noBody())
        );
        return requestMap.getOrDefault(providerName, HttpRequest.Builder::GET);
    }

    private void softDeleteUser(User user) {
        user.softDelete();
    }

    private void deleteProviderToken(String providerName, String email) {
        oauthProviderTokenRepository.deleteByProviderNameAndEmail(providerName, email);
    }

    private void saveOauth2ProviderToken(String providerName, String refreshToken, String email) {
        OAuthProviderToken token = OAuthProviderToken.of(providerName, refreshToken, email);
        oauthProviderTokenRepository.save(token);
    }

    private void updateOauth2ProviderToken(OAuthProviderToken token, String refreshToken) {
        token.updateRefreshToken(refreshToken);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT::of);
    }

    private String findProviderRefreshToken(String providerName, String email) {
        return oauthProviderTokenRepository.findByProviderNameAndEmail(providerName, email)
            .map(OAuthProviderToken::getProviderRefreshToken)
            .orElseThrow(NOT_FOUND_OAUTH2_PROVIDER_TOKEN::of);
    }
}
