package com.home.server.service;

import com.home.server.model.MyIp;
import com.home.server.model.Token;
import com.home.server.model.locations.LocationId;
import com.home.server.model.players.Players;

public interface ICocService {
    LocationId getLocations(Token token, String locationId);
    Players getPlayers(Token token, String playerId);
    MyIp getMyIp(Token token);
}
