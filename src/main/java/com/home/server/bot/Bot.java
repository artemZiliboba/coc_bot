package com.home.server.bot;

import com.home.server.db.HerokuPostgresql;
import com.home.server.dto.MembersData;
import com.home.server.model.ListResult;
import com.home.server.model.MyIp;
import com.home.server.model.developer.CocToken;
import com.home.server.model.members.MembersCommon;
import com.home.server.model.players.Players;
import com.home.server.model.telegram.MsgInfo;
import com.home.server.scheduler.CronTrigger;
import com.home.server.service.AuthService;
import com.home.server.service.CocService;
import com.home.server.service.IAuthService;
import com.home.server.service.TelegramApi;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Bot extends TelegramLongPollingBot {

    private static final String HOST = "https://api.clashofclans.com";
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private static final String LOGTAG = "COMMANDSHANDLER";


    // proxy config
    private static final String PROXY_IP = System.getenv("PROXY_IP");
    private static final String PROXY_PORT = System.getenv("PROXY_PORT");

    private HerokuPostgresql herokuSql = new HerokuPostgresql();
    private RestOperations restTemplate = new RestTemplate();
    private CocService cocService = new CocService(restTemplate, HOST);
    private TelegramApi telegramApi = new TelegramApi(restTemplate, HOST);

    // COC auth service
    private IAuthService authService;
    private static final String COC_URL = System.getenv("COC_URL");
    private static final String COC_EMAIL = System.getenv("COC_EMAIL");
    private static final String COC_PASS = System.getenv("COC_PASS");

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

//            // Устанавливаем настройки прокси
//            if (PROXY_IP != null && PROXY_PORT != null) {
//                botOptions.setProxyHost(PROXY_IP);
//                botOptions.setProxyPort(Integer.parseInt(PROXY_PORT));
//            }
//            // Выбираем тип прокси: [HTTP|SOCKS4|SOCKS5] (по умолчанию: NO_PROXY)
//            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

            telegramBotsApi.registerBot(new Bot(botOptions));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private Bot(DefaultBotOptions options) {
        super(options);
        authService = new AuthService(restTemplate, COC_URL);
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

    private void sendMsgToMyChanel(String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId("-1001241034570");

        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        CocToken cocToken = authService.getCocToken(COC_EMAIL, COC_PASS);

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "/help":
                    BotLogger.info(LOGTAG, "Hello my friend");
                    Players players = cocService.getPlayers(cocToken, "PGU8YVRVV");

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
                    String test = System.getenv("DATABASE_URL");
                    sendMsg(message, "Test variables " + test);
                    sendMsgToMyChanel("From bot, via method");
                    break;
                case "/init":
                    herokuSql.initPostgresDb();
                    sendMsg(message, "The database has been initialized.");
                    break;
                case "/ip":
                    MyIp myIp = cocService.getMyIp();
                    sendMsg(message, String.format("BOT IP : %s\nYou chat ID is %s", myIp.getIp(), message.getChatId().toString()));
                    break;
                case "/start":
                    sendMsg(message, "Hello my lord!");
                    break;
                case "/ruslan":
                    String playerTag = "PGU8YVRVV";
                    String result = checkPlayer(cocToken, playerTag);
                    sendMsg(message, result);
                    break;
                case "/artem":
                    String playerTagArt = "8VP9RGVVQ";
                    String resultArt = checkPlayer(cocToken, playerTagArt);
                    sendMsg(message, resultArt);
                    break;
                case "/trigger":
                    CronTrigger cronTrigger = new CronTrigger();
                    try {
                        log.debug("I in trigger");
                        cronTrigger.startScheduler();
                        sendMsg(message, "Scheduler started.");
                    } catch (Exception e) {
                        log.debug("Failed start scheduler : " + e.getMessage());
                        e.printStackTrace();
                        sendMsg(message, e.getMessage());
                    }
                    break;
                case "/clan":
                    String clanTag = System.getenv("CLAN_TAG");
                    String resultClan = checkMembersClan(cocToken, clanTag);
                    sendMsg(message, resultClan);
                    break;
                case "Hello":
                    try {
                        execute(sendInlineKeyBoardMessage(update.getMessage().getChatId()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "/wtf":
                    MembersData membersData = new MembersData();
                    membersData = herokuSql.checkClanMembers("YLRRJ9PJ");
                    // todo перебрать результат для отправки
                    break;
                default:
            }
        }
    }

    // Inline buttons example
    private static SendMessage sendInlineKeyBoardMessage(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Тык");
        inlineKeyboardButton1.setCallbackData("Button \"Тык\" has been pressed");
        inlineKeyboardButton2.setText("Тык2");
        inlineKeyboardButton2.setCallbackData("Button \"Тык2\" has been pressed");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Fi4a").setCallbackData("CallFi4a"));

        keyboardButtonsRow2.add(inlineKeyboardButton2);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new SendMessage().setChatId(chatId).setText("Пример").setReplyMarkup(inlineKeyboardMarkup);
    }

    private String checkPlayer(CocToken cocToken, String playerTag) {
        Players players = cocService.getPlayers(cocToken, playerTag);
        String result = herokuSql.checkPlayerInDb(players);

        log.info(result);
        return result;
    }

    private String checkMembersClan(CocToken cocToken, String clanTag) {
        String result = "DONE";

        try {
            ListResult<MembersCommon> members = cocService.getMembers(cocToken, clanTag);
            for (MembersCommon item : members.getItems()) {
                Players players = cocService.getPlayers(cocToken, item.getTag().substring(1));
                String temp = herokuSql.checkPlayerInDb(players);
                log.debug(String.format("Result for player %s is : %s", players.getName(), temp));
            }
        } catch (Exception e) {
            return "Failed : " + e.getMessage();
        }

        return result;
    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
