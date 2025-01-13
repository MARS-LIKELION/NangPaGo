package com.mars.NangPaGo.domain.auth.service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.BAD_REQUEST_DISCONNECT_THIRD_PARTY;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_OAUTH2_PROVIDER_TOKEN;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.UNAUTHORIZED_OAUTH2_PROVIDER_TOKEN;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.auth.entity.OauthProviderToken;
import com.mars.NangPaGo.domain.auth.factory.Oauth2TokenFactory;
import com.mars.NangPaGo.domain.auth.factory.oauth2tokeninfo.Oauth2TokenInfo;
import com.mars.NangPaGo.domain.auth.repository.OauthProviderTokenRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class Oauth2ProviderTokenService {

    private final Oauth2TokenFactory oauth2TokenFactory;
    private final OauthProviderTokenRepository oauthProviderTokenRepository;
    private final UserRepository userRepository;

    public static final int RESPONSE_SUCCESS_STATUSCODE = 200;

    @Transactional
    public void checkOauth2ProviderToken(String providerName, String refreshToken, String email) {
        Optional<OauthProviderToken> token = oauthProviderTokenRepository.findByProviderNameAndEmail(providerName,
            email);

        if (token.isEmpty()) {
            saveOauth2ProviderToken(providerName, refreshToken, email);
        }
        if (token.isPresent() && !Objects.equals(token.get().getProviderRefreshToken(), refreshToken)) {
            updateOauth2ProviderToken(token.get(), refreshToken);
        }
    }

    @Transactional
    public void leaveNangpago(String email)
        throws IOException, InterruptedException {
        User user = findUserByEmail(email);
        String providerName = user.getOauth2Provider().name();
        String refreshToken = findProviderRefreshToken(providerName, email);

        String accessToken = Oauth2refreshToAccessToken(providerName, refreshToken);
        disconnectThirdPartyService(user, providerName, accessToken);
    }

    private String Oauth2refreshToAccessToken(String providerName, String refreshToken)
        throws IOException, InterruptedException {

        Oauth2TokenInfo oauth2TokenInfo = oauth2TokenFactory.create(providerName);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(oauth2TokenInfo.getTokenUri()))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(oauth2TokenInfo.getRequestBody(refreshToken)))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == RESPONSE_SUCCESS_STATUSCODE) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());

            return jsonNode.get("access_token").asText(); // 성공적으로 엑세스 토큰을 재발급받음
        } else {
            throw UNAUTHORIZED_OAUTH2_PROVIDER_TOKEN.of();
        }
    }


    private void disconnectThirdPartyService(User user, String providerName, String accessToken)
        throws IOException, InterruptedException {

        Oauth2TokenInfo oauth2TokenInfo = oauth2TokenFactory.create(providerName);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(oauth2TokenInfo.getDisconnectUri(accessToken)))
            .header("Authorization", "Bearer " + accessToken);

        if (Objects.equals(providerName, "KAKAO")) {
            // Kakao의 경우 POST 요청
            requestBuilder.POST(HttpRequest.BodyPublishers.noBody());
        } else {
            requestBuilder.GET();
        }

        HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != RESPONSE_SUCCESS_STATUSCODE) {
            throw BAD_REQUEST_DISCONNECT_THIRD_PARTY.of();
        }
        softDeleteUser(user);
        deleteProviderToken(providerName, user.getEmail());
    }

    private void softDeleteUser(User user) {
        user.softDeleteUser();
    }

    private void deleteProviderToken(String providerName, String email) {
        oauthProviderTokenRepository.deleteByProviderNameAndEmail(providerName, email);
    }

    private void saveOauth2ProviderToken(String providerName, String refreshToken, String email) {
        OauthProviderToken token = OauthProviderToken.of(providerName, refreshToken, email);
        oauthProviderTokenRepository.save(token);
    }

    private void updateOauth2ProviderToken(OauthProviderToken token, String refreshToken) {
        token.updateRefreshToken(refreshToken);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT::of);
    }

    private String findProviderRefreshToken(String providerName, String email) {
        return oauthProviderTokenRepository.findByProviderNameAndEmail(providerName, email)
            .map(OauthProviderToken::getProviderRefreshToken)
            .orElseThrow(NOT_FOUND_OAUTH2_PROVIDER_TOKEN::of);
    }
}
