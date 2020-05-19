package com.home.server.service;

import com.home.server.model.telegram.MsgInfo;
import org.junit.Test;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

public class TelegramApiTest {
    private static final String HOST = "https://api.telegram.org/bot%s";

    private RestOperations restTemplate = new RestTemplate();
    private TelegramApi telegramApi = new TelegramApi(restTemplate, HOST);

    @Test
    public void TestSendMessage() {
        MsgInfo msgInfo = telegramApi.SndMsg("392060526", "Ololotext");
    }
}
