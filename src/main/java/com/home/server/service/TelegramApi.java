package com.home.server.service;

import com.home.server.model.telegram.Me;
import com.home.server.model.telegram.MsgInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestOperations;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
public class TelegramApi extends BaseService implements ITelegramApi {

    private static final String HOST = "https://api.telegram.org/bot%s";
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private static final String URL_ME = "/getMe";
    private static final String SEND_MESSAGE = "/sendMessage?chat_id=%s&text=%s";

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

    @Override
    public MsgInfo SndMsg(String chatId, String text) {
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Encode URL text " + e.getMessage());
        }
        log.debug(String.format("Send message to chatId %s", chatId));
        String url = String.format(HOST + SEND_MESSAGE, BOT_TOKEN, chatId, text);
        HttpHeaders httpHeaders = new HttpHeaders();
        return request(url, HttpMethod.GET, httpHeaders, MsgInfo.class);
    }

    private String prepareUrl(String host, String url) {
        log.info("URI : " + String.format("%s%s", host, url));
        return String.format("%s%s", host, url);
    }

}
