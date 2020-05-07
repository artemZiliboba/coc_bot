package com.home.server.bot;

import com.home.server.model.MyIp;
import com.home.server.model.Token;
import com.home.server.service.CocService;
import com.home.server.db.SqliteManager;
import com.home.server.model.players.Players;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private RestOperations restTemplate = new RestTemplate();
    private SqliteManager sqliteManager = new SqliteManager();
    private static final String host = "https://api.clashofclans.com";
    private CocService cocService = new CocService(restTemplate, host);
    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjFhMDcwMzU4LTMzOGMtNGJhYS1hZjMwLWMyYjAzNGQyZWY4OSIsImlhdCI6MTU4ODYxOTcwOSwic3ViIjoiZGV2ZWxvcGVyL2M4NTEzYmQyLTdiMGMtMjY4ZC01NTJjLWI0MjZjZTc2NjJhZSIsInNjb3BlcyI6WyJjbGFzaCJdLCJsaW1pdHMiOlt7InRpZXIiOiJkZXZlbG9wZXIvc2lsdmVyIiwidHlwZSI6InRocm90dGxpbmcifSx7ImNpZHJzIjpbIjkxLjI0MC4xMjQuMTMwIl0sInR5cGUiOiJjbGllbnQifV19.Xi-5N_6tvcQyjkXFCKdNJYEC70DYUMKBeCYDAWGR2mDXRQ5E5w34I46ItHRf1RBjeAxV6Rln3i7mJSxlPRH6EQ";

    public static void main(String[] args) {

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            // Авторизация бота в прокси, после создания будет использоваться автоматически
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("", "".toCharArray());
                }
            });

            // Создаем экземпляр настроек
            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

            // Устанавливаем настройки прокси
            botOptions.setProxyHost("197.216.2.18");
            botOptions.setProxyPort(8080);
            // Выбираем тип прокси: [HTTP|SOCKS4|SOCKS5] (по умолчанию: NO_PROXY)
            botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);

            telegramBotsApi.registerBot(new Bot(botOptions));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private Bot(DefaultBotOptions options) {
        super(options);
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMsgToMyChanel() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId("-1001241034570");
        sendMessage.setText("From bot");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "/help":
                    Token token = new Token();
                    token.setAccess_token(TOKEN);
                    Players players = cocService.getPlayers(token, "\u2116PGU8YVRVV");

                    // sqliteManager.runQuery(players.getName(), players.getTag(), players.getTownHallLevel(), players.getVersusTrophies(), players.getTrophies());

                    log.info("\n\tName\t: " + players.getName()
                            + "\n\tTag\t\t: " + players.getTag()
                            + "\n\tTown Hall Level \t: " + players.getTownHallLevel());

                    sendMsg(message, String.format("Name: %s " +
                                    "\nTH:%d " +
                                    "\nTrophies: %d" +
                                    "\nVersus trophies: %d"
                            , players.getName(), players.getTownHallLevel(), players.getTrophies(), players.getVersusTrophies()));
                    break;
                case "/settings":
                    sendMsg(message, "It's a setting message");
                    sendMsgToMyChanel();
                    break;
                case "/ip":
                    MyIp myIp = cocService.getMyIp(new Token());
                    sendMsg(message, "BOT IP : " + myIp.getIp());
                default:
            }
        }

    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return "815044037:AAEpHnZwAzWSfl8hJ2aUybaPmr6aCLe87FQ";
    }
}
