package com.home.server.model.players;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Players {
    private String tag;
    private String name;
    private Long townHallLevel;
    private Long expLevel;
    private Long trophies;
    private Long bestTrophies;
    private Long warStars;
    private Long attackWins;
    private Long defenseWins;
    private Long builderHallLevel;
    private Long versusTrophies;
    private Long bestVersusTrophies;
    private Long versusBattleWins;
    private String role;
    private Long donations;
    private Long donationsReceived;
    private Clan clan;
    private League league;
    private List<Achievements> achievements;
    private Long versusBattleWinCount;
    private List<Labels> labels;
    private List<Troops> troops;
    private List<Heroes> heroes;
    private List<Spells> spells;
}
