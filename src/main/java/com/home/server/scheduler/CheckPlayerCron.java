package com.home.server.scheduler;

import com.home.server.model.MyIp;
import com.home.server.model.telegram.MsgInfo;
import com.home.server.service.CocService;
import com.home.server.service.TelegramApi;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class CheckPlayerCron implements Job {
    private static final String COC_URL = System.getenv("COC_URL");
    private RestOperations restTemplate = new RestTemplate();
    private CocService cocService = new CocService(restTemplate, COC_URL);
    private TelegramApi telegramApi = new TelegramApi(restTemplate, "https://api.telegram.org/bot%s");

    public void execute(JobExecutionContext context) throws JobExecutionException {
        MyIp myIp = cocService.getMyIp();
        MsgInfo msgInfo = telegramApi.SndMsg("392060526", "Test");
    }
}
