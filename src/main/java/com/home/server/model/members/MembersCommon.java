package com.home.server.model.members;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MembersCommon {
    private String tag;
    private String name;
    private String role;
    private Integer expLevel;
    private League league;
    private Integer trophies;
    private Integer versusTrophies;
    private Integer clanRank;
    private Integer previousClanRank;
    private Integer donations;
    private Integer donationsReceived;
}
