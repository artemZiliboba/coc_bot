package com.home.server.bot;

import com.home.server.db.HerokuPostgresql;
import com.home.server.dto.MembersData;
import com.home.server.dto.OneMember;
import com.home.server.model.MyIp;
import com.home.server.model.telegram.MsgInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

@Slf4j
public class Service extends TelegramLongPollingBot implements Job {
    private HerokuPostgresql herokuSql = new HerokuPostgresql();
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");

    public void execute(JobExecutionContext context) throws JobExecutionException {
        sendMsg("Please wait, I'm checking members...");

        MembersData membersData = herokuSql.checkClanMembers("YLRRJ9PJ");
        if(Objects.nonNull(membersData) && membersData.getOneMemberList().size() > 1)
            sendMsg(String.format("Found changes for %d member(s)", membersData.getOneMemberList().size()));
        else
            sendMsg("Without changes...\uD83E\uDD37\u200D♂");
        for (OneMember item : membersData.getOneMemberList()) {
            log.debug("\n\t==== START SEND to" + item.getName() + " === with result :" + item.getResult() + "\n");
            sendMsg(item.getResult());
        }
    }


    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {}

    private void sendMsg(String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.enableHtml(false);
        sendMessage.setChatId("392060526");
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
