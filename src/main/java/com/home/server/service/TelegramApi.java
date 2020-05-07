package com.home.server.service;

import com.home.server.model.telegram.me.Me;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestOperations;

@Slf4j
public class TelegramApi extends BaseService implements ITelegramApi {

    private static final String HOST = "https://api.telegram.org/bot%s";
    private static final String BOT_TOKEN = "815044037:AAEpHnZwAzWSfl8hJ2aUybaPmr6aCLe87FQ";
    private static final String URL_ME = "/getMe";

    public TelegramApi(RestOperations restTemplate, String host) {
        super(restTemplate, host);
    }

    @Override
    public Me getMe() {
        log.info("Loaded info about bot name");
        String url = prepareUrl(String.format(HOST, BOT_TOKEN), URL_ME);
        HttpHeaders httpHeaders = new HttpHeaders();

        return request(url, HttpMethod.GET, httpHeaders, Me.class);
    }

    private String prepareUrl(String host, String url) {
        log.info("URI : " + String.format("%s%s", host, url));
        return String.format("%s%s", host, url);
    }

}
