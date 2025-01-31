package com.mars.app.domain.auth.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mars.app.domain.auth.factory.OAuth2TokenFactory;
import com.mars.app.domain.auth.factory.oauth2tokeninfo.OAuth2TokenInfo;
import com.mars.common.exception.NPGExceptionType;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuth2ApiClient {

    private final HttpClient httpClient;
    private final OAuth2TokenFactory oauth2TokenFactory;
    private final ObjectMapper objectMapper;

    public String getAccessToken(String providerName, String refreshToken)
        throws IOException, InterruptedException {

        OAuth2TokenInfo tokenInfo = oauth2TokenFactory.from(providerName);
        HttpRequest request = createAccessTokenRequest(tokenInfo, refreshToken);

        return executeRequest(request, response ->
            extractAccessToken(response.body()));
    }

    public void disconnect(String providerName, String accessToken)
        throws IOException, InterruptedException {

        OAuth2TokenInfo tokenInfo = oauth2TokenFactory.from(providerName);
        HttpRequest request = createDisconnectRequest(tokenInfo, providerName, accessToken);

        executeRequest(request, response -> null);
    }

    private HttpRequest createAccessTokenRequest(OAuth2TokenInfo tokenInfo, String refreshToken) {
        return HttpRequest.newBuilder()
            .uri(URI.create(tokenInfo.getTokenUri()))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(tokenInfo.getRequestBody(refreshToken)))
            .build();
    }

    private HttpRequest createDisconnectRequest(
        OAuth2TokenInfo tokenInfo,
        String providerName,
        String accessToken
    ) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(tokenInfo.getDisconnectUri(accessToken)))
            .header("Authorization", "Bearer " + accessToken);

        getRequestMethodByProvider(providerName).accept(builder);

        return builder.build();
    }

    private Consumer<HttpRequest.Builder> getRequestMethodByProvider(String providerName) {
        Map<String, Consumer<HttpRequest.Builder>> requestMap = Map.of(
            "KAKAO", builder -> builder.POST(HttpRequest.BodyPublishers.noBody())
        );
        return requestMap.getOrDefault(providerName, HttpRequest.Builder::GET);
    }

    private <T> T executeRequest(HttpRequest request, Function<HttpResponse<String>, T> responseHandler)
        throws IOException, InterruptedException {

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.SC_OK) {
            throw NPGExceptionType.UNAUTHORIZED_OAUTH2_PROVIDER_TOKEN.of();
        }

        return responseHandler.apply(response);
    }

    private String extractAccessToken(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("access_token").asText();
        } catch (JsonProcessingException e) {
            throw NPGExceptionType.SERVER_ERROR.of("Failed to parse access token from response");
        }
    }
}
