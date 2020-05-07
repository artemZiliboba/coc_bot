package com.kaptyuk.artem.base.service;

import com.kaptyuk.artem.base.model.MyIp;
import com.kaptyuk.artem.base.model.Token;
import com.kaptyuk.artem.base.model.locations.LocationId;
import com.kaptyuk.artem.base.model.players.Players;

public interface ICocService {
    LocationId getLocations(Token token, String locationId);
    Players getPlayers(Token token, String playerId);
    MyIp getMyIp(Token token);
}
