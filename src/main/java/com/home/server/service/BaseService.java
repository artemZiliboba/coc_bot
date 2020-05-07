package com.home.server.service;

import com.home.server.model.Token;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

import java.util.Collections;

@Slf4j
public abstract class BaseService {
    private static final String AUTHTOKEN = "authtoken";
    private static final String X_PS_TOKEN = "x-ps-token";
    private static final String STATUS_TEXT = " url: %s. %s";

    @Getter
    private final RestOperations restTemplate;

    @Getter
    protected String host;

    public BaseService(RestOperations restTemplate, String host) {
        this.restTemplate = restTemplate;
        this.host = host;
        //this.logger = logger;
    }

    public <T> T request(String url, HttpMethod method, HttpHeaders header, Class<T> responseType) {
        log.info("\n\t---------------" +
                "\n\tURI : " + url +
                "\n\tHeaders : " + header +
                "\n\t---------------");
        return request(url, method, header, null, responseType);
    }

    public <T> T request(String url, HttpMethod method, HttpHeaders header, String body, Class<T> responseType) {
        T result = null;
        try {
            HttpEntity<String> reqEntity = new HttpEntity<>(body, header);
            ResponseEntity<T> respEntity = restTemplate.exchange(url, method, reqEntity, responseType);
            if (respEntity != null && respEntity.getStatusCode() == HttpStatus.OK) {
                result = respEntity.getBody();
            }
        } catch (HttpClientErrorException e) {
            throw new HttpClientErrorException(e.getStatusCode(), String.format(STATUS_TEXT, url, e.getStatusText()));
        }
        return result;
    }

    protected HttpHeaders prepareRequestHeaders(Token token) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        //requestHeaders.put(AUTHTOKEN, Collections.singletonList(token.getAccess_token()));
        //requestHeaders.put(X_PS_TOKEN, Collections.singletonList(token.getAccess_token()));

        return requestHeaders;
    }
}
