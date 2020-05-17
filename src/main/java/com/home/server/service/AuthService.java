package com.home.server.service;

import com.home.server.model.developer.CocToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestOperations;

import java.util.Collections;

@Slf4j
public class AuthService extends BaseService implements IAuthService {

    public AuthService(RestOperations restTemplate, String host) {
        super(restTemplate, host);
    }

    @Override
    public CocToken getCocToken(String email, String pass) {
        log.info("Get temporaryAPIToken token");
        String url = "https://developer.clashofclans.com/api/login";
        String body = String.format("{\"email\": \"%s\",\"password\": \"%s\"}", email, pass);
        CocToken cocToken = request(url, HttpMethod.POST, prepareRequestHeaders(), body, CocToken.class);
        if (cocToken == null) {
            log.debug("Sorry , I can't get temporaryAPIToken by url" + url);
        }
        log.debug("Token got successfully.");
        return cocToken;
    }

    private HttpHeaders prepareRequestHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        return requestHeaders;
    }
}
