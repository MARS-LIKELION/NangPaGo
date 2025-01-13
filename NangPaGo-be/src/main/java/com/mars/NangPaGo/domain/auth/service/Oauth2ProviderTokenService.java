package com.mars.NangPaGo.domain.auth.service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.BAD_REQUEST;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.BAD_REQUEST_DISCONNECT_THIRD_PARTY;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_OAUTH2_PROVIDER_TOKEN;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.UNAUTHORIZED_OAUTH2_PROVIDER_TOKEN;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.auth.entity.OauthProviderToken;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class Oauth2ProviderTokenService {

    public static final int RESPONSE_SUCCESS_STATUSCODE = 200;
    private final OauthProviderTokenRepository oauthProviderTokenRepository;
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String googleTokenUri;
    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.provider.google.dis-connect-uri}")
    private String googleDisConnectUri;
    @Value("${spring.security.oauth2.client.provider.naver.dis-connect-uri}")
    private String naverDisConnectUri;
    @Value("${spring.security.oauth2.client.provider.kakao.dis-connect-uri}")
    private String kakaoDisConnectUri;

    @Transactional
    public void checkOauth2ProviderToken(String providerName, String refreshToken, String email) {
        Optional<OauthProviderToken> token = oauthProviderTokenRepository.findByProviderNameAndEmail(providerName,
            email);

        if (token.isEmpty()) {
            saveOauth2ProviderToken(providerName, refreshToken, email);
        }
        if (!Objects.equals(token.get().getProviderRefreshToken(), refreshToken)) {
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
        String tokenUri = "";
        String requestBody = "";
        String clientId = "";
        String clientSecret = "";

        switch (providerName) {
            case "GOOGLE":
                tokenUri = googleTokenUri;
                clientId = googleClientId;
                clientSecret = googleClientSecret;
                requestBody = "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + clientId
                    + "&client_secret=" + clientSecret;
                break;
            case "NAVER":
                tokenUri = naverTokenUri;
                clientId = naverClientId;
                clientSecret = naverClientSecret;
                requestBody = "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + clientId
                    + "&client_secret=" + clientSecret;
                break;
            case "KAKAO":
                tokenUri = kakaoTokenUri;
                clientId = kakaoClientId;
                requestBody = "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + clientId;
                break;
            default:
                throw BAD_REQUEST.of();
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(tokenUri))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
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
        String disconnectUri = "";

        switch (providerName) {
            case "GOOGLE":
                disconnectUri = googleDisConnectUri + accessToken;
                break;
            case "NAVER":
                disconnectUri = naverDisConnectUri + accessToken;
                break;
            case "KAKAO":
                disconnectUri = kakaoDisConnectUri;
                break;
            default:
                throw BAD_REQUEST.of();
        }

        System.out.println("disUri : "+ disconnectUri);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(disconnectUri))
            .header("Authorization", "Bearer " + accessToken);

        if (providerName.equalsIgnoreCase("KAKAO")) {
            // Kakao의 경우 POST 요청
            requestBuilder.POST(HttpRequest.BodyPublishers.noBody());
        } else {
            // Google과 Naver는 GET 요청
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

    private void deleteProviderToken(String providerName, String email){
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
