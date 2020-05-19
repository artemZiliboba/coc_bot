package com.home.server.service;

import com.home.server.model.telegram.Me;
import com.home.server.model.telegram.MsgInfo;

public interface ITelegramApi {
    Me getMe();

    MsgInfo SndMsg(String chatId, String text);
}
