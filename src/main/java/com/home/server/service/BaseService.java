package com.home.server.service;

import com.home.server.model.ListResult;
import com.home.server.model.Token;
import com.home.server.model.members.MembersCommon;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Slf4j
public abstract class BaseService {
    private static final String STATUS_TEXT = " url: %s. %s";

    @Getter
    private final RestOperations restTemplate;

    public final static HashMap<Class, ParameterizedTypeReference> paramTypeRefMap = new HashMap<>();

    static {
        paramTypeRefMap.put(MembersCommon.class, new ParameterizedTypeReference<ListResult<MembersCommon>>() {
        });
    }

    @Getter
    protected String host;

    public BaseService(RestOperations restTemplate, String host) {
        this.restTemplate = restTemplate;
        this.host = host;
    }

    public <T> T request(String url, HttpMethod method, HttpHeaders header, Class<T> responseType) {
        return request(url, method, header, null, responseType);
    }

    public <T> T request(String url, HttpMethod method, HttpHeaders header, String body, Class<T> responseType) {
        log.info("\n\t---------------" +
                "\n\tURI : " + url +
                "\n\tHeaders : " + header +
                "\n\tBody : " + body +
                "\n\tMethod : " + method +
                "\n\t---------------");
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

    public <T> ListResult<T> requestList(String url, HttpMethod method, HttpHeaders header, Class<T> responseType) {
        return requestList(url, method, header, null, responseType);
    }

    @SuppressWarnings("unchecked")
    public <T> ListResult<T> requestList(String url, HttpMethod method, HttpHeaders header, String body, Class<T> responseType) {
        log.info("\n\t---------------" +
                "\n\tURI : " + url +
                "\n\tHeaders : " + header +
                "\n\tBody : " + body +
                "\n\tMethod : " + method +
                "\n\t---------------");
        ListResult<T> result = null;
        try {
            HttpEntity<String> reqEntity = new HttpEntity<>(body, header);
            ParameterizedTypeReference parameterizedTypeReference = paramTypeRefMap.get(responseType);
            ResponseEntity<ListResult<T>> respEntity = restTemplate.exchange(url, method, reqEntity, parameterizedTypeReference);
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

        return requestHeaders;
    }
}
