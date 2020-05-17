package com.home.server.service;

import com.home.server.model.ListResult;
import com.home.server.model.MyIp;
import com.home.server.model.developer.CocToken;
import com.home.server.model.locations.LocationId;
import com.home.server.model.members.MembersCommon;
import com.home.server.model.players.Players;

public interface ICocService {

    LocationId getLocations(CocToken token, String locationId);

    Players getPlayers(CocToken token, String playerId);

    ListResult<MembersCommon> getMembers(CocToken token, String tag);

    MyIp getMyIp();
}
