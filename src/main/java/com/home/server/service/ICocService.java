package com.home.server.service;

import com.home.server.model.ListResult;
import com.home.server.model.MyIp;
import com.home.server.model.Token;
import com.home.server.model.locations.LocationId;
import com.home.server.model.members.MembersCommon;
import com.home.server.model.players.Players;

public interface ICocService {
    LocationId getLocations(Token token, String locationId);

    Players getPlayers(Token token, String playerId);

    ListResult<MembersCommon> getMembers(Token token, String tag);

    MyIp getMyIp(Token token);
}
